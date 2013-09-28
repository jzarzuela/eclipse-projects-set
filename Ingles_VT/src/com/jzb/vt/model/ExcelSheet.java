/**
 * Rigel Services Model Infrastructure, Version 1.0 Copyright (C) 2002 ISBAN. All Rights Reserved.
 */

package com.jzb.vt.model;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import jxl.Cell;
import jxl.DateCell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * @author PS00A501
 */
public class ExcelSheet {

    public static final String LABEL_Native = "Native";

    public static void writeData(String fileName, VItemList itemList) throws Exception {

        System.setProperty("jxl.nopropertysets", "true");
        
        SimpleDateFormat sdf=new SimpleDateFormat("_yyyy-MM-dd_HH_mm_ss");
        File fo = new File(fileName);
        File fi = new File(fileName+sdf.format(new Date())+".bak");
        
        fi.delete();
        if(!fo.renameTo(fi)) {
            throw new Exception("File couldn't be renamed to backup the information: "+fi.getAbsolutePath());
        }
        
        Workbook wb = Workbook.getWorkbook(fi);
        Sheet sheet = wb.getSheet(0);
        Cell label = sheet.findLabelCell(LABEL_Native);
        if (label == null) {
            throw new Exception("Excel sheet doesn't have a cell labeled as: " + LABEL_Native);
        }

        int icol = label.getColumn();
        
        WritableWorkbook owb = Workbook.createWorkbook(fo,wb);
        WritableSheet wsheet=owb.getSheet(0);
        
        itemList.organizeListAllTogether();
        while(itemList.hasNextVitem()) {
            VItem item=itemList.moveOnNextVItem();
            wsheet.addCell(new Number(icol+4,item.getRowIndex(),(double)item.getTimesRead()));
            wsheet.addCell(new Number(icol+5,item.getRowIndex(),(double)item.getTimesFailed()));
            wsheet.addCell(new Number(icol+6,item.getRowIndex(),(double)item.getTimesInvRead()));
            wsheet.addCell(new Number(icol+7,item.getRowIndex(),(double)item.getTimesInvFailed()));
        }
        
        owb.write();
        owb.close();
        
    }
            
    public static VItemList readData(String fileName) throws Exception {

        System.setProperty("jxl.nopropertysets", "true");
        
        VItemList itemList = new VItemList();

        File fi = new File(fileName);
        if(!fi.exists()) {
            throw new Exception("File couldn't be opened: "+fi.getAbsolutePath()); 
        }
        
        Workbook wb = Workbook.getWorkbook(fi);
        Sheet sheet = wb.getSheet(0);
        Cell label = sheet.findLabelCell(LABEL_Native);
        if (label == null) {
            throw new Exception("Excel sheet doesn't have a cell labeled as: " + LABEL_Native);
        }

        int irow = 1+label.getRow();
        int icol = label.getColumn();
        int maxRow = sheet.getRows();

        for (; irow < maxRow; irow++) {
            VItem item = new VItem();

            Cell data[] = sheet.getRow(irow);

            if(readText(data, icol + 0)==null) continue;
            item.setRowIndex(irow);
            item.setNativeText(readText(data, icol + 0));
            item.setTranslatedText(readText(data, icol + 1));
            item.setCreationDate(readDate(data, icol + 2));
            item.setTheme(readText(data, icol + 3));
            item.setTimesRead(readInt(data, icol + 4));
            item.setTimesFailed(readInt(data, icol + 5));
            item.setTimesInvRead(readInt(data, icol + 6));
            item.setTimesInvFailed(readInt(data, icol + 7));

            itemList.addVItem(item);
        }
        wb.close();

        return itemList;
    }

    private static String readText(Cell data[], int col) {
        if (col >= data.length)
            return null;
        else
            return data[col].getContents();
    }

    private static Date readDate(Cell data[], int col) {
        if (col >= data.length || !(data[col] instanceof DateCell)) {
            return null;
        } else {
            return ((DateCell) data[col]).getDate();
        }
    }

    private static int readInt(Cell data[], int col) {
        if (col >= data.length || !(data[col] instanceof NumberCell)) {
            return 0;
        } else {
            return (int) ((NumberCell) data[col]).getValue();
        }
    }
}
