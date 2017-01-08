package namenode;

import dataStructure.Block;
import dataStructure.DatanodeId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by qi on 2017/1/7.
 */
public class DataManager implements Serializable{
    private List<DatanodeId> datanodeIds= new ArrayList<DatanodeId>();
    private int currentDataNodeIndex=0;
    private long nowID = 0;
    public DatanodeId alloctDataNodeForBlock()
    {
        try
        {
            DatanodeId datanodeId = datanodeIds.get(currentDataNodeIndex);
            currentDataNodeIndex++;
            if(currentDataNodeIndex>=datanodeIds.size())
            {
                currentDataNodeIndex = currentDataNodeIndex%datanodeIds.size();
            }
            return  datanodeId;
        }
        catch (Exception e)
        {
            return  null;
        }
    }

    public Block generateBlock()
    {
        Block block = new Block();
        block.setBockId(nowID++);
        block.setGenerationStamp(new Date().getTime());
        return  block;
    }

    public boolean addDataNode(DatanodeId datanodeId)
    {
        System.out.println("注册DataNode");
        for (DatanodeId dataNodeID:
            datanodeIds) {
            if(datanodeId.getIpAddr().equals(dataNodeID.getIpAddr())&&datanodeId.getHostName().equals(dataNodeID.getHostName())
                    && datanodeId.getXferPort()==dataNodeID.getXferPort())
                return false;
        }
        datanodeIds.add(datanodeId);
        return  true;
    }

    public boolean clearDataNode()
    {
        datanodeIds.clear();
        return true;
    }
}
