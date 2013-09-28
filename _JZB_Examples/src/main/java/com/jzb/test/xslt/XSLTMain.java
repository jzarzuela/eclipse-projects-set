/**
 * 
 */
package com.jzb.test.xslt;

import java.io.FileInputStream;
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xalan.xsltc.trax.TransformerFactoryImpl;

/**
 * @author n63636
 * 
 */
public class XSLTMain {

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
            XSLTMain me = new XSLTMain();
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
        xlstTransformation();
    }
    
    public void xlstTransformation() throws Exception {
        
        String xsltFile = "D:\\JZarzuela\\D_S_Escritorio\\xml\\Sat-cfg.xsl";
        String xmlFile = "D:\\JZarzuela\\D_S_Escritorio\\xml\\VariableCFG.xml";
        
        Source xsltSrc = new StreamSource(xsltFile);
        Source inXML = new StreamSource(new FileInputStream(xmlFile));
        StringWriter sw = new StringWriter();
        Result outXML = new StreamResult(sw);

        TransformerFactory factoryTransformer = null;
        factoryTransformer = TransformerFactory.newInstance();
        Transformer transformer = factoryTransformer.newTransformer(xsltSrc);

        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.setParameter("PARAM_WHICH_CODE", "0009");

        transformer.transform(inXML, outXML);
        System.out.println(sw.getBuffer());
    }
    
    public void xlstCompiledTransformation() throws Exception {
        
        String xsltFile = "D:\\JZarzuela\\D_S_Escritorio\\xml\\Sat-cfg.xsl";
        String xmlFile = "D:\\JZarzuela\\D_S_Escritorio\\xml\\VariableCFG.xml";
        Source xsltSrc = new StreamSource(xsltFile);
        Source inXML = new StreamSource(new FileInputStream(xmlFile));
        StringWriter sw = new StringWriter();
        Result outXML = new StreamResult(sw);

        // Set the TransformerFactory system property to generate and use a translet.
        // Note: To make this sample more flexible, load properties from a properties file.
        // The setting for the Xalan Transformer is "org.apache.xalan.processor.TransformerFactoryImpl"
        // String key = "javax.xml.transform.TransformerFactory";
        // String value = "org.apache.xalan.xsltc.trax.TransformerFactoryImpl";
        // Properties props = System.getProperties();
        // props.put(key, value);
        // System.setProperties(props);

        org.apache.xalan.xsltc.trax.TransformerFactoryImpl tFactory = new org.apache.xalan.xsltc.trax.TransformerFactoryImpl();
        tFactory.setAttribute(TransformerFactoryImpl.USE_CLASSPATH, Boolean.TRUE);
        tFactory.setAttribute(TransformerFactoryImpl.PACKAGE_NAME, "com.jzb.xslt.comp");
        tFactory.setAttribute(TransformerFactoryImpl.GENERATE_TRANSLET, Boolean.FALSE);
        Transformer transformer = tFactory.newTransformer(xsltSrc);

        transformer.setParameter("PARAM_WHICH_CODE", "0001");

        // Perform the transformation from a StreamSource to a StreamResult;
        transformer.transform(inXML , outXML);

        System.out.println(sw.getBuffer());
    }


}
