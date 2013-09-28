package com.jzb.test.ssh;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

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
import com.jzb.util.Des3Encrypter;

/**
 * 
 */

/**
 * @author n000013
 * 
 */
public class Test1 {

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
                throw new RuntimeException("Error decrypting password",ex);
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
            Test1 me = new Test1();
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
        Channel channel;

        JSch jsch = new JSch();
        Session session = jsch.getSession("root", "127.0.0.1", 22);
        // session.setPassword("your password");
        UserInfo ui = new MyUserInfo();
        session.setUserInfo(ui);
        session.setConfig("StrictHostKeyChecking", "no");

        // -------- CONNECT
        session.connect();
        ChannelSftp ftpChnl = _connectFTP(session);
        _createRFolder(ftpChnl, RINSTFOLDER);

        // -------- INSTALL FILE
        _installIPA(session, ftpChnl, "F:\\jb-apps\\_Games\\crk_N[Urinals]_PK[com.bluebirddev.urinals]_V[1.0]_OS[2.2.1]_D[2009-08].ipa");

        // -------- TERMINATE AND RESTART
        _execCommand(session, "killall SpringBoard");
        _disconnectFTP(ftpChnl);
        session.disconnect();

    }

    private static final String RINSTFOLDER         = "/tmp/inst2";
    private static final String RINSTFOLDER_PAYLOAD = "/tmp/inst2/Payload";

    private void _installIPA(Session session, ChannelSftp ftpChnl, String ipaFName) throws Exception {

        File ipaFile = new File(ipaFName);
        String rname = RINSTFOLDER + "/" + ipaFile.getName();

        // -------- SEND FILE
        _sendFile(ftpChnl, ipaFile, rname);

        // --------- PROCESS FILE
        _execCommand(session, "unzip -X -K -qq -o \"" + rname + "\" -d " + RINSTFOLDER);
        _execCommand(session, "chmod 0777 " + RINSTFOLDER + " -R");
        String appFolder=_execCommand2(session, "ls -D " + RINSTFOLDER_PAYLOAD);
        int p=appFolder.indexOf(".app");
        if(p<0) {
            System.out.println("ERROR: .app Folder not found in: "+appFolder);
            return;
        }
        appFolder=appFolder.substring(0,p+4);

        _execCommand(session, "mv -f " + RINSTFOLDER_PAYLOAD + "/"+appFolder+" /Applications/_"+appFolder);

        _execCommand(session, "rm -f -r " + RINSTFOLDER + "/*");
        //_execCommand(session, "rmdir " + RINSTFOLDER_PAYLOAD);
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

    private void _createRFolder(ChannelSftp c, String rfolder) throws Exception {

        System.out.println("Creating remote folder (if doesn't exist): " + rfolder);
        try {
            c.mkdir(rfolder);
        } catch (SftpException e) {
        }
        c.cd(rfolder);
    }

    private void _sendFile(ChannelSftp c, File localFile, String remoteName) throws Exception {
        FileInputStream fis = new FileInputStream(localFile);
        c.put(fis, remoteName, new MyMonitor(localFile.length()), c.OVERWRITE);
    }

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

    private String _execCommand2(Session session, String cmd) throws Exception {

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
        
        StringBuffer sb= new StringBuffer(); 
        while (!ce.isEOF()) {
            while (in.available() > 0) {
                int i = in.read();
                System.out.print((char) i);
                sb.append((char)i);
            }
        }

        int r = ce.getExitStatus();

        ce.disconnect();

        if (r != 0)
            System.out.println("WARNING: Return code " + r);
        
        return sb.toString();
    }
}
