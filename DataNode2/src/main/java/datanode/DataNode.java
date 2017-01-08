package datanode;

import dataStructure.Block;
import dataStructure.DatanodeId;
import protocol.DatanodeProtocol;
import protocol.NameDataNodeProtocol;

import java.io.File;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qi on 2017/1/7.
 */
public class DataNode implements ReportBlockStorage, NameDataNodeProtocol,Serializable{

    private DatanodeId datanodeId;
    private DataTransferServer dataTransferServer;
    private DatanodeProtocol datanodeProtocol;
    private String storePath ;

    public DataNode()
    {
        initConfig();
        initDataNodeIdInfo();
        pushRMI();
        initDataNodeProtocol();
        dataTransferServer = new DataTransferServer(this);
        new Thread(dataTransferServer).start();
        scanBlock();
    }
    private void initConfig()
    {
        HadoopConstant hadoopConstant = new HadoopConstant();
        hadoopConstant.init();
        storePath = HadoopConstant.getStorePath();
    }


    private void pushRMI()
    {
        try {
            LocateRegistry.createRegistry(datanodeId.getRmiPort());
            String path = "rmi://"+datanodeId.getIpAddr()+":"+datanodeId.getRmiPort()+"/"+datanodeId.getRmiName();
            Naming.bind("rmi://"+datanodeId.getIpAddr()+":"+datanodeId.getRmiPort()+"/"+datanodeId.getRmiName(),(NameDataNodeProtocol)this);
            System.out.println("成功发布远程服务!");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }
    }

    public void initDataNodeProtocol()
    {
        try {
            datanodeProtocol = (DatanodeProtocol) Naming.lookup("rmi://"+ HadoopConstant.getNameNodeIpaddress()+":"+ HadoopConstant.getNameNodeRMIPort() +"/NameRMI");
            DatanodeId datanodeId = this.getDatanodeId();
            datanodeProtocol.registerDataNode(this.getDatanodeId());
            System.out.println("远程端口绑定成功！");

        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    public void initDataNodeIdInfo()
    {
        datanodeId = new DatanodeId();
        try {
            datanodeId.setHostName(InetAddress.getLocalHost().getHostName());
            datanodeId.setIpAddr(InetAddress.getLocalHost().getHostAddress());
            datanodeId.setXferPort(HadoopConstant.getDataTransferPort());
            datanodeId.setRmiPort(HadoopConstant.getDataNodeRMIPort());
            datanodeId.setRmiName(HadoopConstant.getNameToDataNodeMethodName());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public DatanodeId getDatanodeId()
    {
        return  datanodeId;
    }

    public static void main(String args[])
    {
            DataNode dataNode = new DataNode();
    }

    public void reportBlock(Block block) {
        try {
            datanodeProtocol.reportBlock(block,this.getDatanodeId());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public void scanBlock()
    {
            List<Block> blocks = new ArrayList<Block>();
            File[] files = listAllFiles();
            if(files==null)return;
            for (File f :files
                 ) {
                String blockId = f.getName().substring(0, f.getName().lastIndexOf("."));
                Block b = new Block();
                b.setBockId(Long.parseLong(blockId));
                blocks.add(b);
            }
        for (Block b:
             blocks) {
            try {
                datanodeProtocol.reportBlock(b,this.getDatanodeId());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private File[] listAllFiles()
    {
        File file = new File(storePath);
        if(file!=null &&file.isDirectory())
        {
            return file.listFiles();
        }
        return null;
    }

    public void deleteBlock(Block block) throws RemoteException {
        File[] files = listAllFiles();
        if(files == null)
            return;
        for (File f :
                files) {
            String blockId = f.getName().substring(0, f.getName().lastIndexOf("."));
            if(Long.parseLong(blockId)==block.getBockId())
            {
                System.out.println("delete success");
                f.delete();
            }
        }
    }
}
