package com.cloudcompute.myHadoopClient;

import dataStructure.BlockStoreLocation;
import dataStructure.FileStatus;
import dataStructure.INode;
import protocol.ClientProtocol;

import java.io.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;

/**
 * Created by qi on 2017/1/7.
 */
public class Client {

    private ClientProtocol clientProtocol;
    private DFSTransferClient dfsTransferClient;
    private Integer blockSize ;
    public Client()
    {
        init();
    }

    private void init()
    {
        try {
            Properties properties = new Properties();
            properties.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
            String nameNodeIP = properties.getProperty("IPAddress");
            String RMIPort  = properties.getProperty("RMIPort");
            blockSize = Integer.valueOf(properties.getProperty("BlockSize"));
            clientProtocol = (ClientProtocol) Naming.lookup("rmi://"+nameNodeIP+":"+RMIPort+"/NameRMI");
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean createFile(String filePath, InputStream inputStream)
    {
        try {
            BlockStoreLocation blockStoreLocation = clientProtocol.createFile(filePath);
            transformDataStream(filePath,blockStoreLocation,inputStream);
            return true;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void transformDataStream(String filePath, BlockStoreLocation blockStoreLocation, InputStream inputStream)
    {
        dfsTransferClient = new DFSTransferClient(blockStoreLocation.getDatanodeId());
        int cutrrentLength = 0;
        if(dfsTransferClient.startTransferData(1))
        {
            System.out.println("can transfer dataStructure");
        }
        dfsTransferClient.writeBlockId(blockStoreLocation.getBlock().getBockId());
        byte[] bytes = new byte[1024];
        int length ;
        try {
            while ((length=inputStream.read(bytes))!=-1)
            {
                dfsTransferClient.writeBytes(bytes,0,length);
                cutrrentLength +=length;
                if(cutrrentLength>=blockSize)
                {
                    dfsTransferClient.closeSocket();
                    transformDataStream(filePath,askForNewBlock(filePath),inputStream);
                }
            }
            dfsTransferClient.closeSocket();
            inputStream.close();
        } catch (IOException e) {
          //  e.printStackTrace();
        }
    }

    public BlockStoreLocation askForNewBlock(String filePath)
    {
        try {
            BlockStoreLocation blockStoreLocation  = clientProtocol.addBlock(filePath);
            return blockStoreLocation;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void readFile(String filePath,String aimPath)
    {
        try {
            List<BlockStoreLocation> blockStoreLocationList = clientProtocol.getFileStoreBlocks(filePath);
            Collections.sort(blockStoreLocationList, new Comparator<BlockStoreLocation>() {
                @Override
                public int compare(BlockStoreLocation b1, BlockStoreLocation b2) {
                    Long first = new Long(b1.getBlock().getBockId());
                    Long second = new Long(b2.getBlock().getBockId());
                    return first.compareTo(second);
                }
            });
            if(blockStoreLocationList==null)
            {
                System.out.println("错误：不存在这样的块!");
            }
            File file = createFileFromPath(aimPath);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            for (BlockStoreLocation b :blockStoreLocationList) {
                if(b.getBlock()==null||b.getDatanodeId()==null)
                    continue;
                readDataFromDataNode(b,fileOutputStream);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void readDataFromDataNode(BlockStoreLocation blockStoreLocation, OutputStream outputStream)
    {
        dfsTransferClient = new DFSTransferClient(blockStoreLocation.getDatanodeId());
        if(dfsTransferClient.startTransferData(2))
        {
            System.out.println("can read dataStructure");
        }

        dfsTransferClient.writeBlockId(blockStoreLocation.getBlock().getBockId());
        byte[] bytes = new byte[1024];
        int length = -1;
        while ((length=dfsTransferClient.readBytes(bytes))!=-1)
        {
            try {
                outputStream.write(bytes,0,length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public INode getFile(String filePath)
    {
        INode node = null;
        try {
            node = clientProtocol.getNodeByPath(filePath);
            return node;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return node;
    }


    public FileStatus getFileStatus(String path)
    {
        try {
            return clientProtocol.getFileStatus(path);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public File createFileFromPath(String path)
    {
        File file = new File(path);
        if(file.exists()==false)
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file;
    }

    public boolean mkDir(String dirPath)
    {
        try {
            if(clientProtocol.mkdir(dirPath)==false)
            {
             System.out.println("远程创建目录失败");
                return false;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean rename(String srcPath,String aimPath)
    {
        try {
            return clientProtocol.renameNode(srcPath,aimPath);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteNode(String nodePath)
    {
        try
        {
            return   clientProtocol.deleteNode(nodePath);
        }
        catch (RemoteException e)
        {

        }
        return false;
    }

    public List<FileStatus> listFileStatusInDir(String dirPath)
    {
        try {
            return clientProtocol.listFileStatus(dirPath);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return new ArrayList<FileStatus>();
    }






    public static void main(String args[]) {
        Client client = new Client();
  /*   try {
            FileInputStream inputStream = new FileInputStream(new File("test.txt"));
            client.createFile("/root/tt.txt",inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    /*    client.mkDir("/root/tt");
        INode node = client.getFile("/root/tt");
        if(node!=null || node instanceof INodeDirectory)
        {
            System.out.println(((INodeDirectory)node).isDir());
        }*/
   /*     INode node = client.getFile("/root/my.txt");
        System.out.println(node.getNodeName());
   //    System.out.println(client.rename("/root/tt.txt","/root/my.txt"));*/
     //    client.deleteNode("/root/tt.txt");
     //   INode node = client.getFile("/root/tt.txt");
   /*     if(node==null)
        {
            System.out.println("delete success");
        }*/

        FileStatus fileStatus = client.getFileStatus("/root/tt.txt");
        System.out.println(fileStatus.getBlockCounts());
    }


}
