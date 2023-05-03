package com.rakbow.website.controller.interceptor;

import com.rakbow.website.entity.LoginTicket;
import com.rakbow.website.entity.User;
import com.rakbow.website.service.UserService;
import com.rakbow.website.util.common.CookieUtil;
import com.rakbow.website.util.common.HostHolder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2022-08-17 23:25
 * @Description:
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Resource
    private UserService userService;

    @Resource
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {

        // 从cookie中获取凭证
        String ticket = CookieUtil.getValue(request, "ticket");


        if (ticket != null) {
            // 查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            // 检查凭证是否有效
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                // 根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                // 在本次请求中持有用户
                hostHolder.setUser(user);
                // // 构建用户认证的结果，并存入securityContext，以便security进行授权
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user, user.getPassword(), userService.getAuthorities(user.getId()));
                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));

            } else if (loginTicket != null) {
                if(loginTicket.getStatus() == 1 || loginTicket.getExpired().before(new Date())){
                    //凭证失效
                    hostHolder.clear();
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, ModelAndView modelAndView) {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) {
        hostHolder.clear();
    }

}
