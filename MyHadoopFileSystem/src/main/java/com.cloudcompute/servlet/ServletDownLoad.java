package com.cloudcompute.servlet;

import com.cloudcompute.myhadoopUtil.HadoopHelper;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by qi on 2016/12/30.
 */
@WebServlet(urlPatterns = {"/fileDownLoad"},name = "ServletDownLoad")
public class ServletDownLoad extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String filename = request.getParameter("filename");
        String filePath = request.getParameter("filePath");
        if(filename==null || filename.equals(""))
            return;
        response.setHeader("content-disposition","attachment; filename="+ transformFileName(request,filename));
        String tempPath = this.getServletContext().getRealPath("/WEB-INF/temp");
        File downLoadFileDir = new File(tempPath);
        if(downLoadFileDir.exists()==false)
        {
            downLoadFileDir.mkdir();
        }
        String aimPath = tempPath+"/"+filename;
        HadoopHelper.readFile(filePath,aimPath);
        File bufferFile = new File(aimPath);
        if(bufferFile.exists()==false)
        {
            System.out.println("缓冲文件失败！");
            return;
        }
        FileInputStream inputStream = new FileInputStream(bufferFile);
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len=inputStream.read(buffer))!=-1)
        {
            response.getOutputStream().write(buffer,0,len);
        }
        inputStream.close();
        if(bufferFile!=null)
            bufferFile.delete();
    }

    public String transformFileName(HttpServletRequest request,String fileName) {
        String filename = fileName;
        Boolean flag = request.getHeader("User-Agent").indexOf("like Gecko") > 0;
        //IE11 User-Agent字符串:Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko
        //IE6~IE10版本的User-Agent字符串:Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.0; Trident/6.0)
        try {


            if (request.getHeader("User-Agent").toLowerCase().indexOf("msie") > 0 || flag) {
                filename = URLEncoder.encode(filename, "UTF-8");//IE浏览器
            } else {
                //先去掉文件名称中的空格,然后转换编码格式为utf-8,保证不出现乱码,
                //这个文件名称用于浏览器的下载框中自动显示的文件名
                filename = new String(filename.replaceAll(" ", "").getBytes("UTF-8"), "ISO8859-1");
                //firefox浏览器
                //firefox浏览器User-Agent字符串:
                //Mozilla/5.0 (Windows NT 6.1; WOW64; rv:36.0) Gecko/20100101 Firefox/36.0
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return  filename;
    }

}
