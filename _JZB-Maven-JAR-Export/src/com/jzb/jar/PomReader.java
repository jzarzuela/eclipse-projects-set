/**
 * 
 */
package com.jzb.jar;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.TreeMap;

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
public class PomReader {

    // ----------------------------------------------------------------------------------------------------
    private static enum DepType {

        POM("0"), POM_MINE("1"), EXT_JAR("2"), OTHER("3"), SNAPSHOT("4");

        private String OT;

        private DepType(String ot) {
            this.OT = ot;
        }

        public String getOT() {
            return OT;
        }
    }

    private static class DependencyInfo {

        public DepType type = DepType.OTHER;
        public String  groupId;
        public String  artifactId;
        public String  version;

        public DependencyInfo() {
            this.type = type;
        }

        public String getKey() {
            return type.getOT() + "#" + groupId + "#" + artifactId + "#" + version;
        }

        public String toString() {
            String val = "";
            if (type == DepType.POM) {
                val += "<!-- POM file -->\n";
            }
            if (type == DepType.POM_MINE) {
                val += "<!-- POM mine file -->\n";
            }
            if (type == DepType.EXT_JAR) {
                val += "<!-- EXT_JAR file -->\n";
            }
            if (type == DepType.SNAPSHOT) {
                val += "<!-- SNAPSHOT POM file -->\n";
            }
            val += "<dependency>\n";
            val += "  <groupId>" + groupId + "</groupId>\n";
            val += "  <artifactId>" + artifactId + "</artifactId>\n";
            val += "  <version>" + version + "</version>\n";
            val += "</dependency>\n";
            return val;
        }
    }

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
            PomReader me = new PomReader();
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

        Path basePath = Paths.get("/Users/jzarzuela/Documents/Java/github-repos/eclipse-projects-set");

        final TreeMap<String, DependencyInfo> poms = new TreeMap();

        Files.walkFileTree(basePath, EnumSet.noneOf(FileVisitOption.class), 2, new SimpleFileVisitor<Path>() {

            @SuppressWarnings("synthetic-access")
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                if (path.getFileName().toString().equals("pom.xml")) {
                    try {
                        _processPOM(path, poms);
                    } catch (Exception ex) {
                        System.out.println("Error processing pom file: " + path);
                        ex.printStackTrace();
                    }

                }
                return FileVisitResult.CONTINUE;
            }
        });

        for (DependencyInfo di:poms.values()) {
            System.out.println(di);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _processPOM(Path path, TreeMap<String, DependencyInfo> poms) throws Exception {

        Document doc = null;

        try (InputStream is = new FileInputStream(path.toFile())) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            doc = docBuilder.parse(new InputSource(is));
        }

        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();

        String packaging = xpath.evaluate("/project/packaging/text()", doc);

        DependencyInfo di = new DependencyInfo();
        di.groupId = xpath.evaluate("/project/groupId/text()", doc);
        di.artifactId = xpath.evaluate("/project/artifactId/text()", doc);
        di.version = xpath.evaluate("/project/version/text()", doc);
        di.type = DepType.POM;
        if (di.artifactId.contains("jzb")) {
            di.type = DepType.POM_MINE;
        } 
        if (di.groupId.contains("JARs")) {
            di.type = DepType.EXT_JAR;
        } 
        if (di.version.contains("SNAPSHOT")) {
            di.type = DepType.SNAPSHOT;
        }
        
        if (!packaging.equals("jar") && !di.groupId.equals("SWT-4_3") && !di.version.contains("SNAPSHOT")) {
            poms.put(di.getKey(), di);
        }

        NodeList nlist = (NodeList) xpath.evaluate("/project/dependencies/dependency", doc, XPathConstants.NODESET);
        for (int n = 0; n < nlist.getLength(); n++) {
            Node node = nlist.item(n);

            String scope = xpath.evaluate("/project/scope/text()", node);
            if (scope.equals("test")) {
                continue;
            }

            DependencyInfo di2 = new DependencyInfo();
            di2.groupId = xpath.evaluate("groupId/text()", node);
            di2.artifactId = xpath.evaluate("artifactId/text()", node);
            di2.version = xpath.evaluate("version/text()", node);
            di2.type = DepType.OTHER;
            if (di2.artifactId.contains("jzb")) {
                di2.type = DepType.POM_MINE;
            } 
            if (di2.groupId.contains("JARs")) {
                di2.type = DepType.EXT_JAR;
            } 
            if (di2.version.contains("SNAPSHOT")) {
                di2.type = DepType.SNAPSHOT;
            }

            if (!di2.groupId.equals("SWT-4_3")) {
                poms.put(di2.getKey(), di2);
            }
        }
    }
}
