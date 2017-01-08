package datanode;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by qi on 2017/1/7.
 */
public class DataTransferServer implements Runnable,Serializable{
    private ReportBlockStorage reportBlockStorage;
    public DataTransferServer(ReportBlockStorage reportBlockStorage)
    {
        this.reportBlockStorage =  reportBlockStorage;
    }
    public void run() {
        try {
            ServerSocket server = new ServerSocket(HadoopConstant.getDataTransferPort());
            while (true)
            {
                Socket client = server.accept();
                System.out.println("接收到一个客户端");
                DataTransferThread dataTransferThread = new DataTransferThread(reportBlockStorage,client);
                Thread thread = new Thread(dataTransferThread);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
