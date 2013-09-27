/**
 * 
 */
package com.jzb.util;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author n63636
 * 
 */
public class AESCypher {

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
            AESCypher me = new AESCypher();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
            System.exit(1);
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
            System.exit(-1);
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

        String val;
        
        val = encryptStr("cosa","pwd");
        System.out.println(val);
        
        val = decryptStr(val,"pwd");
        System.out.println(val);
        
        val = "otra cosa cifrada";
        val = decryptStr(val,"mega password");
        System.out.println(val);
    }

    public static String encryptStr(String toEncrypt, String pwd) throws Exception {

        byte b[] = encrypt(toEncrypt.getBytes(), pwd);
        return new String(Base64.encode(b));
    }

    public static byte[] encrypt(byte[] toEncrypt, String key) throws Exception {

        MessageDigest digester = MessageDigest.getInstance("MD5");
        char[] password = key.toCharArray();
        for (int i = 0; i < password.length; i++) {
        digester.update((byte) password[i]);
        }
        byte[] passwordData = digester.digest();
        Key secretkey = new SecretKeySpec(passwordData, "AES");        
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, secretkey);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(aesCipher.doFinal(toEncrypt));
        byte[] result = baos.toByteArray();
        return result;

    }

    public static String decryptStr(String toDecrypt, String pwd) throws Exception {
        byte b[] = Base64.decode(toDecrypt);
        byte b2[] = decrypt(b, pwd);
        return new String(b2);
    }

    public static byte[] decrypt(byte[] toDecrypt, String key) throws Exception {

        MessageDigest digester = MessageDigest.getInstance("MD5");
        char[] password = key.toCharArray();
        for (int i = 0; i < password.length; i++) {
        digester.update((byte) password[i]);
        }
        byte[] passwordData = digester.digest();
        Key secretkey = new SecretKeySpec(passwordData, "AES");        
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, secretkey);

        byte[] result = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(aesCipher.doFinal(toDecrypt));
        result = baos.toByteArray();
        return result;
    }

}
