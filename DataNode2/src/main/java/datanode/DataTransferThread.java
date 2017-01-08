package datanode;

import dataStructure.Block;

import java.io.*;
import java.net.Socket;

/**
 * Created by qi on 2017/1/7.
 */
public class DataTransferThread implements Runnable,Serializable{
    private ReportBlockStorage reportBlockStorage;
    private Socket clientSocket ;
    public DataTransferThread(ReportBlockStorage reportBlockStorage, Socket socket)
    {
        this.reportBlockStorage = reportBlockStorage;
        this.clientSocket = socket;
    }
    public void run() {
        try {
            while (true) {
                Integer integer = clientSocket.getInputStream().read();
                if(integer==-1)
                    break;
                clientSocket.getOutputStream().write(1);
                long blockId = clientSocket.getInputStream().read();
                if (blockId == -1)
                    break;
                System.out.println(blockId);
                if(integer==1) {
                    byte[] bytes = new byte[1024];
                    File file = generateBlockFile(blockId);
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    int length;
                    long totalBytes = 0;
                    while ((length = clientSocket.getInputStream().read(bytes)) != -1) {
                        fileOutputStream.write(bytes, 0, length);
                        totalBytes += length;
                    }
                    fileOutputStream.close();
                    Block block = new Block();
                    block.setBockId(blockId);
                    block.setNumBytes(totalBytes);
                    reportBlockStorage.reportBlock(block);
                }
                else if(integer == 2)
                {
                    File file = findFileByBlockId(blockId);
                    if(file==null)
                        continue;
                    FileInputStream inputStream = new FileInputStream(file);
                    byte[] bytes = new byte[1024];
                    int len = -1;
                    while ((len=inputStream.read(bytes))!=-1)
                    {
                        clientSocket.getOutputStream().write(bytes,0,len);
                    }
                    clientSocket.close();
                    inputStream.close();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File generateBlockFile(long blockId)
    {
        File file = new File(HadoopConstant.getStorePath()+"\\"+blockId+".block");
        if(file.exists())
            file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public File findFileByBlockId(long blockId)
    {
        File file = new File(HadoopConstant.getStorePath());
        if(file!=null && file.isDirectory())
        {
            File[] files = file.listFiles();
            for (File f :
                    files) {
                String blockName = f.getName().substring(0, f.getName().lastIndexOf("."));
                if (f.getName().substring(0, f.getName().lastIndexOf(".")).equals(new Long(blockId).toString()))
                {
                    return f;
                }
            }
        }
        return null;
    }
}
