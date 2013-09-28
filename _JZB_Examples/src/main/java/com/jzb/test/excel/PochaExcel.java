package com.jzb.test.excel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Blank;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class PochaExcel {

    public PochaExcel() {
    }

    public static void main(String args[]) {
        try {
            System.out.println("***** TEST STARTED *****");
            PochaExcel me = new PochaExcel();
            long t1 = System.currentTimeMillis();
            me.doIt(args);
            long t2 = System.currentTimeMillis();
            System.out.println((new StringBuilder("***** TEST FINISHED [")).append(t2 - t1).append("]*****").toString());
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
        }
    }

    public void doIt(String args[]) throws Exception {
        System.out.println("Parametros: <numJugadores|4> <numCartas|40>");
        int NUM_PLAYERS = args.length <= 1 ? 4 : Integer.parseInt(args[0]);
        int NUM_CARDS = args.length <= 2 ? 40 : Integer.parseInt(args[1]);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_hh-mm-ss");
        String fileName = "pocha_de_" + NUM_PLAYERS + "--" + sdf.format(new Date()) + ".xls";
        File fo = new File(fileName);
        WritableWorkbook owb = Workbook.createWorkbook(fo);

        for (int n = 0; n < 8; n++) {
            createSheet(owb, n, NUM_PLAYERS, NUM_CARDS);
        }

        owb.write();
        owb.close();
    }

    private void createSheet(WritableWorkbook owb, int index, int numPlayers, int numCards) throws Exception {
        WritableSheet wsheet = owb.createSheet("Pocha-" + index, index);
        
        if (numPlayers == 4)
            wsheet.getSettings().setZoomFactor(170);
        else if (numPlayers == 5)
            wsheet.getSettings().setZoomFactor(140);

        int numRows = createFirstColumn(wsheet, numPlayers, numCards);
        for (int n = 0; n < numPlayers; n++) {
            createPlayerSum(wsheet, numRows, n);
        }
        createPlayersLabels(wsheet, numPlayers, numRows);
        createScore(wsheet, numRows);
        createWarningColumns(wsheet, numRows, numPlayers);
        setColumnsWidth(wsheet, numPlayers);
    }

    private void setColumnsWidth(WritableSheet wsheet, int numPlayers) throws Exception {
        for (int n = 0; n < 1 + numPlayers * 3; n++)
            wsheet.setColumnView(n, 5);

    }

    private void createScore(WritableSheet wsheet, int maxRows) throws Exception {
        WritableCellFormat wcf = new WritableCellFormat();
        wcf.setBackground(Colour.YELLOW2);
        wcf.setAlignment(Alignment.CENTRE);
        WritableFont wf = new WritableFont(WritableFont.ARIAL);
        wf.setColour(Colour.BLACK);
        wf.setBoldStyle(WritableFont.BOLD);
        wcf.setFont(wf);
        wcf.setBorder(Border.ALL, BorderLineStyle.THIN);
        WritableCellFormat wcf2 = new WritableCellFormat();
        wcf2.setBackground(Colour.VERY_LIGHT_YELLOW);
        wcf2.setAlignment(Alignment.CENTRE);
        wcf2.setFont(wf);
        wcf2.setBorder(Border.ALL, BorderLineStyle.THIN);
        jxl.write.WritableCell cell = new Label(0, 3 + maxRows, "Cuanto Suma:", wcf);
        wsheet.addCell(cell);
        wsheet.mergeCells(0, 3 + maxRows, 2, 3 + maxRows);
        cell = new Number(3, 3 + maxRows, (new Integer(10)).intValue(), wcf2);
        wsheet.addCell(cell);
        cell = new Label(0, 4 + maxRows, "Cuanto Resta:", wcf);
        wsheet.addCell(cell);
        wsheet.mergeCells(0, 4 + maxRows, 2, 4 + maxRows);
        cell = new Number(3, 4 + maxRows, (new Integer(10)).intValue(), wcf2);
        wsheet.addCell(cell);
    }

    private void createPlayersLabels(WritableSheet wsheet, int numPlayers, int maxRows) throws Exception {

        WritableCellFormat wcf1 = new WritableCellFormat();
        wcf1.setBackground(Colour.YELLOW);
        wcf1.setAlignment(Alignment.CENTRE);
        wcf1.setVerticalAlignment(VerticalAlignment.CENTRE);
        WritableFont wf1 = new WritableFont(WritableFont.ARIAL, 14);
        wf1.setColour(Colour.RED);
        wf1.setBoldStyle(WritableFont.BOLD);
        wcf1.setFont(wf1);
        wcf1.setBorder(Border.ALL, BorderLineStyle.THIN);

        WritableCellFormat wcf2 = new WritableCellFormat();
        wcf2.setBackground(Colour.YELLOW);
        wcf2.setAlignment(Alignment.CENTRE);
        wcf2.setVerticalAlignment(VerticalAlignment.CENTRE);
        WritableFont wf2 = new WritableFont(WritableFont.ARIAL, 20);
        wf2.setColour(Colour.RED);
        wf2.setBoldStyle(WritableFont.BOLD);
        wcf2.setFont(wf2);
        wcf2.setBorder(Border.ALL, BorderLineStyle.THIN);

        for (int n = 0; n < numPlayers; n++) {
            Label cell = new Label(1 + n * 3, 0, "x", wcf1);
            wsheet.addCell(cell);
            wsheet.mergeCells(1 + n * 3, 0, 2 + n * 3, 0);
            char COL = (char) (68 + n * 3);
            String s = (new StringBuilder("SUMA(")).append(COL).append("2:").append(COL).append(Integer.toString(1+maxRows)).append(")").toString();
            Formula cell2 = new Formula(1 + n * 3, 1 + maxRows, s, wcf2);
            wsheet.addCell(cell2);
            wsheet.mergeCells(1 + n * 3, 1 + maxRows, 2 + n * 3, 1 + maxRows);
        }

    }

    private void createWarningColumns(WritableSheet wsheet, int numRows, int numPlayers) throws Exception {

        WritableCellFormat wcf = new WritableCellFormat();
        wcf.setAlignment(Alignment.CENTRE);
        wcf.setVerticalAlignment(VerticalAlignment.CENTRE);
        WritableFont wf = new WritableFont(WritableFont.ARIAL, 10);
        wf.setColour(Colour.RED);
        wf.setBoldStyle(WritableFont.BOLD);
        wcf.setFont(wf);
        wcf.setWrap(true);

        WritableCell cell = new Label(1 + numPlayers * 3, 0, "NO Puede", wcf);
        wsheet.addCell(cell);
        wsheet.setColumnView(1 + numPlayers * 3, 7);

        cell = new Label(2 + numPlayers * 3, 0, "Suma", wcf);
        wsheet.addCell(cell);
        wsheet.setColumnView(2 + numPlayers * 3, 7);

        for (int n = 2; n < numRows + 2; n++) {
            
            String s = "SI(Y(";
            for (int i = 0; i < numPlayers; i++) {
                char COL = (char) (66 + i * 3);
                s += (i>0?",":"") + "ESBLANCO(" + COL + n + ")";
            }
            s += "),\"\",A"+n+"-SUMA(";
            for (int i = 0; i < numPlayers; i++) {
                char COL = (char) (66 + i * 3);
                s+=(i>0?",":"") + COL + n;
            }
            s += "))";

            cell = new Formula(1 + numPlayers * 3, n - 1, s, wcf);

            wsheet.addCell(cell);
        }

        for (int n = 2; n < numRows + 2; n++) {
            String s = "SI(Y(";
            for (int i = 0; i < numPlayers; i++) {
                if (i > 0)
                    s = (new StringBuilder(String.valueOf(s))).append("+").toString();
                char COL = (char) (67 + i * 3);
                s = (new StringBuilder(String.valueOf(s))).append(COL).append(n).toString();
            }

            s = (new StringBuilder(String.valueOf(s))).append("<>A").append(n).append(",NO(O(").toString();
            for (int i = 0; i < numPlayers; i++) {
                if (i > 0)
                    s = (new StringBuilder(String.valueOf(s))).append(",").toString();
                char COL = (char) (67 + i * 3);
                s = (new StringBuilder(String.valueOf(s))).append("ESBLANCO(").append(COL).append(n).append(")").toString();
            }

            s = (new StringBuilder(String.valueOf(s))).append("))),\"MAL\",\"\")").toString();
            cell = new Formula(2 + numPlayers * 3, n - 1, s, wcf);
            wsheet.addCell(cell);
        }

    }

    private void createPlayerSum(WritableSheet wsheet, int numRows, int numPlayer) throws Exception {
        int row = 1;
        char B_COL = (char) (66 + numPlayer * 3);
        char C_COL = (char) (67 + numPlayer * 3);
        String Q_SUMAR = (new StringBuilder("$D$")).append(numRows + 4).toString();
        String Q_RESTAR = (new StringBuilder("$D$")).append(numRows + 5).toString();
        
        WritableCellFormat wcf = new WritableCellFormat();
        wcf.setBackground(Colour.LAVENDER);
        wcf.setAlignment(Alignment.CENTRE);
        WritableFont wf = new WritableFont(WritableFont.ARIAL);
        wf.setColour(Colour.BLACK);
        wf.setBoldStyle(WritableFont.BOLD);
        wcf.setFont(wf);
        wcf.setBorder(Border.ALL, BorderLineStyle.THIN);
        
        WritableCellFormat wcf2 = new WritableCellFormat();
        wcf2.setAlignment(Alignment.CENTRE);
        wcf2.setBorder(Border.ALL, BorderLineStyle.THIN);
        
        for (int n = 2; n < numRows + 2; n++) {
           
            Blank cell2 = new Blank(1 + numPlayer * 3, row, wcf2);
            wsheet.addCell(cell2);
            Blank cell3 = new Blank(2 + numPlayer * 3, row, wcf2);
            wsheet.addCell(cell3);

            String s = (new StringBuilder("NO(O(ESBLANCO(")).append(B_COL).append(n).append("),ESBLANCO(").append(C_COL).append(n).append(")))*((").append(B_COL).append(n).append("=").append(C_COL)
                    .append(n).append(")*(").append(C_COL).append(n).append("+1)*(").append(Q_SUMAR).append("*(").append(C_COL).append(n).append("<>A").append(n).append(")+2*").append(Q_SUMAR)
                    .append("*(").append(C_COL).append(n).append("=A").append(n).append("))-(").append(B_COL).append(n).append("<>").append(C_COL).append(n).append(")*").append(Q_RESTAR).append(
                            "*ABS(").append(C_COL).append(n).append("-").append(B_COL).append(n).append("))").toString();
            Formula cell = new Formula(3 + numPlayer * 3, row, s, wcf);
            wsheet.addCell(cell);
            row++;
        }

    }

    private int createFirstColumn(WritableSheet wsheet, int numPlayers, int numCards) throws Exception {

        WritableFont wf = new WritableFont(WritableFont.ARIAL);
        wf.setColour(Colour.WHITE);
        wf.setBoldStyle(WritableFont.BOLD);
        WritableCellFormat wcf1 = new WritableCellFormat();
        wcf1.setBackground(Colour.LIGHT_BLUE);
        wcf1.setAlignment(Alignment.CENTRE);
        wcf1.setFont(wf);
        wcf1.setBorder(Border.ALL, BorderLineStyle.THIN);
        WritableCellFormat wcf2 = new WritableCellFormat();
        wcf2.setBackground(Colour.DARK_BLUE);
        wcf2.setAlignment(Alignment.CENTRE);
        wcf2.setFont(wf);
        wcf2.setBorder(Border.ALL, BorderLineStyle.THIN);

        int row = 1;
        int maxNumCards = numCards / numPlayers;

        for (int n = 0; n < numPlayers; n++) {

            Number cell = new Number(0, row, (new Integer(2)).intValue(), ((row - 1) % numPlayers == 0) ? wcf1 : wcf2);
            wsheet.addCell(cell);
            row++;
        }

        for (int n = 3; n < maxNumCards; n++) {
            Number cell = new Number(0, row, (new Integer(n)).intValue(), ((row - 1) % numPlayers == 0) ? wcf1 : wcf2);
            wsheet.addCell(cell);
            row++;
        }

        for (int n = 0; n < numPlayers; n++) {
            Number cell = new Number(0, row, (new Integer(maxNumCards)).intValue(), ((row - 1) % numPlayers == 0) ? wcf1 : wcf2);
            wsheet.addCell(cell);
            row++;
        }

        for (int n = maxNumCards - 1; n > 2; n--) {
            Number cell = new Number(0, row, (new Integer(n)).intValue(), ((row - 1) % numPlayers == 0) ? wcf1 : wcf2);
            wsheet.addCell(cell);
            row++;
        }

        for (int n = 0; n < numPlayers; n++) {
            Number cell = new Number(0, row, (new Integer(2)).intValue(), ((row - 1) % numPlayers == 0) ? wcf1 : wcf2);
            wsheet.addCell(cell);
            row++;
        }

        return --row;
    }
}