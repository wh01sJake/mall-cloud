package com.intelijake.mall.common.interceptor;

import com.intelijake.mall.pojo.Customer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * ClassName: LoginInterceptor
 * Description:
 * <p>
 * Datetime: 2025/6/28 23:01
 * Author: @Likun.Fang
 * Version: 1.0
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            String requestedWith = request.getHeader("X-Requested-With");
            // Ajax 请求，返回特殊状态码或 JSON
            if ("XMLHttpRequest".equals(requestedWith)) {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json");
                response.getWriter().write("{\"code\":401, \"message\":\"Not Logged In\"}");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else {// 普通请求，直接重定向
                response.sendRedirect("/page/login");
            }
            return false;
        }

        return true;
    }
}