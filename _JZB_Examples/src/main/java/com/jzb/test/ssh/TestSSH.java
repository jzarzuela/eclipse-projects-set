/**
 * 
 */
package com.jzb.test.ssh;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Vector;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jzb.test.ssh.Test1.MyMonitor;

/**
 * @author jzarzuela
 * 
 */
public class TestSSH {

    // ----------------------------------------------------------------------------------------------------
    private static class FileInfo {

        public boolean isDir;
        public String  name;
        public long    size;
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        long t1 = 0, t2 = 0;
        try {
            System.out.println("\n***** EXECUTION STARTED *****\n");
            TestSSH me = new TestSSH();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("\n***** EXECUTION FINISHED [" + (t2 - t1) + "]*****\n");
            System.exit(1);
        } catch (Throwable th) {
            t2 = System.currentTimeMillis();
            System.out.println("***** EXECUTION FAILED [" + (t2 - t1) + "]*****");
            th.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {

        // -------- CREATE
        JSch jsch = new JSch();
        Session session = jsch.getSession("vbox", "192.168.71.107", 22);
        UserInfo ui = new MyUserInfo();
        session.setUserInfo(ui);
        session.setConfig("StrictHostKeyChecking", "no");
        
        // -------- CONNECT SESSION
        session.connect();
        
        // -------- CONNECT CHANNEL
        System.out.println("Connecting FTP channel");
        ChannelSftp ftpChnl = (ChannelSftp) session.openChannel("sftp");
        ftpChnl.connect(30000);

        // -------- MEASURE
        ArrayList<FileInfo> info = _listFolder(ftpChnl,".");
        for(FileInfo fi:info) {
            System.out.println(fi.name+" - "+fi.isDir+" - "+fi.size);
        }

        // -------- TERMINATE AND RESTART CHANNEL
        System.out.println("Disconnecting FTP channel");
        ftpChnl.exit();

        // -------- TERMINATE AND RESTART SESSION
        session.disconnect();
    }
    
    // ----------------------------------------------------------------------------------------------------
    private void _createRFolder(ChannelSftp c, String rfolder) throws Exception {

        System.out.println("Creating remote folder (if doesn't exist): " + rfolder);
        try {
            c.mkdir(rfolder);
        } catch (SftpException e) {
        }
        c.cd(rfolder);
    }
    
    // ----------------------------------------------------------------------------------------------------
    private void _sendFile(ChannelSftp c, File localFile, String remoteName) throws Exception {
        FileInputStream fis = new FileInputStream(localFile);
        c.put(fis, remoteName, new MyMonitor(localFile.length()), c.OVERWRITE);
    }
    
    // ----------------------------------------------------------------------------------------------------
    private int _execCommand(Session session, String cmd) throws Exception {

        System.out.println();
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(cmd);
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println();
        ChannelExec ce = (ChannelExec) session.openChannel("exec");
        ce.setCommand(cmd);
        OutputStream out = ce.getOutputStream();
        InputStream in = ce.getInputStream();
        ce.connect(30000);
        while (!ce.isEOF()) {
            while (in.available() > 0) {
                int i = in.read();
                System.out.print((char) i);
            }
        }

        int r = ce.getExitStatus();

        ce.disconnect();

        if (r != 0)
            System.out.println("WARNING: Return code " + r);
        return r;
    }

    // ----------------------------------------------------------------------------------------------------
    private ArrayList<FileInfo> _listFolder(ChannelSftp ftpChnl, String path) {

        ArrayList<FileInfo> list = new ArrayList<FileInfo>();

        try {
            //System.out.println("Listing folder: '" + path + "'");
            Vector v = ftpChnl.ls(path);
            for (int n = 0; n < v.size(); n++) {
                LsEntry line = (LsEntry) v.get(n);
                @SuppressWarnings("synthetic-access")
                FileInfo fi = new FileInfo();
                fi.isDir = line.getAttrs().isDir();
                fi.name = line.getFilename();
                fi.size = _parseLong(line.getLongname().substring(31, 43));
                list.add(fi);
            }
        } catch (SftpException ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    // ----------------------------------------------------------------------------------------------------
    private long _parseLong(String s) {
        try {
            return Long.parseLong(s.trim());
        } catch (Throwable th) {
            return 0;
        }
    }


}
