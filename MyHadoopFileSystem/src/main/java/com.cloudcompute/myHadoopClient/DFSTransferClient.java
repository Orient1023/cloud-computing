package com.cloudcompute.myHadoopClient;

import dataStructure.DatanodeId;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by qi on 2017/1/7.
 */
public class DFSTransferClient {
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    public DFSTransferClient(DatanodeId datanodeId)
    {
        connectToDataNode(datanodeId);
    }

    private void connectToDataNode(DatanodeId datanodeId)
    {
        try {
            socket = new Socket(datanodeId.getIpAddr(),datanodeId.getXferPort());
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*
    public void connectToDataNode(DatanodeId datanodeId)
    {
        try {
                socket = new Socket(datanodeId.getIpAddr(),datanodeId.getXferPort());
                socket.getOutputStream().write(0);
                Integer response = socket.getInputStream().read();
                System.out.println(response);
                socket.getOutputStream().write("132434".getBytes());
                socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/

    public boolean startTransferData(Integer type)
    {
        try
        {
            outputStream.write(type);
            Integer respone = inputStream.read();
            return respone == 1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void writeBlockId(long blockId)
    {
        try {
            outputStream.write((int)blockId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeBytes(byte[] bytes,int start,int offest)
    {
        try {
            outputStream.write(bytes,start,offest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public int readBytes(byte[] bytes)
    {
        try
        {
            return inputStream.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void closeSocket()
    {
        try {
            if(socket.isClosed()==false)
                 socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
