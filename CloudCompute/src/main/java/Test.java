import org.apache.commons.fileupload.FileItem;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import static java.net.URI.create;

/**
 * Created by qi on 2016/12/26.
 */
public class Test {
    private static final String url = "hdfs://192.168.163.200:9000/user";
    public static void main(String[] args) throws Exception {
     //   String uri = "hdfs://192.168.163.200:9000/";
        System.setProperty("hadoop.home.dir", "F:\\HadoopCommon\\hadoop-common-2.2.0-bin-master");
        Configuration config = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(url), config);

  /*      FileStatus[] statuses = fs.listStatus(new Path("/user"));
        for (FileStatus status : statuses) {
            System.out.println(status);
         //   System.out.println("owner: "+status.getOwner());
        }*/

    //    uploadToHdfs();
     //     readFromHdfs();
     //   getDirectoryFromHdfs();
    /*    FileSystem fs = getFileSystem();
        System.out.println("please enter a key");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        RemoteIterator<LocatedFileStatus> fileStatus =  fs.listFiles(new Path("/user"),true);
        while (fileStatus.hasNext())
        {
            LocatedFileStatus f = fileStatus.next();
            System.out.println("file name :"+f.getPath().getName()+" file owner:"+f.getOwner());
        }
  //      readFromHdfs();*/
    //    copyFromLocalToHDFS();
 //       createHDFSFile();
      //      renameHDFSFILE("luoyuxia","yuxia1");
 //       checkIsExsist("yuxia1");
  //      getListsOfHDFSHostName();
       //     getLocationInCluster("yuxia1");
     //   printHDFSFile("yuxia1");
    //    fs.mkdirs(new Path("/user/qi/luo/" +
     //           ""));
    //    readFromHdfs();
        deleteFromHdfs();
    }

    private static FileSystem getFileSystem() throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(url),conf);
        return  fs;
    }
    private static void uploadToHdfs() throws FileNotFoundException,
            IOException {

        String localSrc = "F:\\pro-apache-hadoop-master.zip";
        String dst = "hdfs://192.168.163.200:9000/user/cloudcompute/xxxx.zip";
        InputStream in = new BufferedInputStream(new FileInputStream(localSrc));
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(create(dst), conf);
        OutputStream out = fs.create(new Path(dst), new Progressable() {
            public void progress() {
                System.out.print(".");
            }
        });
        IOUtils.copyBytes(in, out, 4096, true);
    }

    private static void readFromHdfs() throws FileNotFoundException,IOException {
        String dst = "hdfs://192.168.163.200:9000/user/cloudcompute/xxxx.zip";
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(create(dst), conf);
        FSDataInputStream hdfsInStream = fs.open(new Path(dst));
   /*     OutputStream out = new FileOutputStream("F:\\li");
        byte[] ioBuffer = new byte[1024];
        int readLen = hdfsInStream.read(ioBuffer);
        while(-1 != readLen){
            out.write(ioBuffer, 0, readLen);
            readLen = hdfsInStream.read(ioBuffer);
        }
        out.close();*/
        IOUtils.copyBytes(hdfsInStream,System.out,4096,false);
        hdfsInStream.close();
        fs.close();
    }

    private static void deleteFromHdfs() throws FileNotFoundException,IOException {
        String dst = "hdfs://192.168.163.200:9000/user/cloudcompute/xxxx.zip";
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(create(dst), conf);
        fs.deleteOnExit(new Path(dst));
        fs.close();
    }

    private static void getDirectoryFromHdfs() throws FileNotFoundException,IOException {
        String dst = "hdfs://192.168.163.200:9000/user/cloudcompute";
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(create(dst), conf);
        FileStatus fileList[] = fs.listStatus(new Path(dst));
        int size = fileList.length;
        for(int i = 0; i < size; i++){
            System.out.println("name:" + fileList[i].getPath().getName() + "\t\tsize:" + fileList[i].getLen()
            +"  owner:"  + fileList[i].getOwner().toString());
        }
        fs.close();
    }

    //从文件系统复制到HDFS中
    private static void copyFromLocalToHDFS() throws IOException {

        Configuration config = new Configuration();
        FileSystem hdfs = FileSystem.get(create(url),config);
        String srcPath = "F:\\vs_enterprise_CHS.exe";
        String dstPath = "hdfs://192.168.163.200:9000/user/cloudcompute/vs_enterprise_CHS.exe";
        Path src = new Path(srcPath);
        Path dst = new Path(dstPath);
      //  hdfs.co
        hdfs.copyFromLocalFile(src,dst);
    }

    //创建HDFS文件
    private static void  createHDFSFile() throws IOException {
        Configuration config = new Configuration();
        FileSystem hdfs = FileSystem.get(create(url),config);
        String filename = "luoyuxia";
        Path path  = new Path(filename);
        FSDataOutputStream outputStream = hdfs.create(path);
        outputStream.write("123".getBytes());

    }

    //重命名文件
    private static void renameHDFSFILE(String fromName,String toName) throws IOException {
        Configuration config = new Configuration();
        FileSystem hdfs = FileSystem.get(create(url),config);
        Path fromPath = new Path(fromName);
        Path toPath = new Path(toName);
        boolean isRename = hdfs.rename(fromPath,toPath);
        System.out.println(isRename);
    }

    //删除文件
    private static void deleteHDFSFILE(String filePath)throws  IOException
    {
        Configuration config = new Configuration();
        FileSystem hdfs = FileSystem.get(create(url),config);
        Path path = new Path(filePath);
        boolean isDeleted = hdfs.deleteOnExit(path);
    }

    //检查一个文件是否存在
    private static void checkIsExsist(String filePath)throws IOException
    {
        Configuration config = new Configuration();
        FileSystem hdfs = FileSystem.get(create(url),config);
        Path path = new Path(filePath);
        boolean isExists = hdfs.exists(path);
        System.out.println(isExists);
    }

    //获取文件在HDFS集群中的位置
    private static void getLocationInCluster(String filePath) throws IOException {
        Configuration config = new Configuration();
        FileSystem hdfs = FileSystem.get(create(url),config);
        Path path = new Path(filePath);
        FileStatus fileStatus = hdfs.getFileStatus(path);
        BlockLocation[] blockLocations = hdfs.getFileBlockLocations(fileStatus,0,fileStatus.getLen());
        int blkCount = blockLocations.length;
        for(int i=0;i<blkCount;i++)
        {
        //    blockLocations[i].
            blockLocations[i].getLength();
            String[] hosts = blockLocations[i].getHosts();
            for (String host :
                    hosts) {
                System.out.println(host);
            }
        }
       // fileStatus
    //    String[][] locations = fileStatus.
    }

    private static void printHDFSFile(String fileName) throws IOException {
        Configuration config = new Configuration();
        FileSystem hdfs = FileSystem.get(create(url),config);
        Path path = new Path(fileName);
        FSDataInputStream dis = hdfs.open(path);
        System.out.println(dis.read());
        dis.close();
    }

    //获取HDFS集群中主机名列表
    private static void getListsOfHDFSHostName() throws IOException {
        Configuration config = new Configuration();
        FileSystem hdfs = FileSystem.get(create(url),config);
     //   DistributedFileSystem dfs = (DistributedFileSystem)hdfs;
        DatanodeInfo[] datanodeInfo = ((DistributedFileSystem) hdfs).getDataNodeStats();
        String[] names = new String[datanodeInfo.length];
        for (int i=0;i<datanodeInfo.length;i++)
        {
            names[i] = datanodeInfo[i].getHostName();
        }
        for (String name :
                names) {
            System.out.println(name);
        }
    }

}