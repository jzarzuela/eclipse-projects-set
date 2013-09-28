/**
 * 
 */
package com.jzb.test.ssh;

import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.jcraft.jsch.UserInfo;
import com.jzb.util.AESCypher;

/**
 * @author jzarzuela
 * 
 */
public class MyUserInfo implements UserInfo {

    String     passwd;
    JTextField passwordField = new JPasswordField(20);

    public String getPassphrase() {
        System.out.println("-- MT --> Test1.MyUserInfo.enclosing_method(enclosing_method_arguments)");
        return null;
    }

    public String getPassword() {
        try {
            return AESCypher.decryptStr("gmqbpuCTTk0nF25/hg+1mA==");
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