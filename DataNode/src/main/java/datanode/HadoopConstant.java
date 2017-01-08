package datanode;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by qi on 2017/1/8.
 */
public  class   HadoopConstant {
    private static String storePath ;
    private static String nameNodeIpaddress;
    private static Integer nameNodeRMIPort;
    private static Integer dataNodeRMIPort;
    private static Integer dataTransferPort;
    private static  String nameToDataNodeMethodName;
    public  void init()
    {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        storePath = properties.getProperty("StorePath");
        dataNodeRMIPort = Integer.valueOf(properties.getProperty("DataNodeRMIPort"));
        nameNodeIpaddress = properties.getProperty("NameNodeIP");
        nameNodeRMIPort = Integer.valueOf(properties.getProperty("NameNodeRMIPort"));
        dataTransferPort = Integer.valueOf(properties.getProperty("dataTransferPort"));
        nameToDataNodeMethodName = properties.getProperty("NameToDataNodeMethodName");
    }

    public static String getStorePath() {
        return storePath;
    }

    public static String getNameNodeIpaddress() {
        return nameNodeIpaddress;
    }

    public static Integer getNameNodeRMIPort() {
        return nameNodeRMIPort;
    }

    public static Integer getDataNodeRMIPort() {
        return dataNodeRMIPort;
    }

    public static Integer getDataTransferPort() {
        return dataTransferPort;
    }

    public static String getNameToDataNodeMethodName() {
        return nameToDataNodeMethodName;
    }
}
