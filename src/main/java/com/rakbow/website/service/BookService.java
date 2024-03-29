package com.rakbow.website.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.rakbow.website.controller.interceptor.AuthorityInterceptor;
import com.rakbow.website.dao.BookMapper;
import com.rakbow.website.data.ApiInfo;
import com.rakbow.website.data.SearchResult;
import com.rakbow.website.data.dto.QueryParams;
import com.rakbow.website.data.emun.common.Entity;
import com.rakbow.website.data.vo.book.BookVO;
import com.rakbow.website.data.vo.book.BookVOBeta;
import com.rakbow.website.entity.Book;
import com.rakbow.website.util.common.CommonUtil;
import com.rakbow.website.util.common.DateUtil;
import com.rakbow.website.util.common.VisitUtil;
import com.rakbow.website.util.convertMapper.entity.BookVOMapper;
import com.rakbow.website.util.entity.BookUtil;
import com.rakbow.website.util.file.QiniuFileUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2022-12-28 23:45
 * @Description: book业务层
 */
@Service
public class BookService {

    //region ------注入依赖------

    @Resource
    private BookMapper bookMapper;
    @Resource
    private QiniuFileUtil qiniuFileUtil;
    @Resource
    private VisitUtil visitUtil;

    private final BookVOMapper bookVOMapper = BookVOMapper.INSTANCES;

    //endregion

    //region ------CRUD------

    /**
     * 新增图书
     *
     * @param book 新增的图书
     * @author rakbow
     */
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public String addBook(Book book) {
        int id = bookMapper.addBook(book);
        return String.format(ApiInfo.INSERT_DATA_SUCCESS, Entity.BOOK.getNameZh());
    }

