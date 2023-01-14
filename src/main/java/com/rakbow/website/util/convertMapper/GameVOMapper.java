package com.rakbow.website.util.convertMapper;

import com.alibaba.fastjson2.JSON;
import com.rakbow.website.data.emun.common.Region;
import com.rakbow.website.data.emun.game.GamePlatform;
import com.rakbow.website.data.emun.game.ReleaseType;
import com.rakbow.website.data.segmentImagesResult;
import com.rakbow.website.data.vo.game.GameVO;
import com.rakbow.website.data.vo.game.GameVOAlpha;
import com.rakbow.website.data.vo.game.GameVOBeta;
import com.rakbow.website.entity.Game;
import com.rakbow.website.util.common.CommonUtils;
import com.rakbow.website.util.entity.FranchiseUtils;
import com.rakbow.website.util.image.CommonImageUtils;
import com.rakbow.website.util.entity.ProductUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2023-01-12 10:45
 * @Description: Game VO转换接口
 */
@Mapper(componentModel = "spring")
public interface GameVOMapper {

    /**
     * 获取该类自动生成的实现类的实例
     * 接口中的属性都是 public static final 的 方法都是public abstract的
     */
    GameVOMapper INSTANCES = Mappers.getMapper(GameVOMapper.class);

    /**
     * Game转VO对象，用于详情页面，转换量最大的
     *
     * @param game 游戏
     * @return GameVO
     * @author rakbow
     */
    default GameVO game2VO(Game game) {
        if (game == null) {
            return null;
        }

        GameVO gameVO = new GameVO();

        //基础信息
        gameVO.setId(game.getId());
        gameVO.setName(game.getName());
        gameVO.setNameZh(game.getNameZh());
        gameVO.setNameEn(game.getNameEn());
        gameVO.setBarcode(game.getBarcode());
        gameVO.setReleaseDate(CommonUtils.dateToString(game.getReleaseDate()));
        gameVO.setHasBonus(game.getHasBonus() == 1);
        gameVO.setRemark(game.getRemark());

        //关联信息
        gameVO.setProducts(ProductUtils.getProductList(game.getProducts()));
        gameVO.setFranchises(FranchiseUtils.getFranchiseList(game.getFranchises()));

        //复杂字段
        gameVO.setRegion(Region.getRegionJson(game.getRegion()));
        gameVO.setPlatform(GamePlatform.getGamePlatformJson(game.getPlatform()));
        gameVO.setReleaseType(ReleaseType.getReleaseTypeJson(game.getReleaseType()));
        gameVO.setOrganizations(JSON.parseArray(game.getOrganizations()));
        gameVO.setStaffs(JSON.parseArray(game.getStaffs()));
        gameVO.setDescription(game.getDescription());
        gameVO.setBonus(game.getBonus());

        //将图片分割处理
        segmentImagesResult segmentImages = CommonImageUtils.segmentImages(game.getImages(), 200);
        gameVO.setCover(segmentImages.cover);
        gameVO.setImages(segmentImages.images);
        gameVO.setDisplayImages(segmentImages.displayImages);
        gameVO.setOtherImages(segmentImages.otherImages);

        //审计字段
        gameVO.setAddedTime(CommonUtils.timestampToString(game.getAddedTime()));
        gameVO.setEditedTime(CommonUtils.timestampToString(game.getEditedTime()));
        gameVO.set_s(game.get_s());

        return gameVO;
    }

    /**
     * Game转VO，供list和index界面使用，信息量较少
     *
     * @param game 游戏
     * @return GameVOAlpha
     * @author rakbow
     */
    default GameVOAlpha game2VOAlpha(Game game) {
        if (game == null) {
            return null;
        }

        GameVOAlpha gameVOAlpha = new GameVOAlpha();

        //基础信息
        gameVOAlpha.setId(game.getId());
        gameVOAlpha.setName(game.getName());
        gameVOAlpha.setNameZh(game.getNameZh());
        gameVOAlpha.setNameEn(game.getNameEn());
        gameVOAlpha.setBarcode(game.getBarcode());
        gameVOAlpha.setReleaseDate(CommonUtils.dateToString(game.getReleaseDate()));
        gameVOAlpha.setHasBonus(game.getHasBonus() == 1);
        gameVOAlpha.setRemark(game.getRemark());

        //关联信息
        gameVOAlpha.setProducts(ProductUtils.getProductList(game.getProducts()));
        gameVOAlpha.setFranchises(FranchiseUtils.getFranchiseList(game.getFranchises()));

        //复杂字段
        gameVOAlpha.setRegion(Region.getRegionJson(game.getRegion()));
        gameVOAlpha.setPlatform(GamePlatform.getGamePlatformJson(game.getPlatform()));
        gameVOAlpha.setReleaseType(ReleaseType.getReleaseTypeJson(game.getReleaseType()));

        //将图片分割处理
        gameVOAlpha.setCover(CommonImageUtils.generateCover(game.getImages()));

        //审计字段
        gameVOAlpha.setAddedTime(CommonUtils.timestampToString(game.getAddedTime()));
        gameVOAlpha.setEditedTime(CommonUtils.timestampToString(game.getEditedTime()));
        gameVOAlpha.set_s(game.get_s());

        return gameVOAlpha;
    }

    /**
     * 列表转换, Game转VO对象，供list和index界面使用，信息量较少
     *
     * @param games 游戏列表
     * @return List<GameVOAlpha>
     * @author rakbow
     */
    default List<GameVOAlpha> game2VOAlpha(List<Game> games) {
        List<GameVOAlpha> gameVOAlphas = new ArrayList<>();

        if (!games.isEmpty()) {
            games.forEach(game -> gameVOAlphas.add(game2VOAlpha(game)));
        }

        return gameVOAlphas;
    }

    /**
     * Game转VO对象，信息量最少
     *
     * @param game 游戏
     * @return GameVOBeta
     * @author rakbow
     */
    default GameVOBeta game2VOBeta(Game game) {
        if (game == null) {
            return null;
        }

        GameVOBeta gameVOBeta = new GameVOBeta();

        //基础信息
        gameVOBeta.setId(game.getId());
        gameVOBeta.setName(game.getName());
        gameVOBeta.setNameZh(game.getNameZh());
        gameVOBeta.setNameEn(game.getNameEn());
        gameVOBeta.setReleaseDate(CommonUtils.dateToString(game.getReleaseDate()));

        //复杂字段
        gameVOBeta.setRegion(Region.getRegionJson(game.getRegion()));
        gameVOBeta.setPlatform(GamePlatform.getGamePlatformJson(game.getPlatform()));
        gameVOBeta.setReleaseType(ReleaseType.getReleaseTypeJson(game.getReleaseType()));

        //图片
        gameVOBeta.setCover(CommonImageUtils.generateThumbCover(game.getImages(), 50));

        //审计字段
        gameVOBeta.setAddedTime(CommonUtils.timestampToString(game.getAddedTime()));
        gameVOBeta.setEditedTime(CommonUtils.timestampToString(game.getEditedTime()));

        return gameVOBeta;
    }

    /**
     * 列表转换, Game转VO对象，信息量最少
     *
     * @param games 游戏列表
     * @return List<GameVOBeta>
     * @author rakbow
     */
    default List<GameVOBeta> game2VOBeta(List<Game> games) {
        List<GameVOBeta> gameVOBetas = new ArrayList<>();

        if (!games.isEmpty()) {
            games.forEach(game -> gameVOBetas.add(game2VOBeta(game)));
        }

        return gameVOBetas;
    }

}