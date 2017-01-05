package com.cloud.sevlet;

import com.cloud.util.HdfsHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by qi on 2017/1/5.
 */
@WebServlet(urlPatterns = "/renameFile",name = "ServletRename")
public class ServletRename extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
   //     request.setCharacterEncoding("UTF-8");
    //    response.setContentType("text/html;charset=UTF-8");
        String fromPath = request.getParameter("fromPath");
        if(fromPath == null || fromPath.equals("")==true)
            return;
        String aimName = request.getParameter("aimName");
        if(aimName==null || aimName.equals("")==true)
            return;
        HdfsHelper.renameFile(fromPath,aimName);
        response.sendRedirect("/goToHadoopDir");
    }
}
