/**
 * 
 */
package com.jzb.booking;

import java.io.File;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import net.sourceforge.jFuzzyLogic.rule.Rule;

/**
 * @author jzarzuela
 * 
 */
public class TestFuzzy {

    private FuzzyLogicRanking m_flr;

    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("\n***** EXECUTION STARTED *****\n");
            TestFuzzy me = new TestFuzzy();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("\n***** EXECUTION FINISHED [" + (t2 - t1) + "]*****\n");
            System.exit(1);
        } catch (Throwable th) {
            System.out.println("\n***** EXECUTION FAILED *****\n");
            th.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    // rating * price
    private double valueRating[]    = new double[12];
    private double valuePrice[]     = new double[12];
    private double valueRanking[][] = new double[12][12];

    // ----------------------------------------------------------------------------------------------------
    public void doIt(String[] args) throws Exception {

        File fxls = new File("/Users/jzarzuela/Documents/Reglas.xls");
        Workbook wb = Workbook.getWorkbook(fxls);
        Sheet sheet = wb.getSheet(1);

        for (int x = 0; x < 12; x++) {
            Cell cell = sheet.getCell(3 + x, 3);
            String rating_value = cell.getContents();
            valueRating[x] = Double.parseDouble(rating_value);
        }

        for (int y = 0; y < 12; y++) {
            Cell cell = sheet.getCell(2, 4 + y);
            String price_value = cell.getContents();
            valuePrice[y] = Double.parseDouble(price_value);
        }

        for (int x = 0; x < 12; x++) {
            for (int y = 0; y < 12; y++) {
                Cell cell = sheet.getCell(3 + x, 4 + y);
                String ranking_value = cell.getContents();
                valueRanking[x][y] = Double.parseDouble(ranking_value);
            }
        }

        m_flr = new FuzzyLogicRanking();

        for (double rating = 0; rating < 10; rating += 0.5) {
            System.out.println();
            for (double price = 0; price < 200; price += 15) {
                double ranking = 2 * _findRankingValue(price, rating);
                double fuzzy_ranking = Math.round(2 * m_flr.calcRanking(price, rating));
                if (Math.abs(ranking - fuzzy_ranking) >= 2) {
                    System.out.println("(" + rating + ", " + price + ") -> " + ranking + " == " + fuzzy_ranking);
                    _debugRule();
                }

            }
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private double _findRankingValue(double price, double rating) {
        int priceIndex = _findPriceIndex(price);
        int ratingIndex = _findRatingIndex(rating);
        return valueRanking[ratingIndex][priceIndex];
    }

    // ----------------------------------------------------------------------------------------------------
    private int _findPriceIndex(double value) {
        for (int n = 0; n < 11; n++) {
            if (valuePrice[n] <= value && valuePrice[n + 1] > value) {
                return n;
            }
        }
        throw new RuntimeException("Error in price value: " + value);
    }

    // ----------------------------------------------------------------------------------------------------
    private int _findRatingIndex(double value) {
        for (int n = 0; n < 11; n++) {
            if (valueRating[n] <= value && valueRating[n + 1] > value) {
                return n;
            }
        }
        throw new RuntimeException("Error in rating value: " + value);
    }

    // ----------------------------------------------------------------------------------------------------
    public void doIt2(String[] args) throws Exception {
        m_flr = new FuzzyLogicRanking();
        _calcRanking(1, 58, 5.3);
        _calcRanking(1, 59, 5.4);
        _calcRanking(1, 60, 5.5);
        _calcRanking(1, 61, 5.6);
        _calcRanking(1, 62, 5.7);
    }

    // ----------------------------------------------------------------------------------------------------
    public void doIt3(String[] args) throws Exception {
        m_flr = new FuzzyLogicRanking();
        _calcRanking(1, 90, 4);
    }

    // ----------------------------------------------------------------------------------------------------
    public void doIt4(String[] args) throws Exception {

        m_flr = new FuzzyLogicRanking();

        // --- 0 ---------------------------------------------------------------
        System.out.println();
        _calcRanking(0, 60, 0);
        _calcRanking(0, 60, 2.5);
        _calcRanking(0, 60, 5);
        _calcRanking(0, 100, 0);
        _calcRanking(0, 100, 2.5);
        _calcRanking(0, 100, 5);

        _calcRanking(0, 105, 1);
        _calcRanking(0, 110, 2.5);
        _calcRanking(0, 115, 2.5);
        _calcRanking(0, 100, 5.0);

        _calcRanking(0, 60, 1);
        _calcRanking(0, 80, 2.5);
        _calcRanking(0, 100, 5.0);

        _calcRanking(0, 60, 1);
        _calcRanking(0, 80, 2.5);
        _calcRanking(0, 100, 5.0);

        // --- 1 ---------------------------------------------------------------
        System.out.println();
        _calcRanking(1, 50, 3);
        _calcRanking(1, 50, 4);
        _calcRanking(1, 90, 4);
        _calcRanking(1, 100, 6);

        // --- 2 ---------------------------------------------------------------
        System.out.println();
        _calcRanking(2, 140, 7.3);
        _calcRanking(2, 160, 7.2);

        _calcRanking(2, 170, 9.5);
        _calcRanking(2, 180, 8.2);
        _calcRanking(2, 180, 8.5);

        // --- 3 ---------------------------------------------------------------
        System.out.println();
        _calcRanking(3, 50, 6);

        _calcRanking(3, 120, 7.3);

        _calcRanking(3, 140, 8.5);

        // --- 4 ---------------------------------------------------------------
        System.out.println();
        _calcRanking(4, 90, 7.3);

        _calcRanking(4, 100, 7.2);
        _calcRanking(4, 100, 7.3);
        _calcRanking(4, 100, 7.8);
        _calcRanking(4, 100, 8.2);
        _calcRanking(4, 100, 8.5);
        _calcRanking(4, 100, 9.5);
        _calcRanking(4, 120, 8.5);

        // --- 5 ---------------------------------------------------------------
        System.out.println();
        _calcRanking(5, 50, 7.2);
        _calcRanking(5, 50, 7.3);
        _calcRanking(5, 50, 7.8);

        _calcRanking(5, 50, 8.2);
        _calcRanking(5, 50, 8.5);
        _calcRanking(5, 50, 9.5);
        _calcRanking(5, 90, 8.5);
    }

    // ----------------------------------------------------------------------------------------------------
    private void _calcRanking(int result, double price, double avgRating) throws Exception {
        double ranking = m_flr.calcRanking(price, avgRating);
        System.out.println((price < 100 ? " " : "") + price + ", " + avgRating + " -> " + result + " -> " + Math.round(ranking) + " / " + ranking);
        if (Math.abs(result - Math.round(ranking)) >= 2) {
            _debugRule();
        }
    }

    // ----------------------------------------------------------------------------------------------------
    private void _debugRule() {
        // Show each rule (and degree of support)
        for (Rule r : m_flr.getFis().getFunctionBlock("ranking").getFuzzyRuleBlock("No1").getRules()) {
            System.out.println(r);
        }
        System.out.println();
    }
}
