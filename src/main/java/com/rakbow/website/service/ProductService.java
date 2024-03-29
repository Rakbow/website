package com.rakbow.website.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.rakbow.website.controller.interceptor.AuthorityInterceptor;
import com.rakbow.website.dao.ProductMapper;
import com.rakbow.website.data.ApiInfo;
import com.rakbow.website.data.SearchResult;
import com.rakbow.website.data.dto.QueryParams;
import com.rakbow.website.data.vo.product.ProductVOAlpha;
import com.rakbow.website.entity.Product;
import com.rakbow.website.util.common.DataFinder;
import com.rakbow.website.util.common.DateUtil;
import com.rakbow.website.util.common.RedisUtil;
import com.rakbow.website.util.convertMapper.entity.ProductVOMapper;
import com.rakbow.website.util.entity.ProductUtil;
import com.rakbow.website.util.file.QiniuFileUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2022-08-20 2:02
 * @Description:
 */
@Service
public class ProductService {

    //region ------注入实例------
    @Resource
    private ProductMapper productMapper;
    @Resource
    private QiniuFileUtil qiniuFileUtil;
    @Resource
    private RedisUtil redisUtil;

    private final ProductVOMapper productVOMapper = ProductVOMapper.INSTANCES;
    //endregion

    //region ------基础增删改查------

    //新增作品
//    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
//    public String addProduct(Product product) {
//        int id = productMapper.addProduct(product);
//        return String.format(ApiInfo.INSERT_DATA_SUCCESS, Entity.PRODUCT.getNameZh());
//    }

    //通过id查找作品
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class, readOnly = true)
    public Product getProduct(int id) {
        return productMapper.getProduct(id, true);
    }

    /**
     * 根据Id获取Product,需要判断权限
     *
     * @param id id
     * @return Product
     * @author rakbow
     */
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class, readOnly = true)
    public Product getProductWithAuth(int id) {
        if(AuthorityInterceptor.isSenior()) {
            return productMapper.getProduct(id, true);
        }
        return productMapper.getProduct(id, false);
    }

    //更新作品信息
