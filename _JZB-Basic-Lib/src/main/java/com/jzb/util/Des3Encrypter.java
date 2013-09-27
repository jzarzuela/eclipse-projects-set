/**
 * 
 */
package com.jzb.util;

import java.io.ByteArrayOutputStream;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * @author n63636
 * 
 */
public class Des3Encrypter {

    private static final byte[]     salt  = { (byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c, (byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99 };
    private static final int        count = 20;
    private static SecretKey        m_secretKey;
    private static Cipher           m_cipher;
    private static PBEParameterSpec m_paramSpec;

    private static void init() throws Exception {

        System.setProperty("java.security.debug", "all");

        String secretPassword = "#u2r3o$cb4m&p7dk@fj8d9k3l&4x5cc";
        PBEKeySpec pbeKeySpec = new PBEKeySpec(secretPassword.toCharArray());

        SecretKeyFactory keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndTripleDES", "SunJCE");
        m_secretKey = keyFac.generateSecret(pbeKeySpec);

        m_paramSpec = new PBEParameterSpec(salt, count);

        m_cipher = Cipher.getInstance("PBEWithMD5AndTripleDES", "SunJCE");

    }

    public static String encryptStr(String toEncrypt) throws Exception {

        byte b[] = encrypt(toEncrypt.getBytes());
        return new String(Base64.encode(b));
    }

    public static byte[] encrypt(byte[] toEncrypt) throws Exception {

        if (m_cipher == null)
            init();

        byte[] result = null;
        m_cipher.init(Cipher.ENCRYPT_MODE, m_secretKey, m_paramSpec);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(m_cipher.doFinal(toEncrypt));
        result = baos.toByteArray();
        return result;

    }

    public static String decryptStr(String toDecrypt) throws Exception {
        byte b[] = Base64.decode(toDecrypt);
        byte b2[] = decrypt(b);
        return new String(b2);
    }

    public static byte[] decrypt(byte[] toDecrypt) throws Exception {

        if (m_cipher == null)
            init();

        byte[] result = null;
        m_cipher.init(Cipher.DECRYPT_MODE, m_secretKey, m_paramSpec);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(m_cipher.doFinal(toDecrypt));
        result = baos.toByteArray();
        return result;
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
            Des3Encrypter me = new Des3Encrypter();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
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

        String s = encryptStr("un ejemplo");
        System.out.println(s);
        System.out.println(decryptStr(s));
        Des3Encrypter.decryptStr("");

    }
}
