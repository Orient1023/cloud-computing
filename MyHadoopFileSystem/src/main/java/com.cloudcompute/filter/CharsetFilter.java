package com.cloudcompute.filter;

import javax.servlet.*;
import java.io.IOException;

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
