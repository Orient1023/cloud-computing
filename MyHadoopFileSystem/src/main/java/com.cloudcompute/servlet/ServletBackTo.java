package com.cloudcompute.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by qi on 2017/1/3.
 */
@WebServlet(urlPatterns = "/back",name = "ServletBackTo")
public class ServletBackTo extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String aimPath = request.getParameter("aimPath");
        if(aimPath == null || aimPath.equals("")==true)
            return;
        String finalPath=aimPath;
        request.getSession().setAttribute("currentPath",finalPath);
        response.sendRedirect("/goToHadoopDir");
    }
}