    /**
     * 根据Id获取图书,泛用
     *
     * @param id 图书id
     * @return book
     * @author rakbow
     */
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class, readOnly = true)
    public Book getBook(int id) {
        return bookMapper.getBook(id, true);
    }

     /**
      * 根据Id获取图书,需要判断权限
      *
      * @param id 图书id
      * @return book
      * @author rakbow
      */
     @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class, readOnly = true)
     public Book getBookWithAuth(int id) {
         if(AuthorityInterceptor.isSenior()) {
             return bookMapper.getBook(id, true);
         }
         return bookMapper.getBook(id, false);
     }

    /**
     * 根据Id删除图书
     *
     * @param book 图书
     * @author rakbow
     */
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public void deleteBook(Book book) {
        //删除前先把服务器上对应图片全部删除
        qiniuFileUtil.commonDeleteAllFiles(JSON.parseArray(book.getImages()));
        bookMapper.deleteBook(book.getId());
        visitUtil.deleteVisit(Entity.BOOK.getId(), book.getId());
    }

    /**
     * 更新图书基础信息
     *
     * @param id 图书id
     * @author rakbow
     */
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public String updateBook(int id, Book book) {
        bookMapper.updateBook(id, book);
        return String.format(ApiInfo.UPDATE_DATA_SUCCESS, Entity.BOOK.getNameZh());
    }

    //endregion

    //region ------数据处理------

    public BookVO buildVO(Book book) {
        BookVO VO = bookVOMapper.toVO(book);
        if(AuthorityInterceptor.isJunior()) {
            //可供编辑的editCompanies
            VO.setEditCompanies(JSON.parseArray(book.getCompanies()));
            //可供编辑的editPersonnel
            VO.setEditPersonnel(JSON.parseArray(book.getPersonnel()));
            //可供编辑的editSpecs
            VO.setEditSpecs(JSON.parseArray(book.getSpecs()));
        }
        return VO;
    }

    /**
     * 检测数据合法性
     *
     * @param bookJson bookJson
     * @return string类型错误消息，若为空则数据检测通过
     * @author rakbow
     */
    public String checkBookJson(JSONObject bookJson) {
        if (StringUtils.isBlank(bookJson.getString("title"))) {
            return ApiInfo.BOOK_TITLE_EMPTY;
        }

        if (!StringUtils.isBlank(bookJson.getString("isbn10"))) {
            if (bookJson.getString("isbn10").replaceAll("-", "").length() != 10) {
                return ApiInfo.BOOK_ISBN10_LENGTH_EXCEPTION;
            }
        } else {
            return ApiInfo.BOOK_ISBN10_LENGTH_EXCEPTION;
        }
        if (!StringUtils.isBlank(bookJson.getString("isbn13"))) {
            if (bookJson.getString("isbn13").replaceAll("-", "").length() != 13) {
                return ApiInfo.BOOK_ISBN13_LENGTH_EXCEPTION;
            }
        } else {
            return ApiInfo.BOOK_ISBN13_LENGTH_EXCEPTION;
        }
        if (StringUtils.isBlank(bookJson.getString("publishDate"))) {
            return ApiInfo.BOOK_PUBLISH_DATE_EMPTY;
        }
        if (StringUtils.isBlank(bookJson.getString("bookType"))) {
            return ApiInfo.BOOK_TYPE_EMPTY;
        }
        if (StringUtils.isBlank(bookJson.getString("franchises"))
                || StringUtils.equals(bookJson.getString("franchises"), "[]")) {
            return ApiInfo.FRANCHISES_EMPTY;
        }
        if (StringUtils.isBlank(bookJson.getString("products"))
                || StringUtils.equals(bookJson.getString("products"), "[]")) {
            return ApiInfo.PRODUCTS_EMPTY;
        }
        return "";
    }

    /**
     * 处理前端传送图书数据
     *
     * @param bookJson bookJson
     * @return 处理后的book json格式数据
     * @author rakbow
     */
    public JSONObject handleBookJson(JSONObject bookJson) {

        String[] products = CommonUtil.str2SortedArray(bookJson.getString("products"));
        String[] franchises = CommonUtil.str2SortedArray(bookJson.getString("franchises"));

        bookJson.put("isbn10", bookJson.getString("isbn10").replaceAll("-", ""));
        bookJson.put("isbn13", bookJson.getString("isbn13").replaceAll("-", ""));
        bookJson.put("publishDate", bookJson.getDate("publishDate"));
        bookJson.put("products", "{\"ids\":[" + StringUtils.join(products, ",") + "]}");
        bookJson.put("franchises", "{\"ids\":[" + StringUtils.join(franchises, ",") + "]}");

        return bookJson;
    }

    //endregion

    //region ------更新book数据------

    /**
     * 更新图书作者信息
     *
     * @param id      图书id
     * @param authors 图书的作者信息json数据
     * @author rakbow
     */
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public String updateBookAuthors(int id, String authors) {
        bookMapper.updateBookAuthors(id, authors, DateUtil.NOW_TIMESTAMP);
        return ApiInfo.UPDATE_BOOK_AUTHOR_SUCCESS;
    }

    //endregion

    //region ------特殊查询------

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class, readOnly = true)
    public SearchResult getBooksByFilter(QueryParams param) {

        JSONObject filter = param.getFilters();

        String title = filter.getJSONObject("title").getString("value");
        String isbn10 = filter.getJSONObject("isbn10").getString("value");
        String isbn13 = filter.getJSONObject("isbn13").getString("value");
        String region = filter.getJSONObject("region").getString("value");
        String publishLanguage = filter.getJSONObject("publishLanguage").getString("value");

        int bookType = 100;
        if (filter.getJSONObject("bookType").get("value") != null) {
            bookType = filter.getJSONObject("bookType").getIntValue("value");
        }

        List<Integer> franchises = filter.getJSONObject("franchises").getList("value", Integer.class);
        List<Integer> products = filter.getJSONObject("products").getList("value", Integer.class);
        List<Integer> serials = filter.getJSONObject("serials").getList("value", Integer.class);

        String hasBonus;
        if (filter.getJSONObject("hasBonus").get("value") == null) {
            hasBonus = null;
        } else {
            hasBonus = filter.getJSONObject("hasBonus").getBoolean("value")
                    ? Integer.toString(1) : Integer.toString(0);
        }

        List<Book> books = bookMapper.getBooksByFilter(title, isbn10, isbn13, serials, region, publishLanguage,
                bookType, franchises, products, hasBonus, AuthorityInterceptor.isSenior(), param.getSortField(), param.getSortOrder(), param.getFirst(), param.getRows());

        int total = bookMapper.getBooksRowsByFilter(title, isbn10, isbn13, serials, region, publishLanguage,
                bookType, franchises, products, hasBonus, AuthorityInterceptor.isSenior());

        return new SearchResult(total, books);
    }

    /**
     * 根据作品id获取图书
     *
     * @param productId 作品id
     * @return List<JSONObject>
     * @author rakbow
     */
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class, readOnly = true)
    public List<BookVOBeta> getBooksByProductId(int productId) {
        List<Integer> products = new ArrayList<>();
        products.add(productId);

        List<Book> books = bookMapper.getBooksByFilter(null, null, null, null,
                null, null, 100, null, products, null, false, "publishDate",
                -1,  0, 0);

        return bookVOMapper.book2VOBeta(books);
    }

    /**
     * 获取相关联Book
     *
     * @param id 图书id
     * @return list封装的Book
     * @author rakbow
     */
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class, readOnly = true)
    public List<BookVOBeta> getRelatedBooks(int id) {

        List<Book> result = new ArrayList<>();

//        Book book = getBook(id);
//
//        //该Book包含的作品id
//        List<Integer> productIds = JSONObject.parseObject(book.getProducts()).getList("ids", Integer.class);
//
//        //该系列所有Book
//        List<Book> allBooks = bookMapper.getBooksByFilter(null, null, null, 0,
//                        null, null, 100, CommonUtil.ids2List(book.getFranchises()),
//                        null, null, false, "publishDate", 1, 0, 0)
//                .stream().filter(tmpBook -> tmpBook.getId() != book.getId()).collect(Collectors.toList());
//
//        List<Book> queryResult = allBooks.stream().filter(tmpBook ->
//                StringUtils.equals(tmpBook.getProducts(), book.getProducts())
//                        && StringUtils.equals(tmpBook.getPublisher(), book.getPublisher())).collect(Collectors.toList());
//
//        if (queryResult.size() > 5) {//结果大于5
//            result.addAll(queryResult.subList(0, 5));
//        } else if (queryResult.size() == 5) {//结果等于5
//            result.addAll(queryResult);
//        } else if (queryResult.size() > 0) {//结果小于5不为空
//            List<Book> tmp = new ArrayList<>(queryResult);
//
//            if (productIds.size() > 1) {
//                List<Book> tmpQueryResult = allBooks.stream().filter(tmpBook ->
//                        JSONObject.parseObject(tmpBook.getProducts()).getList("ids", Integer.class)
//                                .contains(productIds.get(1))).collect(Collectors.toList());
//
//                if (tmpQueryResult.size() >= 5 - queryResult.size()) {
//                    tmp.addAll(tmpQueryResult.subList(0, 5 - queryResult.size()));
//                } else if (tmpQueryResult.size() > 0 && tmpQueryResult.size() < 5 - queryResult.size()) {
//                    tmp.addAll(tmpQueryResult);
//                }
//            }
//            result.addAll(tmp);
//        } else {
//            List<Book> tmp = new ArrayList<>(queryResult);
//            for (int productId : productIds) {
//                tmp.addAll(
//                        allBooks.stream().filter(tmpBook ->
//                                JSONObject.parseObject(tmpBook.getProducts()).getList("ids", Integer.class)
//                                        .contains(productId)).collect(Collectors.toList())
//                );
//            }
//            result = CommonUtil.removeDuplicateList(tmp);
//            if (result.size() >= 5) {
//                result = result.subList(0, 5);
//            }
//        }

        return bookVOMapper.book2VOBeta(CommonUtil.removeDuplicateList(result));
    }

    //endregion

    //region other

    /**
     * isbn互相转换
     *
     * @param label,isbn 转换方式,isbn
     * @return isbn
     * @author rakbow
     */
    public String getISBN(String label, String isbn) throws Exception {

        isbn = isbn.replaceAll("-", "");

        if(StringUtils.equals(label, "isbn13")) {
            if(isbn.length() != 10) {
                throw new Exception(ApiInfo.BOOK_ISBN10_LENGTH_EXCEPTION);
            }
            return BookUtil.getISBN13(isbn);
        }
        if(StringUtils.equals(label, "isbn10")) {
            if(isbn.length() != 13) {
                throw new Exception(ApiInfo.BOOK_ISBN13_LENGTH_EXCEPTION);
            }
            return BookUtil.getISBN10(isbn);
        }
        return null;
    }

    //endregion

}
