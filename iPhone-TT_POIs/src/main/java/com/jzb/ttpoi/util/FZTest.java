/**
 * 
 */
package com.jzb.ttpoi.util;

import net.sourceforge.jFuzzyLogic.FIS;

/**
 * @author jzarzuela
 * 
 */
public class FZTest {

    public static void main(String[] args) throws Exception {
        // Load from 'FCL' file
        String fileName = "/Users/jzarzuela/Documents/java-Campus/TT_POIs/src/com/jzb/ttpoi/util/duplicado.fcl";
        FIS fis = FIS.load(fileName, true);
        // Error while loading?
        if (fis == null) {
            System.err.println("Can't load file: '" + fileName + "'");
            return;
        }

        // Show
        //fis.chart();

        // Set inputs
        fis.setVariable("name", 0);
        fis.setVariable("distance", 75);

        // Evaluate
        fis.evaluate();

        // Show output variable's chart
        //fis.getVariable("duplicated").chartDefuzzifier(true);

        for (int n = 0; n < 10; n++) {
            for (int d = 0; d < 500; d++) {

                fis.setVariable("name", n * 10);
                int dd = d * 10;
                if(dd>5000) dd=5000;
                fis.setVariable("distance", dd);
                fis.evaluate();
                int r=(int)fis.getVariable("duplicated").defuzzify();
                if(r<25)
                System.out.println(n*10 + " \t " + dd + "\t --> " + r);

            }
        }

        System.out.println("---- DONE! ----");
    }
}