/**
 * 
 */
package com.jzb.test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;

/**
 * @author jzarzuela
 * 
 */
public class FileHashing {

    private static final char[] _HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', };
    private static final int    CHUNK_SIZE = 102400;                                                                               // 1024;
    private static final int    NUM_CHUNKS = 3;
    private ByteBuffer          m_byteBuffer;
    private MessageDigest       m_md5;
    private int                 m_totalReadLength;

    // ----------------------------------------------------------------------------------------------------
    public FileHashing() throws Exception {
        m_md5 = MessageDigest.getInstance("MD5");
        m_byteBuffer = ByteBuffer.allocateDirect(NUM_CHUNKS * CHUNK_SIZE);
    }

    // ----------------------------------------------------------------------------------------------------
    public String processFile(final File file) throws Exception {

        // Lee la informacion
        _readFileData(file);

        // Calcula el HASHING del fichero
        String hashing = _calcFileHashing(file);

        // Retorna el valor
        return hashing;
    }

    // ----------------------------------------------------------------------------------------------------
    private void _readFileData(File file) throws IOException {

        // Reinicia el buffer de lectura
        m_byteBuffer.rewind();
        m_byteBuffer.limit(m_byteBuffer.capacity());
        m_totalReadLength = 0;

        // Asi como el numero de lecturas atendiendo al tamaño
        int totalReadingsCount;
        long skipPos;
        if (file.length() > NUM_CHUNKS * CHUNK_SIZE) {
            totalReadingsCount = NUM_CHUNKS;
            skipPos = (file.length() - CHUNK_SIZE) / (NUM_CHUNKS - 1);
        } else {
            totalReadingsCount = 1;
            skipPos = 0;
        }

        // Crea el canal asincrono para el fichero a procesar
        try (FileChannel fChannel = FileChannel.open(file.toPath(), StandardOpenOption.READ)) {

            // Lee la informacion
            long currentPos = 0;
            for (int n = 0; n < totalReadingsCount; n++) {

                // Va incrementando el limite de lectura del buffer en cada iteracion para albergar el siguiente "chunk"
                if (totalReadingsCount > 1) {
                    m_byteBuffer.limit((n + 1) * CHUNK_SIZE);
                } else {
                    m_byteBuffer.limit((int) file.length());
                }

                // Actualiza la posicion de lectura
                currentPos = skipPos * n;
                int len = fChannel.read(m_byteBuffer, currentPos);
                if (len < 0) {
                    throw new IOException("No data was read from file channel");
                } else {
                    m_totalReadLength += len;
                }
            }
        }

    }

    // ----------------------------------------------------------------------------------------------------
    private String _calcFileHashing(File file) throws Exception {

        // Reinicia el calculo de hashing
        m_md5.reset();

        // Guarda como parte de la informacion la longitud del fichero
        // Salvo que sea cero (vacio) en ese caso mete el nombre
        // (Mismo HASHING si nombre es igual y esta vacio)
        if (file.length() > 0) {
            m_md5.update(_longToByteArray(file.length()));
        } else {
            m_md5.update(file.getName().getBytes("UTF-8"));
        }

        // Actualiza el calculo con los datos leidos
        m_byteBuffer.rewind();
        m_byteBuffer.limit(m_totalReadLength);
        m_md5.update(m_byteBuffer);

        // Realiza el calculo y lo retorna
        String hash = _toHex(m_md5.digest());
        return hash;
    }

    // ----------------------------------------------------------------------------------------------------
    private byte[] _longToByteArray(long l) {

        // Guarda como parte de la informacion la longitud del fichero
        byte buffer[] = new byte[8];
        for (int n = 0; n < 8; n++) {
            buffer[n] = (byte) (l & 0x0FF);
            l = l >> 8;
        }
        return buffer;
    }

    // ----------------------------------------------------------------------------------------------------
    private String _toHex(byte hash[]) {

        StringBuffer buf = new StringBuffer(hash.length * 2);

        for (int idx = 0; idx < hash.length; idx++)
            buf.append(_HEX_CHARS[(hash[idx] >> 4) & 0x0f]).append(_HEX_CHARS[hash[idx] & 0x0f]);

        return buf.toString();
    }

}
