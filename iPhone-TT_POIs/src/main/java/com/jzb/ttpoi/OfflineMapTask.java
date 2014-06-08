/**
 * 
 */
package com.jzb.ttpoi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * @author jzarzuela
 * 
 */
public class OfflineMapTask implements Runnable {

    private OfflineMap m_owner;
    private File m_tileFile;
    private URL  m_tileUrl;

    /**
     * 
     */
    public OfflineMapTask(OfflineMap owner, File tileFile, URL tileUrl) {
        m_owner = owner;
        m_tileFile = tileFile;
        m_tileUrl = tileUrl;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        
        int size = 0;
        try {
            System.out.println("Downloading map tile '" + m_tileFile + "'");
            size = _downloadMapTile(m_tileFile, m_tileUrl);            
        } catch (Throwable th) {
            System.out.println(th);
        }
        m_owner.taskEnded(size);
        
        //m_owner.taskEnded(20000);
    }

    private int _downloadMapTile(File tileFile, URL tileLink) throws Exception {
        int size = 0;
        byte buffer[] = new byte[65536];
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tileFile)); InputStream bis = new BufferedInputStream(tileLink.openStream())) {
            for (;;) {
                int len = bis.read(buffer);
                if (len > 0) {
                    size+=len;
                    bos.write(buffer, 0, len);
                } else {
                    break;
                }
            }
        }
        return size;

    }
}
