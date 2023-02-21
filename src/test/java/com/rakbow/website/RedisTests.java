package com.rakbow.website;

import com.alibaba.fastjson2.JSON;
import com.rakbow.website.dao.*;
import com.rakbow.website.entity.*;
import com.rakbow.website.service.EntityService;
import com.rakbow.website.service.FranchiseService;
import com.rakbow.website.service.ProductService;
import com.rakbow.website.service.UserService;
import com.rakbow.website.util.common.RedisUtil;
import com.rakbow.website.util.common.VisitUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = WebSiteApplication.class)
public class RedisTests {

    @Autowired
    private UserService userService;
    @Autowired
    private EntityService entityService;
    @Autowired
    private FranchiseService franchiseService;
    @Autowired
    private ProductService productService;
    @Autowired
    private AlbumMapper albumMapper;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private DiscMapper discMapper;
    @Autowired
    private GameMapper gameMapper;
    @Autowired
    private MerchMapper merchMapper;
    @Autowired
    private MusicMapper musicMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private FranchiseMapper franchiseMapper;
    @Autowired
    private VisitUtil visitUtil;
    @Autowired
    private RedisUtil redisUtil;

    @Test
    public void refreshData() {
        productService.refreshRedisProducts();
        franchiseService.refreshRedisFranchises();
        // entityService.refreshRedisEmunData();
    }

    @Test
    public void redisTest1() {

        List<Album> albums = albumMapper.getAll();
        albums.forEach(album -> {
            // visitUtils.setEntityVisitRanking(1, album.getId(), Long.parseLong(redisUtil.get(key).toString()));
            // visitUtils.setEntityVisitRanking(1, album.getId(), 0);
            visitUtil.addVisit(1, album.getId());
        });
        List<Disc> discs = discMapper.getAll();
        discs.forEach(disc -> {
            // visitUtils.setEntityVisitRanking(2, disc.getId(), 0);
            visitUtil.addVisit(2, disc.getId());
        });
        List<Book> books = bookMapper.getAll();
        books.forEach(book -> {
            // visitUtils.setEntityVisitRanking(3, book.getId(), 0);
            visitUtil.addVisit(3, book.getId());
        });
        List<Merch> merchs = merchMapper.getAll();
        merchs.forEach(merch -> {
            // visitUtils.setEntityVisitRanking(4, merch.getId(), 0);
            visitUtil.addVisit(4, merch.getId());
        });
        List<Game> games = gameMapper.getAll();
        games.forEach(game -> {
            // visitUtils.setEntityVisitRanking(5, game.getId(), 0);
            visitUtil.addVisit(5, game.getId());
        });
        List<Franchise> franchises = franchiseMapper.getAll();
        franchises.forEach(franchise -> {
            // visitUtils.setEntityVisitRanking(6, franchise.getId(), 0);
            visitUtil.addVisit(6, franchise.getId());
        });
        List<Product> products = productMapper.getAll();
        products.forEach(product -> {
            // visitUtils.setEntityVisitRanking(7, product.getId(), 0);
            visitUtil.addVisit(7, product.getId());
        });
        List<Music> musics = musicMapper.getAll();
        musics.forEach(music -> {
            // visitUtils.setEntityVisitRanking(9, music.getId(), 0);
            visitUtil.addVisit(9, music.getId());
        });
    }

    @Test
    public void deleteAllCache() {
        List<String> keys = redisUtil.keys("VISIT_*");
        keys.forEach(key -> {
            redisUtil.delete(key);
        });
    }

    @Test
    public void redisTest3() {
        System.out.println(JSON.toJSON(visitUtil.getEntityVisitRanking(1, 10)));
    }

}
