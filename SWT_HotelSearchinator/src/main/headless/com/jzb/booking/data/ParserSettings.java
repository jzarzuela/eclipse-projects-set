/**
 * 
 */
package com.jzb.booking.data;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.jzb.util.AppPreferences;
import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public class ParserSettings {

    public static ArrayList<Integer> minRoomCapacities         = new ArrayList<Integer>(); // 1 habitacion familiar
    public static double             NonCancelableIncrement    = 1.2;                     // +20%
    public static double             FamilyBreakfastCostPerDay = 25.0;                    // 25E por familia
    public static double             maxTotalPrice             = 0;

    static {
        minRoomCapacities.add(4);
    }

    // ----------------------------------------------------------------------------------------------------
    public static void saveToAppPrefs(AppPreferences prefs) {

        prefs.setPref("MinRoomCapacities", ParserSettings.minRoomCapacities_to_str());

        double value = ParserSettings.NonCancelableIncrement;
        prefs.setPrefDouble("NonCancelableIncrement", 100 * value - 100);

        prefs.setPrefDouble("BreakfastCostPerDay", ParserSettings.FamilyBreakfastCostPerDay);
        prefs.setPrefDouble("MaxTotalPrice", ParserSettings.maxTotalPrice);

    }

    // ----------------------------------------------------------------------------------------------------
    public static void loadFromAppPrefs(AppPreferences prefs) {

        double value;

        ParserSettings.str_to_minRoomCapacities(prefs.getPref("MinRoomCapacities", "4"));

        value = prefs.getPrefDouble("NonCancelableIncrement", 20.0);
        ParserSettings.NonCancelableIncrement = (100.0 + value) / 100.0;

        value = prefs.getPrefDouble("BreakfastCostPerDay", 25.0);
        ParserSettings.FamilyBreakfastCostPerDay = value;

        value = prefs.getPrefDouble("MaxTotalPrice", 0.0);
        ParserSettings.maxTotalPrice = value;
    }

    // ----------------------------------------------------------------------------------------------------
    public static String minRoomCapacities_to_str() {

        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (int v : minRoomCapacities) {
            sb.append(first ? "" : ", ");
            sb.append(v);
            if (first)
                first = false;
        }
        return sb.toString();
    }

    // ----------------------------------------------------------------------------------------------------
    public static void str_to_minRoomCapacities(String str) {

        minRoomCapacities.clear();

        StringTokenizer st = new StringTokenizer(str, ",");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            try {
                token = token.trim();
                int v = Integer.parseInt(token);
                minRoomCapacities.add(v);
            } catch (Throwable th) {
                Tracer._warn("Error parsing capacity value:" + token, th);
            }
        }
    }
}
