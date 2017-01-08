package com.cloudcompute.servlet;


import com.cloudcompute.myhadoopUtil.HadoopHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by qi on 2017/1/3.
 */
@WebServlet(urlPatterns = {"/deleteFile"},name = "ServletDeletaFile")
public class ServletDeletaFile extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String filePath = request.getParameter("filePath");
        if(filePath == null || filePath.equals("")==true)
            return;
        HadoopHelper.deleteFile(filePath);
        response.sendRedirect("/goToHadoopDir");
    }
}
