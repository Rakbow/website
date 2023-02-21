package com.rakbow.website.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.rakbow.website.dao.ProductMapper;
import com.rakbow.website.data.ApiInfo;
import com.rakbow.website.data.RedisCacheConstant;
import com.rakbow.website.data.emun.common.EntityType;
import com.rakbow.website.data.SearchResult;
import com.rakbow.website.data.emun.product.ProductCategory;
import com.rakbow.website.data.vo.product.ProductVOAlpha;
import com.rakbow.website.entity.Product;
import com.rakbow.website.entity.User;
import com.rakbow.website.util.entity.ProductUtil;
import com.rakbow.website.util.common.*;
import com.rakbow.website.util.convertMapper.ProductVOMapper;
import com.rakbow.website.util.file.QiniuFileUtil;
import com.rakbow.website.util.file.QiniuImageUtil;
import com.rakbow.website.util.common.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2022-08-20 2:02
 * @Description:
 */
@Service
public class ProductService {

    //region ------注入实例------
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private QiniuImageUtil qiniuImageUtil;
    @Autowired
    private QiniuFileUtil qiniuFileUtil;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private VisitUtil visitUtil;

    private final ProductVOMapper productVOMapper = ProductVOMapper.INSTANCES;
    //endregion

    //region ------基础增删改查------

    //新增作品
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public String addProduct(Product product) {
        int id = productMapper.addProduct(product);
        visitUtil.addVisit(EntityType.PRODUCT.getId(), id);
        return String.format(ApiInfo.INSERT_DATA_SUCCESS, EntityType.PRODUCT.getNameZh());
    }

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
    public Product getProductWithAuth(int id, int userAuthority) {
        if(userAuthority > 2) {
            return productMapper.getProduct(id, true);
        }
        return productMapper.getProduct(id, false);
    }

    //获取所有作品
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class, readOnly = true)
    public List<Product> getAllProduct() {
        return productMapper.getAll();
    }

    //更新作品信息
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public String updateProduct(int id, Product product) {
        productMapper.updateProduct(id, product);
        return String.format(ApiInfo.UPDATE_DATA_SUCCESS, EntityType.PRODUCT.getNameZh());
    }

    //删除作品
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public int deleteProduct(int id) {
        deleteAllProductImages(id);
        return productMapper.deleteProduct(id);
    }

    //通过系列Id获取所有作品的数组，供前端选项用
    public List<JSONObject> getProductSet(int franchiseId, int entityType) {

        List<Integer> franchises = new ArrayList<>();
        franchises.add(franchiseId);

        List<Integer> categories = ProductUtil.getCategoriesByEntityType(entityType);

        List<JSONObject> productSet = new ArrayList<>();
        productMapper.getProductsByFilter(null, null, franchises, categories, false,
                null, -1, 0, 0).forEach(product -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("label", product.getNameZh() + "(" + ProductCategory.getNameZhByIndex(product.getCategory()) + ")");
            jsonObject.put("value", product.getId());
            productSet.add(jsonObject);
        });
        return productSet;
    }

    //通过系列Id获取所有作品的数组，供前端选项用
    public JSONArray getProductSet(List<Integer> franchises, int entityType) {

        JSONArray productSet = new JSONArray();

        if (franchises.size() != 0) {
            List<Integer> categories = ProductUtil.getCategoriesByEntityType(entityType);
            productMapper.getProductsByFilter(null, null, franchises, categories, false,
                    null, -1, 0, 0).forEach(product -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("value", product.getId());
                jsonObject.put("label", product.getNameZh() + "(" + ProductCategory.getNameZhByIndex(product.getCategory()) + ")");
                productSet.add(jsonObject);
            });
        }
        return productSet;
    }

    //endregion

    //region ------数据处理------

    /**
     * json对象转Product，以便保存到数据库
     *
     * @param productJson json
     * @return Album
     * @author rakbow
     */
    public Product json2Product(JSONObject productJson) {
        return JSON.to(Product.class, productJson);
    }

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
        productMapper.updateProductOrganizations(id, organizations, new Timestamp(System.currentTimeMillis()));
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
        productMapper.updateProductStaffs(id, staffs, new Timestamp(System.currentTimeMillis()));
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
        JSONArray images = JSON.parseArray(product.getImages());
        qiniuFileUtil.commonDeleteAllFiles(images);

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
    public SearchResult getProductsByFilter(JSONObject queryParams, int userAuthority) {

        JSONObject filter = queryParams.getJSONObject("filters");

        String sortField = queryParams.getString("sortField");
        int sortOrder = queryParams.getIntValue("sortOrder");
        int first = queryParams.getIntValue("first");
        int row = queryParams.getIntValue("rows");

        String name = filter.getJSONObject("name").getString("value");
        String nameZh = filter.getJSONObject("nameZh").getString("value");
        List<Integer> franchises = filter.getJSONObject("franchise").getList("value", Integer.class);
        List<Integer> categories = filter.getJSONObject("category").getList("value", Integer.class);

        List<Product> products = productMapper.getProductsByFilter(name, nameZh, franchises, categories, userAuthority > 2,
                sortField, sortOrder, first, row);
        int total = productMapper.getProductsRowsByFilter(name, nameZh, franchises, categories, userAuthority > 2);

        return new SearchResult(total, products);
    }

    /**
     * 刷新Redis缓存中的products数据
     *
     * @author rakbow
     */
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class, readOnly = true)
    public void refreshRedisProducts() {

        JSONArray products = new JSONArray();
        getAllProduct().forEach(product -> {
            JSONObject jo = new JSONObject();
            jo.put("value", product.getId());
            jo.put("label", product.getNameZh() + "(" +
                    ProductCategory.getNameZhByIndex(product.getCategory()) + ")");
            products.add(jo);
        });

        redisUtil.set(RedisCacheConstant.PRODUCT_SET, products);
        //缓存时间1个月
        redisUtil.expire(RedisCacheConstant.PRODUCT_SET, 2592000);

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
