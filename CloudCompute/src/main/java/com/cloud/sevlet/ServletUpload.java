package com.cloud.sevlet;

import com.cloud.util.HdfsHelper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

/**
 * Created by qi on 2016/12/30.
 */
@WebServlet(urlPatterns = {"/uploadFile"},name = "ServletUpload")
public class ServletUpload extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
          doGet(request,response);
    }

    protected void doGet(HttpServletRequest request,HttpServletResponse response) throws  ServletException,IOException
    {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter pw = response.getWriter();
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if(isMultipart==false)
        {
            pw.write("文件上传格式有误!");
            return;
        }
        DiskFileItemFactory factory  = new DiskFileItemFactory();
        String tempPath = this.getServletContext().getRealPath("/WEB-INF/temp");
        File tmpFile = new File(tempPath);
        if (!tmpFile.exists()) {
            //创建临时目录
            tmpFile.mkdir();
        }
        factory.setSizeThreshold(1024*100);
        factory.setRepository(tmpFile);
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setProgressListener(new ProgressListener() {

            //pBytesRead：当前以读取到的字节数
            //pContentLength：文件的长度
            //pItems:第几项
            public void update(long pBytesRead, long pContentLength,
                               int pItems) {
                System.out.println("已读去文件字节 :"+pBytesRead+" 文件总长度："+pContentLength+"   第"+pItems+"项");

            }
        });

        try {
            List<FileItem> items = upload.parseRequest(request);
            for (FileItem item :
                    items) {
                if (item.isFormField()) {
                    String name = item.getFieldName();
                    String value = item.getString("UTF-8");
                    System.out.println(name + "==" + value);
                } else {
                    InputStream inputStream = item.getInputStream();
                    String fileName = item.getName();
                    if (fileName == null || fileName.equals("")) {
                        continue;
                    }
                    fileName = fileName.substring(fileName.lastIndexOf("\\")+1);
                    String prePath = (String)request.getSession().getAttribute("currentPath");
                    HdfsHelper.uploadToHdfs(prePath+"/"+fileName,inputStream);
                }
                item.delete();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        response.sendRedirect("/goToHadoopDir");
    }
/*
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter pw = response.getWriter();
        String storePath = getServletContext().getRealPath("/WEB-INF/files");
        File storeFile = new File(storePath);
        if(storeFile.exists()==false) storeFile.mkdir();
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if(isMultipart==false)
        {
            pw.write("文件上传格式有误!");
            return;
        }
        DiskFileItemFactory factory  = new DiskFileItemFactory();
        String tempPath = this.getServletContext().getRealPath("/WEB-INF/temp");
        File tmpFile = new File(tempPath);
         if (!tmpFile.exists()) {
          //创建临时目录
            tmpFile.mkdir();
       }
        factory.setSizeThreshold(1024*100);
        factory.setRepository(tmpFile);
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setProgressListener(new ProgressListener() {

            //pBytesRead：当前以读取到的字节数
            //pContentLength：文件的长度
            //pItems:第几项
            public void update(long pBytesRead, long pContentLength,
                               int pItems) {
                System.out.println("已读去文件字节 :"+pBytesRead+" 文件总长度："+pContentLength+"   第"+pItems+"项");

            }
        });
        try {
            List<FileItem> items = upload.parseRequest(request);
            for (FileItem item :
                    items) {
                if(item.isFormField())
                {
                    String name = item.getFieldName();
                    String value = item.getString("UTF-8");
                    System.out.println(name+"=="+value);
                }
                else
                {
                     InputStream inputStream = item.getInputStream();
                    String fileName = item.getName();
                    if(fileName == null || fileName.equals("")) {
                        continue;
                    }
                    System.out.println(fileName);
                    fileName = fileName.substring(fileName.lastIndexOf("\\")+1);
                    fileName = UUID.randomUUID()+"_"+fileName;

                    String newStorePath = makeStorePath(storePath);
                    String newStoreFilePath = newStorePath+"\\"+fileName;
                    OutputStream outputStream = new FileOutputStream(newStoreFilePath);
                    byte[] buffer = new byte[1024];
                    int len = -1;
                    while ((len=inputStream.read(buffer))!=-1)
                    {
                        outputStream.write(buffer,0,len);
                    }
                    inputStream.close();
                    outputStream.close();
                    item.delete();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private String makeStorePath(String storePath) {

        Date date = new Date();
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        String s = df.format(date);
        String path = storePath+"\\"+s;
        File file = new File(path);
        if(!file.exists())
        {
            file.mkdirs();//创建多级目录，mkdir只创建一级目录
        }
        return path;
    }*/
}
