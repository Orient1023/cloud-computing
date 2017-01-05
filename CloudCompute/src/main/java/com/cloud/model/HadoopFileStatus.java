package com.cloud.model;

import com.sun.org.apache.xerces.internal.impl.dv.xs.DoubleDV;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.hadoop.fs.BlockLocation;

/**
 * Created by qi on 2017/1/3.
 */
public class HadoopFileStatus {
    private String fileName ;
    private String filePath ;
    private Long fileSize ;
    private Boolean dir ;
    private Integer fileOrDir;
    private Integer blkCount;
    private short replication ;
    private String formatSizeInfo;

    public String getFormatSizeInfo() {
        if(fileSize>0 && fileSize<1024)
        {
            return fileSize +"字节";
        }
        else if(fileSize>=1024 && fileSize <1024*1024)
        {
            Double d = Double.parseDouble(fileSize.toString()) / 1024;
            return String.format("%.2f",d) +"KB";
        }
        else if(fileSize>=1024 * 1024 && fileSize <1024*1024*1024)
        {
            Double d = Double.parseDouble(fileSize.toString()) / (1024*1024);
            return String.format("%.2f",d) +"MB";
        }
        else if(fileSize >=1024*1024*1024 && fileSize <=1024*1024*1024*1024)
        {
            Double d = Double.parseDouble(fileSize.toString()) / (1024*1024*1024);
            return String.format("%.2f",d) +"GB";
        }
        else
        {
            Double d = Double.parseDouble(fileSize.toString()) / (1024*1024*1024*1024);
            return String.format("%.2f",d) +"TB";
        }
    }

    public void setFormatSizeInfo(String formatSizeInfo) {
        this.formatSizeInfo = formatSizeInfo;
    }

    public short getReplication() {
        return replication;
    }

    public void setReplication(short replication) {
        this.replication = replication;
    }

    public Integer getBlkCount()
    {
        return  blockLocations.length;
    }

    public BlockLocation[] getBlockLocations() {
        return blockLocations;
    }

    public void setBlockLocations(BlockLocation[] blockLocations) {
        this.blockLocations = blockLocations;
    }

    private BlockLocation[] blockLocations;

    public Boolean getDir() {
        return dir;
    }

    public void setDir(Boolean dir) {
        this.dir = dir;
    }

    public Integer getFileOrDir() {
        return fileOrDir;
    }

    public void setFileOrDir(Integer fileOrDir) {
        this.fileOrDir = fileOrDir;
    }


    private String owner ;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
