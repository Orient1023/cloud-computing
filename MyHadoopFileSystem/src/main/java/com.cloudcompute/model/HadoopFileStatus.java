package com.cloudcompute.model;


import dataStructure.BlockInfo;

import java.util.List;

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
    private String formatSizeInfo;
    private List<BlockInfo> blockInfos;

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

    public List<BlockInfo> getBlockInfos() {
        return blockInfos;
    }

    public void setBlockInfos(List<BlockInfo> blockInfos) {
        this.blockInfos = blockInfos;
    }

    public Integer getBlkCount()
    {
        return  blockInfos.size();
    }



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


}
