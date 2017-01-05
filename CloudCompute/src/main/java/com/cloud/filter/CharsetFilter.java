package com.cloud.filter;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class CharsetFilter implements Filter{

    private String charset;
    private boolean flag;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // TODO Auto-generated method stub
        if(flag && null!=charset)
        {
            request.setCharacterEncoding(charset);
            response.setCharacterEncoding(charset);
        }
        chain.doFilter(request, response);

    }

    @Override
    public void init(FilterConfig config)throws ServletException
    {
        this.charset = config.getInitParameter("charset");
        this.flag = "true".equals(config.getInitParameter("flag"));

    }

}
