/**
 * 
 */
package com.jzb.test.mmhttpServer;

import java.io.File;
import java.util.HashMap;

import mmhttp.protocol.InputStreamResponse;
import mmhttp.protocol.Request;
import mmhttp.protocol.Response;
import mmhttp.protocol.SimpleResponse;
import mmhttp.server.Responder;
import mmhttp.server.Server;

/**
 * @author n63636
 * 
 */
public class FileResponder implements Responder {

    private File m_baseFolders[];

    private HashMap<String, String> m_ctMaps = new HashMap<String, String>();

    public FileResponder(File baseFolders[]) {
        m_baseFolders = baseFolders;
        _loadCTMap();
    }
    
    public Response makeResponse(Server server, Request request) throws Exception {

        String resName=request.getResource();
        File resFile = _findResource(resName );
        if (resFile!=null) {
            String contenType=_getContenType(resName);
            InputStreamResponse isr = new InputStreamResponse();
            isr.setStatus(200);
            isr.setContentType(contenType);
            isr.setBody(resFile);
            System.out.println("Resource found: "+resFile);
            return isr;
        }
        else {
            System.out.println("Resource NOT found: "+resName);
            return new SimpleResponse(404,"Resource not found: "+resName);
        }
    }

    private File _findResource(String resName) {
        for(File bf:m_baseFolders) {
            File resFile=new File(bf,resName);
            System.out.println(resFile.getAbsolutePath());
            if(resFile.exists()) return resFile;
        }
        return null;
    }

    private String _getContenType(String resName) {

        String ext;

        int p1 = resName.lastIndexOf('.');
        if (p1 >= 0) {
            ext = resName.substring(p1).toLowerCase();
        } else {
            ext = resName.toLowerCase();
        }

        String ct = m_ctMaps.get(ext);
        if (ct == null) {
            return "unknown";
        } else {
            return ct;
        }
    }

    private void _loadCTMap() {
        m_ctMaps.put("png", "image/png");
        m_ctMaps.put("jpg", "image/jpeg");
        m_ctMaps.put("gif", "image/gif");
        m_ctMaps.put("bmp", "image/x-ms-bmp");

        m_ctMaps.put("txt", "text/plain");

        m_ctMaps.put("htm", "text/html");
        m_ctMaps.put("html", "text/html");

        m_ctMaps.put("css", "text/css");
        m_ctMaps.put("js", "text/javascript");
        
        m_ctMaps.put("pdf", "application/pdf");
        m_ctMaps.put("rtf", "application/rtf");
        m_ctMaps.put("doc", "application/msword");
        m_ctMaps.put(".xls", "application/ms-excel");
        m_ctMaps.put(".xlsx", "application/ms-excel");
    }

}
