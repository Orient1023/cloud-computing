package com.cloudcompute.myhadoopUtil;

import com.cloudcompute.model.HadoopFileStatus;
import com.cloudcompute.myHadoopClient.Client;
import dataStructure.FileStatus;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qi on 2017/1/8.
 */
public class HadoopHelper {

    public static final String rootPath ="/root";


    public static Client client = new Client();

    public static void uploadToHdfs(String filePath,InputStream inputStream)
    {
        if(client.createFile(filePath,inputStream)==false)
        {
            System.out.println("上传文件失败！");
        }
    }

    public static FileStatus getFileStatus(String filePath)
    {
        return client.getFileStatus(filePath);
    }

    public static boolean renameFile(String srcpath,String aimName)
    {
        String aimPath = srcpath.substring(0,srcpath.lastIndexOf("/")+1)+aimName;
        return client.rename(srcpath,aimPath);
    }

    public static boolean deleteFile(String filePath)
    {
        return client.deleteNode(filePath);
    }

    public static boolean readFile(String filePath,String aimPath)
    {
        client.readFile(filePath,aimPath);
        return true;
    }

    public static List<FileStatus> listFileStatus(String dirPath)
    {
        return client.listFileStatusInDir(dirPath);
    }

    public static List<HadoopFileStatus> listHadoopFileStatus(String dirPath)
    {
        List<FileStatus> fileStatuses = listFileStatus(dirPath);
        List<HadoopFileStatus>hadoopFileStatusList = new ArrayList<HadoopFileStatus>();
        for (FileStatus f :
                fileStatuses) {
            hadoopFileStatusList.add(generateHadoopFileStatusFromFileStats(f));
        }
        return hadoopFileStatusList;
    }


    public static HadoopFileStatus getHadoopFileStatus(String filePath)
    {
        FileStatus fileStatus = getFileStatus(filePath);
        return generateHadoopFileStatusFromFileStats(fileStatus);
    }
    public static HadoopFileStatus generateHadoopFileStatusFromFileStats(FileStatus fileStatus)
    {
        if (fileStatus==null)
            return null;
        HadoopFileStatus hadoopFileStatus = new HadoopFileStatus();
        hadoopFileStatus.setFilePath(fileStatus.getGetPath());
        hadoopFileStatus.setBlockInfos(fileStatus.getBlockInfos());
        hadoopFileStatus.setDir(fileStatus.isDir());
        hadoopFileStatus.setFileName(fileStatus.getName());
        hadoopFileStatus.setFileSize(fileStatus.getSize());
        return hadoopFileStatus;
    }

    public static boolean mkdir(String dirPath)
    {
        return client.mkDir(dirPath);
    }



}
