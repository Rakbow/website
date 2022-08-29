package com.rakbow.blog;

import com.rakbow.blog.entity.User;
import com.rakbow.blog.service.AlbumService;
import com.rakbow.blog.service.UserService;
import com.rakbow.blog.util.CommonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Map;

/**
 * @Project_name: blog
 * @Author: Rakbow
 * @Create: 2022-08-16 22:09
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = BlogApplication.class)
public class userTest {

    @Autowired
    private UserService userService;

    @Autowired
    private AlbumService albumService;

    @Test
    public void registerTest() {
        User user = new User();
        user.setUsername("guest");
        user.setPassword("guest123");
        user.setEmail("944828032@qq.com");
        Map<String, Object> map = userService.register(user);
        if (map != null) {
            for (String key : map.keySet()) {
                System.out.println("key= " + key + " and value= " + map.get(key));
            }
        }
    }

    @Test
    public void testTime() throws ParseException {
        String date = "2006/12/01";
        System.out.println(CommonUtil.stringToDate(date, "yyyy/MM/dd"));
        System.out.println(new Timestamp(System.currentTimeMillis()));
    }

    @Test
    public void testAlbum() {
        System.out.println(albumService.findAlbumById(122).get_s());
    }

}
