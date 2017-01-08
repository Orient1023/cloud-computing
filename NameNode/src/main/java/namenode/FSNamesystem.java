package namenode;

import dataStructure.*;
import protocol.ClientProtocol;
import protocol.DatanodeProtocol;
import protocol.NameDataNodeProtocol;

import java.io.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qi on 2017/1/7.
 */
public class FSNamesystem extends UnicastRemoteObject implements ClientProtocol, DatanodeProtocol,Serializable{
    private FSDirectory fsDirectory = new FSDirectory();
    private DataManager dataManager = new DataManager();
    private Map<Block, DatanodeId> blocks = new HashMap<Block, DatanodeId>();
    private Map<Block, INodeFile> blockINodeFileMap= new HashMap<Block, INodeFile>();
    private Map<DatanodeId, NameDataNodeProtocol> nameDataNodeProtocolMap = new HashMap<DatanodeId, NameDataNodeProtocol>();


    public FSDirectory getFsDirectory() {
        return fsDirectory;
    }

    public void setFsDirectory(FSDirectory fsDirectory) {
        this.fsDirectory = fsDirectory;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public Map<Block, INodeFile> getBlockINodeFileMap() {
        return blockINodeFileMap;
    }

    public void setBlockINodeFileMap(Map<Block, INodeFile> blockINodeFileMap) {
        this.blockINodeFileMap = blockINodeFileMap;
    }

    protected FSNamesystem() throws RemoteException {
        super();

    }

    public static FSNamesystem loadFromDisk()
    {
        FSNamesystem fsNamesystem = null;
        try {
            fsNamesystem = new FSNamesystem();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("fsdirectory"));
            FSDirectory directory = (FSDirectory)objectInputStream.readObject();
            fsNamesystem.setFsDirectory(directory);
            objectInputStream.close();

            objectInputStream = new ObjectInputStream(new FileInputStream("dataManager"));
            fsNamesystem.setDataManager((DataManager)objectInputStream.readObject());
            objectInputStream.close();

            objectInputStream = new ObjectInputStream(new FileInputStream("blockINodeFileMap"));
            fsNamesystem.setBlockINodeFileMap((Map<Block, INodeFile>)objectInputStream.readObject());
            objectInputStream.close();

        } catch (ClassNotFoundException e) {
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fsNamesystem;
    }

    public BlockStoreLocation createFile(String filePath) {
        if(fsDirectory.addFile(filePath)==false)
        {
            System.out.println("添加文件失败！");
        }
        return addBlock(filePath);
    }

    public BlockStoreLocation addBlock(String filePath) {
        INode file = fsDirectory.findNodeByPath(filePath);
        if(file instanceof INodeFile == false)
            return null;
        Block block = dataManager.generateBlock();
        DatanodeId datanodeId = dataManager.alloctDataNodeForBlock();
        blockINodeFileMap.put(block, (INodeFile) file);
        return  new BlockStoreLocation(block,datanodeId);
    }

    public List<BlockStoreLocation> getFileStoreBlocks(String filePath) throws RemoteException {
        INode node = fsDirectory.findNodeByPath(filePath);
        if(node==null || node instanceof INodeFile == false)
            return null;
        List<Block> containBlocks = ((INodeFile)node).getBlocks();
        List<BlockStoreLocation> fileBlockStoreLocation = new ArrayList<BlockStoreLocation>();
        for (Block b :
                containBlocks) {
            DatanodeId datanode  = blocks.get(b);
            fileBlockStoreLocation.add(new BlockStoreLocation(b,datanode));
        }
        return fileBlockStoreLocation;
    }

    public INode getNodeByPath(String filePath) throws RemoteException {
        return fsDirectory.findNodeByPath(filePath);
    }

    public boolean mkdir(String dirPath) throws RemoteException {
       return fsDirectory.mkdir(dirPath);
    }

    public boolean renameNode(String srcPath, String aimPath) {
        INode node = fsDirectory.findNodeByPath(srcPath);
        String aimName = aimPath.substring(aimPath.lastIndexOf("/")+1);
        if(node == null)
            return false;
        if(node instanceof INodeDirectory)
        {
            node.setNodeName(aimName);
        }
        else if(node instanceof INodeFile)
        {
            List<Block> blocks = ((INodeFile)node).getBlocks();
            for (Block block:blocks
                 ) {
                blockINodeFileMap.get(block).setNodeName(aimName);
            }
            node.setNodeName(aimName);
        }
        return true;
    }

    public boolean deleteNode(String srcPath) throws RemoteException {
        INode node = fsDirectory.findNodeByPath(srcPath);
        List<Block>fileBlocks= new ArrayList<Block>();
        if(node==null)
            return false;
        if(node instanceof INodeDirectory)
        {
            fileBlocks = ((INodeDirectory)node).getAllBlocks();
        }
        else if(node instanceof INodeFile)
        {
            fileBlocks = ((INodeFile)node).getBlocks();
        }
        deleteBlocks(fileBlocks);
        ((INodeDirectory)node.getParent()).removeChild(node);
        return  true;
    }


    public FileStatus getFileStatus(String path)
    {
        INode node = fsDirectory.findNodeByPath(path);
        FileStatus fileStatus = null;
        if(node instanceof INodeFile)
        {
            fileStatus = new FileStatus();
            INodeFile file = (INodeFile)node;
            fileStatus.setGetPath(file.getTotalPath());
            fileStatus.setDir(file.isDir());
            fileStatus.setName(file.getNodeName());
            fileStatus.setSize(file.getFileSize());
            List<Block>blockLisr = file.getBlocks();
            for (Block b :
                    blockLisr) {
                DatanodeId d = blocks.get(b);
                BlockInfo blockInfo= new BlockInfo();
                blockInfo.setBlockId(b.getBockId());
                blockInfo.setHostname(d.getHostName());
                blockInfo.setIpAddress(d.getIpAddr());
                fileStatus.addBlockInfo(blockInfo);
            }
        }
        else if(node instanceof INodeDirectory)
        {
            INodeDirectory directory = (INodeDirectory)node;
            fileStatus = new FileStatus();
            fileStatus.setName(directory.getNodeName());
            fileStatus.setGetPath(directory.getTotalPath());
            fileStatus.setDir(true);
        }
        return  fileStatus;
    }

    public List<FileStatus> listFileStatus(String dirPath)
    {
        List<FileStatus> fileStatuses = new ArrayList<FileStatus>();
        INode node = fsDirectory.findNodeByPath(dirPath);
        if(node instanceof INodeDirectory)
        {
            INodeDirectory directory = (INodeDirectory)node;
            List<INode>childs = directory.getChildren();
            for (INode n:
                 childs) {
                FileStatus f = getFileStatus(n.getTotalPath());
                fileStatuses.add(f);
            }
        }
        return fileStatuses;
    }


    public void deleteBlocks(List<Block>fileBlocks)
    {
        for (Block b :
                fileBlocks) {
            DatanodeId d = blocks.get(b);
            NameDataNodeProtocol protocol =
                    nameDataNodeProtocolMap.get(d);
            try {
                protocol.deleteBlock(b);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            blockINodeFileMap.remove(b);
            blocks.remove(b);
        }
    }

    public void registerDataNode(DatanodeId datanodeId) {


        NameDataNodeProtocol nameDataNodeProtocol = null;
        try {
            String path = "rmi://"+datanodeId.getIpAddr()+":"+datanodeId.getRmiPort()+"/"+datanodeId.getRmiName();
             nameDataNodeProtocol  = (NameDataNodeProtocol)Naming.lookup("rmi://"+datanodeId.getIpAddr()+":"+datanodeId.getRmiPort()+"/"+datanodeId.getRmiName());
            System.out.println("master 订阅了远程服务!");
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        dataManager.addDataNode(datanodeId);
        if(nameDataNodeProtocol!=null)
             nameDataNodeProtocolMap.put(datanodeId,nameDataNodeProtocol);
    }

    public void reportBlock(Block block, DatanodeId datanodeId) throws RemoteException {
        blocks.put(block,datanodeId);
        blockINodeFileMap.get(block).addBlock(block);
        System.out.println("blockID:  "+ block.getBockId());
        System.out.println("datanodeID:  "+datanodeId.getIpAddr());
    }

    public void saveAsImage()
    {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("fsdirectory"));
            objectOutputStream.writeObject(fsDirectory);
            objectOutputStream.close();
            objectOutputStream = new ObjectOutputStream(new FileOutputStream("dataManager"));
            objectOutputStream.writeObject(dataManager);
            objectOutputStream.close();
            objectOutputStream = new ObjectOutputStream(new FileOutputStream("blockINodeFileMap"));
            objectOutputStream.writeObject(blockINodeFileMap);
            objectOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
