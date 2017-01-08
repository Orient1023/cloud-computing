package namenode;

import dataStructure.INode;
import dataStructure.INodeDirectory;
import dataStructure.INodeFile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qi on 2017/1/7.
 */
public class FSDirectory implements Serializable{

    private INodeDirectory virtualRootPath ;
    private INodeDirectory rootDir ;
    public FSDirectory()
    {
        virtualRootPath = new INodeDirectory("");
        rootDir = new INodeDirectory("root");
        virtualRootPath.addChild(rootDir);
    }


    public INode findNodeByPath(String nodePath)
    {
        String[] paths = nodePath.split("/");
        List<String>  pathList = new ArrayList<String>();
        for (String path :
                paths) {
            pathList.add(path);
        }
        pathList.remove(0);
        return virtualRootPath.findNode(pathList);
    }



    public boolean addChild(String filePath)
    {
        return true;
    }

    public boolean addFile(String filePath)
    {
        String  filePrePath = filePath.substring(0,filePath.lastIndexOf("/"));
        INode node = findNodeByPath(filePrePath);
        if(node==null || node instanceof INodeFile)
            return false;
        INode file = new INodeFile(filePath.substring(filePath.lastIndexOf("/")+1));
        ((INodeDirectory)node).addChild(file);
        return true;
    }

    public boolean mkdir(String filePath)
    {
        String  filePrePath = filePath.substring(0,filePath.lastIndexOf("/"));
        INode node = findNodeByPath(filePrePath);
        if(node==null || node instanceof INodeDirectory == false)
        {
            return  false;
        }
        ((INodeDirectory)node).addChild(new INodeDirectory(filePath.substring(filePath.lastIndexOf("/")+1)));
        return true;
    }

 /*   public boolean addDir(String dirPath)
    {
            return true;
    }*/

    public boolean addBlock()
    {
        return  true;
    }


    public static void main(String args[])
    {
        FSDirectory f = new FSDirectory();
        INode node =  f.findNodeByPath("/root/lyx");
        System.out.println(node.getTotalPath());
    }
}
