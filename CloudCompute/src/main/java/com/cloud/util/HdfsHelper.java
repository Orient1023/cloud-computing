package com.cloud.util;

import com.cloud.model.HadoopFileStatus;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static java.net.URI.create;

/**
 * Created by qi on 2017/1/3.
 */
public class HdfsHelper {
    private static String basePath = "hdfs://192.168.163.200:9000/user";
    private static String baseDir = "/user";
     public static final String rootPath   = "/cloudcompute";
    private static FileSystem fileSystem;
    static
    {
        System.setProperty("hadoop.home.dir", "F:\\HadoopCommon\\hadoop-common-2.2.0-bin-master");
    }

    public static void uploadToHdfs(String filePath,InputStream inputStream) throws FileNotFoundException,
            IOException {

        Configuration configuration = new Configuration();
        String desPath = basePath+filePath;
        FileSystem fileSystem = FileSystem.get(create(desPath), configuration);
        OutputStream out = fileSystem.create(new Path(desPath), new Progressable() {
            public void progress() {
                System.out.print(".");
            }
        });
        IOUtils.copyBytes(inputStream, out, 4096, true);
    }

    public static List<HadoopFileStatus> getFilesInDir(String dirPath) throws IOException {
            Configuration configuration = new Configuration();
        String desPath = basePath;
        if(dirPath.equals("") == false)
        {
            desPath = basePath+dirPath;
        }
        FileSystem fs = FileSystem.get(create(desPath), configuration);
        FileStatus fileList[] = fs.listStatus(new Path(desPath));
        List<HadoopFileStatus> fileStatusList = new ArrayList<HadoopFileStatus>();
        for (FileStatus fileStatus:
             fileList) {
             HadoopFileStatus hadoopFileStatus = new HadoopFileStatus();
            hadoopFileStatus.setDir(fileStatus.isDirectory());
            hadoopFileStatus.setFileOrDir(fileStatus.isDirectory()==true?0:1);
            hadoopFileStatus.setFileName(fileStatus.getPath().getName());
            hadoopFileStatus.setFilePath(fileStatus.getPath().toString());
            hadoopFileStatus.setFileSize(fileStatus.getLen());
            hadoopFileStatus.setOwner(fileStatus.getOwner());
            if(fileStatus.isDirectory()==false) {
                BlockLocation[] blockLocations = fs.getFileBlockLocations(fileStatus, 0, fileStatus.getLen());
                hadoopFileStatus.setBlockLocations(blockLocations);
            }
            hadoopFileStatus.setReplication(fileStatus.getReplication());
            fileStatusList.add(hadoopFileStatus);
        }
        return fileStatusList;
    }

    public  static boolean mkdir(String dirName) throws IOException {
        Configuration configuration = new Configuration();
        FileSystem fs = FileSystem.get(create(basePath),configuration);
        fs.mkdirs(new Path(baseDir+dirName));
        return  true;
    }


    public static FSDataInputStream getFileInputStream(String filePath) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(create(filePath), conf);
        FSDataInputStream hdfsInStream = fs.open(new Path(filePath));
        return  hdfsInStream;
    }

    public static void deleteFile(String filePath) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(create(filePath), conf);
        fs.deleteOnExit(new Path(filePath));
        fs.close();
    }


    public static boolean renameFile(String from,String toName) throws IOException {
        Configuration config = new Configuration();
        FileSystem hdfs = FileSystem.get(create(basePath),config);
        Path fromPath = new Path(from);
        String aimPath = from.substring(0,from.lastIndexOf("/")+1)+toName;
        Path toPath = new Path(aimPath);
        return hdfs.rename(fromPath,toPath);
    }
}
