package com.test.http_spring.Filter;


import com.test.http_spring.utils.ToolsFunction;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Enumeration;


@WebFilter(urlPatterns = "/**",filterName = "sqlFilter")
@Configuration
public class SqlFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        Enumeration<String> names = request.getParameterNames();
        StringBuilder sql = new StringBuilder();
        //将所有的参数加入拼接字符串
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String[] values = request.getParameterValues(name);
            for (String value : values) {
                sql.append(value);
            }
        }
        //检查token是否有注入
        String token=request.getHeader("token");
        sql.append(token);
        if (ToolsFunction.isSqlInjection(sql.toString())) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        filterChain.doFilter(request, response);
    }
}