//    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
//    public String updateProduct(int id, Product product) {
//        productMapper.updateProduct(id, product);
//        return String.format(ApiInfo.UPDATE_DATA_SUCCESS, Entity.PRODUCT.getNameZh());
//    }

    //删除作品
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public int deleteProduct(int id) {
        deleteAllProductImages(id);
        return productMapper.deleteProduct(id);
    }

    //通过系列id获取所有作品的数组，供前端选项用
    public JSONArray getProductSet(List<Integer> franchises, int entityType, String lang) {

        JSONArray productSet = new JSONArray();

        if (franchises.size() != 0) {
            List<Integer> categories = ProductUtil.getCategoriesByEntityType(entityType);
            List<Product> products = productMapper.getProductsByFilter(null, null, franchises, categories,
                    false, null, -1, 0, 0);
            products.forEach(product -> {
                JSONObject jo = new JSONObject();
                jo.put("value", product.getId());
                if(StringUtils.equals(lang, Locale.CHINESE.getLanguage())) {
//                    jo.put("label", product.getNameZh() + "(" + ProductCategory.getNameById(product.getCategory(), lang) + ")");
                }
                if(StringUtils.equals(lang, Locale.ENGLISH.getLanguage())) {
//                    jo.put("label", product.getNameEn() + "(" + ProductCategory.getNameById(product.getCategory(), lang) + ")");
                }

                productSet.add(jo);
            });
        }
        return productSet;
    }

    //endregion

    //region ------数据处理------

    /**
     * 检测数据合法性
     *
     * @param productJson json
     * @return string类型错误消息，若为空则数据检测通过
     * @author rakbow
     */
    public String checkProductJson(JSONObject productJson) {
        if (StringUtils.isBlank(productJson.getString("name"))) {
            return ApiInfo.PRODUCT_NAME_EMPTY;
        }
        if (StringUtils.isBlank(productJson.getString("nameZh"))) {
            return ApiInfo.PRODUCT_NAME_ZH_EMPTY;
        }
        if (StringUtils.isBlank(productJson.getString("releaseDate"))) {
            return ApiInfo.PRODUCT_RELEASE_DATE_EMPTY;
        }
        if (StringUtils.isBlank(productJson.getString("franchise"))) {
            return ApiInfo.PRODUCT_FRANCHISE_EMPTY;
        }
        if (StringUtils.isBlank(productJson.getString("category"))) {
            return ApiInfo.PRODUCT_CATEGORY_EMPTY;
        }
        return "";
    }

    /**
     * 更新关联组织信息
     *
     * @param id            id
     * @param organizations 关联组织json数据
     * @author rakbow
     */
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public String updateProductOrganizations(int id, String organizations) {
        productMapper.updateProductOrganizations(id, organizations, DateUtil.NOW_TIMESTAMP);
        return ApiInfo.UPDATE_PRODUCT_ORGANIZATIONS_SUCCESS;
    }

    /**
     * 更新Staff
     *
     * @param id     作品id
     * @param staffs staff相关信息json数据
     * @author rakbow
     */
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public String updateProductStaffs(int id, String staffs) {
        productMapper.updateProductStaffs(id, staffs, DateUtil.NOW_TIMESTAMP);
        return ApiInfo.UPDATE_PRODUCT_STAFFS_SUCCESS;
    }

    /**
     * 删除该作品所有图片
     *
     * @param id 作品id
     * @author rakbow
     */
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public void deleteAllProductImages(int id) {
        Product product = getProduct(id);
        qiniuFileUtil.commonDeleteAllFiles(JSON.parseArray(product.getImages()));

    }

    /**
     * 获取相关作品
     *
     * @param productId 作品id
     * @return list
     * @author rakbow
     */
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class, readOnly = true)
    public List<ProductVOAlpha> getRelatedProducts(int productId) {
        List<ProductVOAlpha> relatedProducts;

        Product product = getProduct(productId);

        List<Integer> franchises = new ArrayList<>();
        franchises.add(product.getFranchise());

        List<Integer> categories = new ArrayList<>();
        categories.add(product.getCategory());

        List<Product> sameFranchiseProducts = productMapper.getProductsByFilter
                (null, null, franchises, categories, false, null, -1, 0, 0);
        List<Product> products = DataFinder.findProductsByClassification(product.getCategory(), sameFranchiseProducts);

        products.removeIf(it -> it.getId() == productId);

        relatedProducts = productVOMapper.product2VOAlpha(products);

        if (relatedProducts.size() > 5) {
            relatedProducts = relatedProducts.subList(0, 5);
        }

        return relatedProducts;
    }

    //endregion

    //region ------特殊查询------

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class, readOnly = true)
    public SearchResult getProductsByFilter(QueryParams param) {

        JSONObject filter = param.getFilters();

        String name = filter.getJSONObject("name").getString("value");
        String nameZh = filter.getJSONObject("nameZh").getString("value");
        List<Integer> franchises = filter.getJSONObject("franchise").getList("value", Integer.class);
        List<Integer> categories = filter.getJSONObject("category").getList("value", Integer.class);

        List<Product> products = productMapper.getProductsByFilter(name, nameZh, franchises, categories, AuthorityInterceptor.isSenior(),
                param.getSortField(), param.getSortOrder(), param.getFirst(), param.getRows());
        int total = productMapper.getProductsRowsByFilter(name, nameZh, franchises, categories, AuthorityInterceptor.isSenior());

        return new SearchResult(total, products);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class, readOnly = true)
    public List<ProductVOAlpha> getProductsByFranchiseId(int franchiseId) {

        List<Integer> franchise = new ArrayList<>();
        franchise.add(franchiseId);

        List<Product> products = productMapper.getProductsByFilter(null, null, franchise,
                null, false, "releaseDate", 1, 0, 0);

        return productVOMapper.product2VOAlpha(products);
    }

    //endregion

    //region ------图片操作------
    //endregion

}
