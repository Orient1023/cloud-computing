package namenode;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Properties;

/**
 * Created by qi on 2017/1/7.
 */
public class NameNode extends JFrame{
    private FSNamesystem fsNamesystem;
    private Properties properties = new Properties();
    public NameNode()
    {
        fsNamesystem = FSNamesystem.loadFromDisk();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("close");
                fsNamesystem.saveAsImage();
                super.windowClosing(e);
            }
        });
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run()
    {
        try {
            if(fsNamesystem!=null) {
                String ipAddress = properties.getProperty("IPAddress");
                System.out.println(ipAddress);
                System.setProperty("java.rmi.server.hostname",ipAddress);
                Integer RMIPort = Integer.valueOf(properties.getProperty("RMIPort"));
                LocateRegistry.createRegistry(RMIPort);
                Naming.bind("rmi://"+ipAddress+":"+RMIPort+"/NameRMI", fsNamesystem);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }
    }
    public static void main(String args[])
    {
        NameNode nameNode = new NameNode();
        nameNode.setSize(500,400);
        nameNode.setVisible(true);
        nameNode.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        nameNode.run();
    }
}
