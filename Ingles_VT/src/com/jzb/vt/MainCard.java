/**
 * Rigel Services Model Infrastructure, Version 1.0 Copyright (C) 2002 ISBAN. All Rights Reserved.
 */

package com.jzb.vt;

import java.io.File;

import com.jzb.vt.model.ExcelSheet;
import com.jzb.vt.model.VItemList;

/**
 * @author PS00A501
 */
public class MainCard {

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
            MainCard me = new MainCard();
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

        itemList.setDirectTranslation(false);
        itemList.organizeListByAlreadyRead();

        VCard cardWnd = new VCard(itemList);
        cardWnd.open();

        ExcelSheet.writeData(inputFile, itemList);

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

}
