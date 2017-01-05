package com.cloud.sevlet;

import com.cloud.util.HdfsHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by qi on 2017/1/3.
 */
@WebServlet(urlPatterns = {"/mkDir"},name = "ServletMkDir")
public class ServletMkDir extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String dirName  = request.getParameter("dirName");
        if(dirName==null)
            return;
        String currentPath = (String)request.getSession().getAttribute("currentPath");
        if(currentPath==null)
            return;
        HdfsHelper.mkdir(currentPath+"/"+dirName);
        response.sendRedirect("/goToHadoopDir");
    }
}
