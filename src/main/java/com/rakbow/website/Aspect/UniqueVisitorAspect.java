package com.rakbow.website.aspect;

import com.rakbow.website.util.common.CommonUtil;
import com.rakbow.website.util.common.CookieUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2023-02-21 22:23
 * @Description:
 */
@Component
@Aspect
public class UniqueVisitorAspect {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Pointcut("@annotation(com.rakbow.website.annotation.UniqueVisitor)")
    private void pointcut() {}

    @Before("pointcut()")
    public void before() {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();

        // 获取请求体 request
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        // 获取响应体 response
        HttpServletResponse response = ((ServletRequestAttributes) requestAttributes).getResponse();

        String visitToken = CookieUtil.getValue(request, "visit_token");
        if (ObjectUtils.isEmpty(visitToken)){
            //生成visitToken,并返回
            visitToken = CommonUtil.generateUUID();
            Cookie cookie = new Cookie("visit_token", visitToken);
            cookie.setPath(contextPath);
            assert response != null;
            response.addCookie(cookie);
        }
    }

}
