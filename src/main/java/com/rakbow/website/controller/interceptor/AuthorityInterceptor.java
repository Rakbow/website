package com.rakbow.website.controller.interceptor;

import com.rakbow.website.annotation.AdminAuthorityRequired;
import com.rakbow.website.util.common.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2022-09-30 9:51
 * @Description:
 */
public class AuthorityInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            AdminAuthorityRequired adminAuthorityRequired = method.getAnnotation(AdminAuthorityRequired.class);
            if (adminAuthorityRequired != null && hostHolder.getUser() == null) {
                // response.;
                return false;
            }
        }
        return true;
    }
}
