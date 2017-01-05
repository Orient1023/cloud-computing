package com.cloud.sevlet;

import com.cloud.model.HadoopFileStatus;
import com.cloud.util.HdfsHelper;

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
            currentPath = HdfsHelper.rootPath;
            request.getSession().setAttribute("currentPath", currentPath);
        }

        String dir = request.getParameter("dir");
        if(dir!=null && dir.equals("")==false)
        {
            currentPath = currentPath +"/"+dir;
        }

        List<HadoopFileStatus> hadoopFileStatusList = HdfsHelper.getFilesInDir(currentPath);
        request.setAttribute("hadoopFileStatusList",hadoopFileStatusList);
        request.getSession().setAttribute("currentPath",currentPath);
        request.getRequestDispatcher("/WEB-INF/pages/main.jsp").forward(request,response);
    }
}
