/**
 * 
 */
package com.jzb.je;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author jzarzuela
 * 
 */
public class Tester {

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
            Tester me = new Tester();
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

        String projectName = "SWT_FindDuplicated";
        _parseClasspathFile(_projectNameToFile(projectName));
    }

    // ----------------------------------------------------------------------------------------------------
    private File _projectNameToFile(String name) {

        File baseFolders[] = { new File("/Users/jzarzuela/Documents/WKSP/Consolidado"), new File("/Users/jzarzuela/Documents/java-Campus") };

        if (name.startsWith("/")) {
            name = name.substring(1);
        }

        for (File baseFolder : baseFolders) {
            File prjFolder = new File(baseFolder, name);
            if (prjFolder.exists() && prjFolder.isDirectory()) {
                return prjFolder;
            }
        }

        // Ups!!!
        return null;
    }

    // ----------------------------------------------------------------------------------------------------
    private void _parseClasspathFile(File projectFolder) {

        System.out.println("------- projectName: "+projectFolder);
        File classpathFile = new File(projectFolder, ".classpath");
        if (!classpathFile.exists())
            // Ups!!!
            return;

        try (InputStream is = new FileInputStream(classpathFile)) {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            Document doc = docBuilder.parse(new InputSource(is));

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            NodeList nlist = (NodeList) xpath.evaluate("/classpath/classpathentry", doc, XPathConstants.NODESET);
            for (int n = 0; n < nlist.getLength(); n++) {
                Node node = nlist.item(n);
                String kind = node.getAttributes().getNamedItem("kind").getNodeValue();
                String path = node.getAttributes().getNamedItem("path").getNodeValue();
                System.out.println(kind + " " + path);
                switch (kind) {
                    case "src":
                        File subProject = _projectNameToFile(path);
                        if (subProject != null) {
                            _parseClasspathFile(subProject);
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
