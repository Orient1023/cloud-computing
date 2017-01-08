package com.cloudcompute.servlet;


import com.cloudcompute.model.HadoopFileStatus;
import com.cloudcompute.myhadoopUtil.HadoopHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by qi on 2017/1/3.
 */
@WebServlet(urlPatterns = {"/goToHadoopDir"},name = "ServletGoDir")
public class ServletGoDir extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String currentPath = (String)request.getSession().getAttribute("currentPath");
        if(currentPath == null) {
            currentPath = HadoopHelper.rootPath;
            request.getSession().setAttribute("currentPath", currentPath);
        }

        String dir = request.getParameter("dir");
        if(dir!=null && dir.equals("")==false)
        {
            currentPath = currentPath +"/"+dir;
        }

        String refresh = (String) request.getSession().getAttribute("refresh");
        if(refresh!=null && refresh.equals("true"))
        {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            request.setAttribute("requestRefresh","true");
            request.getSession().setAttribute("refresh","false");
        }

        List<HadoopFileStatus> hadoopFileStatusList = HadoopHelper.listHadoopFileStatus(currentPath);
        request.setAttribute("hadoopFileStatusList",hadoopFileStatusList);
        request.getSession().setAttribute("currentPath",currentPath);
        request.getRequestDispatcher("/WEB-INF/pages/main.jsp").forward(request,response);
    }
}
