/**
 * 
 */
package com.jzb.booking;

import java.io.InputStream;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.rule.RuleBlock;

import com.jzb.booking.data.TRoomData;

/**
 * @author jzarzuela
 * 
 */
public class FuzzyLogicRanking {

    private FIS m_fis;

    // ----------------------------------------------------------------------------------------------------
    public FIS getFis() {
        return m_fis;
    }
    
    // ----------------------------------------------------------------------------------------------------
    public FuzzyLogicRanking() throws Exception {

        FIS.debug = false;
        m_fis = loadFuzzyLogic();

        RuleBlock.debug = false;
    }

    // ----------------------------------------------------------------------------------------------------
    public void adjustRanking(TRoomData room) throws Exception {

        int price = (int) (room.calculatedPrice / room.ownerHotel.numDays);
        double ranking = calcRanking(price, room.ownerHotel.avgRating);
        room.ranking = (int) (2.0 * ranking);
    }

    // ----------------------------------------------------------------------------------------------------
    public double calcRanking(double price, double avgRating) throws Exception {

        // Casos extremos
        if(price>200 || avgRating==0) return 0;
        
        
        // Evalua las reglas de logica difusa
        m_fis.setVariable("price", price < 200 ? price : 200);
        m_fis.setVariable("rating", avgRating);
        m_fis.evaluate();
        double ranking = m_fis.getVariable("ranking").defuzzify();
        
        return ranking;
    }

    // ----------------------------------------------------------------------------------------------------
    private FIS loadFuzzyLogic() throws Exception {

        InputStream is = FuzzyLogicRanking.class.getResourceAsStream("ranking.fcl");
        FIS fis = FIS.load(is, false);
        // Error while loading?
        if (fis == null) {
            throw new Exception("Can't load file: 'ranking.fcl' from classpath");
        }
        return fis;
    }

}
