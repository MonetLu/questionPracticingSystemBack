package com.itheima.web.filters;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(value = "/*")
public class AuthorFilter implements Filter {

    private FilterConfig filterConfig;

    /**
     * 初始化方法，获取过滤器的配置对象
     *
     * @param filterConfig
     * @throws ServletException
     */

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }


    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request;
        HttpServletResponse response;
        HttpSession session;

        try {
            request = (HttpServletRequest) req;
            response = (HttpServletResponse) resp;
            session = request.getSession();

            //1. 获取本次操做
            String url = request.getRequestURI();
            if (url.endsWith(".css")
                    || url.endsWith(".png")
                    || url.endsWith(".jpg")
                    || url.endsWith(".js")
                    || url.endsWith("index.jsp")
                    || url.endsWith("login.jsp")
                    || url.endsWith("/")
                    || url.endsWith("unauthorized.jsp")) {
                chain.doFilter(request, response);
                return;
            }
            String queryString = request.getQueryString();
            if (queryString == null || queryString.endsWith("operation=login")
                    || queryString.endsWith("operation=home")
                    || queryString.endsWith("operation=logout")) {
                chain.doFilter(request, response);
                return;
            }

//            System.out.println(url);
//            System.out.println(queryString);

            //1. 当前获取到的url：/system/dept
            //2. 当前获取到的查询参数： operation=list operation=toEdit&id=100
            url = url.substring(1);
            int index = queryString.indexOf('&');
            if (index != -1) {
                queryString = queryString.substring(0, index);
            }
            url = url + "?" + queryString;
            //2. 获取到当前登陆人允许的操做
            String authorStr = session.getAttribute("authorStr").toString();
//TODO: set the links for different functions in different modules
            System.out.println(url);
            System.out.println(authorStr);
            //3. 比对本次操作是否在当前登陆人允许的操做范围内
            if (authorStr.contains(url)) {
                //3.1 如果允许，放行
                chain.doFilter(request, response);
                return;
            } else {
                //3.2 如果不允许，跳转到非法访问页
                response.sendRedirect(request.getContextPath() + "/unauthorized.jsp");
            }

        } catch (Exception e) {

            e.printStackTrace();
        }


    }


    public void destroy() {
        //可以做一些清理操作
    }
}

