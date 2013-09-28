package com.jzb.vt;

import java.io.File;

import com.jzb.vt.model.ExcelSheet;
import com.jzb.vt.model.VItem;
import com.jzb.vt.model.VItemList;

/**
 * @author PS00A501
 */
public class MainHTML {

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
            MainHTML me = new MainHTML();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
            ExceptionMessage.showExceptionMessage(th);
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

        String inputFile = getInputDataFileName(args);

        VItemList itemList = ExcelSheet.readData(inputFile);

        StringBuffer sb = new StringBuffer();

        printHeader(sb);
        String theme = "";
        itemList.organizeListByThemeAndDate(null);
        while(itemList.hasNextVitem()) {
            VItem item = itemList.moveOnNextVItem();
            if (!theme.equals(item.getTheme())) {
                theme = item.getTheme();
                printRowHeader(item, sb);
            }
            printRow(item, sb);
        }
        printEnd(sb);

        HTMLViewer window = new HTMLViewer();
        window.open(sb.toString());
    }

    private void useHelp(boolean terminate) {

        System.out.println();
        System.out.println("Use: <Name of a Excel existing file to use>");
        System.out.println();
        if (terminate)
            System.exit(-1);
    }

    private String getInputDataFileName(String[] args) throws Exception {

        if (args.length != 1) useHelp(true);
        
        String fName=args[0];
        
        File inFile = new File(fName);
        System.out.println(inFile);
        if(!inFile.exists()) useHelp(true);
        
        return fName;

    }
    
    private void printHeader(StringBuffer sb) {
        sb.append("<HTML>\n<HEAD>\n<TITLE>Vocabulary list</TITLE>\n</HEAD>\n<BODY>\n<" + "TABLE border='1' CELLPADDING='5' CELLSPACING='2'>\n");
    }

    private void printEnd(StringBuffer sb) {
        sb.append("</TABLE>\n</BODY>\n</HTML>");
    }

    private void printRowHeader(VItem item, StringBuffer sb) {
        sb.append("<TR><TD COLSPAN='2' BGCOLOR='#99CCFF'><CENTER><B>");
        sb.append(item.getTheme());
        sb.append("</B></CENTER></TD></TR>");
    }

    private void printRow(VItem item, StringBuffer sb) {
        sb.append("<TR>");
        sb.append("<TD>");
        sb.append(styledText(item.getNativeText()));
        sb.append("</TD>");
        sb.append("<TD>");
        sb.append(styledText(item.getTranslatedText()));
        sb.append("</TD>");
        sb.append("</TR>\n");
    }

    private String styledText(String text) {

        boolean marking = false;
        char buffer[] = new char[text.length()];
        text.getChars(0, buffer.length, buffer, 0);
        StringBuffer sb = new StringBuffer(buffer.length);
        for (int n = 0; n < buffer.length; n++) {
            if (buffer[n] != '|') {
                sb.append(buffer[n]);
            } else {
                if (marking) {
                    sb.append("</B></FONT>");
                    marking = false;
                } else {
                    marking = true;
                    sb.append("<FONT COLOR='#FF0000'><B>");
                }
            }
        }
        return sb.toString();
    }

}
