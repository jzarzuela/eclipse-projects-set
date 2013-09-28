package com.jzb.test.ssh;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;
import com.jcraft.jsch.UserInfo;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jzb.util.Des3Encrypter;

/**
 * 
 */

/**
 * @author n000013
 * 
 */
public class ListTest {

    public static class MyMonitor implements SftpProgressMonitor {

        private long m_cur  = 0;
        private int  m_iter = 0;
        private long m_max;

        public MyMonitor(long max) {
            m_max = max;
        }

        public boolean count(long count) {
            // System.out.println("-- MT --> Test1.MyMonitor.count(" + count + ")");
            m_cur += count;
            if (m_max > 0) {
                long p = 100L * m_cur / m_max;
                System.out.print(p + "% ");
            } else {
                System.out.print(m_cur + " ");
            }
            m_iter = (m_iter + 1) % 10;
            if (m_iter == 0)
                System.out.println();
            return true;
        }

        public void end() {
            System.out.println("-- MT --> Test1.MyMonitor.end()");
        }

        public void init(int op, String src, String dest, long max) {
            m_cur = 0;
            m_max = max;
            System.out.println("-- MT --> Test1.MyMonitor.init(" + op + ", " + src + ", " + dest + ", " + max + ")");
        }
    }

    public static class MyUserInfo implements UserInfo {

        String     passwd;
        JTextField passwordField = new JPasswordField(20);

        public String getPassphrase() {
            System.out.println("-- MT --> Test1.MyUserInfo.enclosing_method(enclosing_method_arguments)");
            return null;
        }

        public String getPassword() {
            try {
                return Des3Encrypter.decryptStr("WvO3H3yRW2nPsNpQjcGPFA==");
            } catch (Exception ex) {
                throw new RuntimeException("Error decrypting password", ex);
            }
            // return passwd;
        }

        public boolean promptPassphrase(String message) {
            return true;
        }

        public boolean promptPassword(String message) {
            return true;
            // Object[] ob = { passwordField };
            // int result = JOptionPane.showConfirmDialog(null, ob, message, JOptionPane.OK_CANCEL_OPTION);
            // if (result == JOptionPane.OK_OPTION) {
            // passwd = passwordField.getText();
            // return true;
            // } else {
            // return false;
            // }
        }

        public boolean promptYesNo(String message) {
            return true;
        }

        public void showMessage(String message) {
            System.out.println("-- MT --> Test1.MyUserInfo.showMessage(" + message + ")");
        }
    }

    private static class FileInfo {

        boolean isDir;
        String  name;
        long    size;
    }

    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("***** TEST STARTED *****");
            ListTest me = new ListTest();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
            System.exit(0);
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
            System.exit(1);
        }
    }

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
        Session session = jsch.getSession("root", "180.112.104.241", 22);
        UserInfo ui = new MyUserInfo();
        session.setUserInfo(ui);
        session.setConfig("StrictHostKeyChecking", "no");

        // -------- CONNECT
        session.connect();
        ChannelSftp ftpChnl = _connectFTP(session);

        // -------- MEASURE
        _measureApps(ftpChnl);
        //long l=_measureFolder(ftpChnl,"/User/Applications/D15F7B0D-7FA5-4009-832C-DEEED406AC81");
        //String name=_getAppName(ftpChnl,"/User/Applications/D15F7B0D-7FA5-4009-832C-DEEED406AC81");
        //System.out.println(name + " " + l);

        // -------- TERMINATE AND RESTART
        _disconnectFTP(ftpChnl);
        session.disconnect();

    }

    private void _measureApps(ChannelSftp ftpChnl) {

        TreeMap<String, Long> appsInfo = new TreeMap<String, Long>();

        ArrayList<FileInfo> list = _listFolder(ftpChnl, "/User/Applications");

        for (FileInfo fi : list) {
            if (fi.isDir && !(fi.name.equals(".") || fi.name.equals(".."))) {
                String name = _getAppName(ftpChnl, "/User/Applications/" + fi.name);
                long size = _measureFolder(ftpChnl, "/User/Applications/" + fi.name);
                if (name == null) {
                    name = fi.name;
                }
                appsInfo.put(name, size);
            }
        }

        System.out.println("\n*****************************************************");
        for (Entry<String, Long> entry : appsInfo.entrySet()) {
            System.out.println(entry.getKey()+", "+entry.getValue());
        }
    }

    private String _getAppName(ChannelSftp ftpChnl, String path) {
        ArrayList<FileInfo> list = _listFolder(ftpChnl, path);
        for (FileInfo fi : list) {
            if (fi.isDir && fi.name.toLowerCase().endsWith(".app")) {
                return fi.name;
            }
        }
        return null;
    }

    private long _measureFolder(ChannelSftp ftpChnl, String path) {
        long size = 0;
        ArrayList<FileInfo> list = _listFolder(ftpChnl, path);
        for (FileInfo fi : list) {
            size += fi.size;
            if (fi.isDir && !(fi.name.equals(".") || fi.name.equals(".."))) {
                size += _measureFolder(ftpChnl, path + "/" + fi.name);
            }
        }
        return size;
    }

    private ChannelSftp _connectFTP(Session session) throws Exception {
        System.out.println("Connecting FTP channel");
        ChannelSftp c = (ChannelSftp) session.openChannel("sftp");
        c.connect(30000);
        return c;
    }

    private void _disconnectFTP(ChannelSftp c) throws Exception {
        System.out.println("Disconnecting FTP channel");
        c.exit();
    }

    private ArrayList<FileInfo> _listFolder(ChannelSftp ftpChnl, String path) {

        ArrayList<FileInfo> list = new ArrayList<FileInfo>();

        try {
            //System.out.println("Listing folder: '" + path + "'");
            Vector v = ftpChnl.ls(path);
            for (int n = 0; n < v.size(); n++) {
                LsEntry line = (LsEntry) v.get(n);
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

    private long _parseLong(String s) {
        try {
            return Long.parseLong(s.trim());
        } catch (Throwable th) {
            return 0;
        }
    }
}
