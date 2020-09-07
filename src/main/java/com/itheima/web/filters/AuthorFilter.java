//package com.itheima.web.filters;
//
//
//import javax.servlet.*;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.annotation.WebInitParam;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import java.io.IOException;
//
//@WebFilter(value = "/*")
//public class AuthorFilter implements Filter {
//
//    private FilterConfig filterConfig;
//
//    /**
//     * 初始化方法，获取过滤器的配置对象
//     *
//     * @param filterConfig
//     * @throws ServletException
//     */
//
//    public void init(FilterConfig filterConfig) throws ServletException {
//        this.filterConfig = filterConfig;
//    }
//
//
//    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
//        //1.定义和协议相关的请求和响应对象
//        HttpServletRequest request;
//        HttpServletResponse response;
//        HttpSession session;
//        try {
//            //2.把参数转换成协议相关的对象
//            request = (HttpServletRequest) req;
//            response = (HttpServletResponse) resp;
//            session = request.getSession();
//
//            //1. 获取本次操作
//            //不需要权限访问的统统放行
//            String url = request.getRequestURI();
//            if (url.endsWith(".css")
//                    || url.endsWith(".png")
//                    || url.endsWith(".jpg")
//                    || url.endsWith(".index.jsp")
//                    || url.endsWith(".login.jsp")
//                    || url.endsWith("unauthorized.jsp")
//            ) {
//                chain.doFilter(request, response);
//                return;
//            }
//
//
//            String queryString = request.getQueryString();
//
//
//
//
//            if (queryString != null) {
//
//                if (queryString.endsWith("operation=login")
//                    ||queryString.endsWith("operation=home")
//                    ||queryString.endsWith("operation=logout")
//                ) {
//                    chain.doFilter(request, response);
//                    return;
//                }
//                //1.当前获取到的url
//                url = url.substring(1);
//                int index = queryString.indexOf('&');
//
//                if (index != -1) {
//                    queryString = queryString.substring(0, index);
//                }
//                url = url + "?" + queryString;
//
//
//                //2. 获取到当前登陆人允许的操作
//                String authorStr = session.getAttribute("authorStr").toString();
//
//                System.out.println(url);
//                System.out.println(authorStr);
//
//
//                //3. 比对本次操作是否在登陆人允许的操作范围内
//                //实际上就是看当前url是否再允许的模块之中
//                if (authorStr.contains(url)){
//                    //3.1 如果允许，放行
//
//                }else {
//                    //3.2 不允许跳转到非法访问页
//
//                    response.sendRedirect(request.getContextPath()+"/unauthorized.jsp");
//                    return;
//                }
//            }
//            //6.放行
//            chain.doFilter(request, response);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public void destroy() {
//        //可以做一些清理操作
//    }
//}
//
