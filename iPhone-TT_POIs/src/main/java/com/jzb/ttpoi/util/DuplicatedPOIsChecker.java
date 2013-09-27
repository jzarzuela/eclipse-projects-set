/**
 * 
 */
package com.jzb.ttpoi.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import net.sourceforge.jFuzzyLogic.FIS;

import com.jzb.ttpoi.data.TPOIData;

/**
 * @author jzarzuela
 * 
 */
public class DuplicatedPOIsChecker {

    public static void checkDuplicated(boolean namesWithPrefix, ArrayList<TPOIData> allPois, ArrayList<TPOIData> distinct, ArrayList<TPOIData> duplicated) throws Exception {

        distinct.clear();
        duplicated.clear();

        HashSet<TPOIData> set_duplicated = new HashSet<TPOIData>();

        FIS fis = loadFuzzyLogic();

        for (int n = 0; n < allPois.size(); n++) {

            // POI a comparar
            TPOIData poi1 = allPois.get(n);

            // Si ya figura como duplicado se lo salta
            if (set_duplicated.contains(poi1))
                continue;

            // Lo añade como nuevo y lo compara con el resto
            distinct.add(poi1);

            // Consigue el nombre y prefijo (si se dijo) del POI
            String poifullName1 = poi1.getName().toLowerCase();
            String poiName1 = poifullName1;
            String poiPrefix1 = null;

            if (namesWithPrefix) {
                int p1 = poifullName1.indexOf('-');
                if (p1 > 0) {
                    poiName1 = poifullName1.substring(p1);
                    poiPrefix1 = poifullName1.substring(0, p1);
                }
            }

            for (int i = n + 1; i < allPois.size(); i++) {

                // POI a comparar
                TPOIData poi2 = allPois.get(i);

                // Si ya figura como duplicado se lo salta
                if (set_duplicated.contains(poi2))
                    continue;

                // Consigue el nombre y prefijo (si se dijo) del POI
                String poifullName2 = poi2.getName().toLowerCase();
                String poiName2 = poifullName2;
                String poiPrefix2 = null;

                if (namesWithPrefix) {
                    int p2 = poifullName2.indexOf('-');
                    if (p2 > 0) {
                        poiName2 = poifullName2.substring(p2);
                        poiPrefix2 = poifullName2.substring(0, p2);
                    }
                }

                // Los nombres a utilizar depende de si tenian o no prefijo (-)
                String text1, text2;
                if (poiPrefix1 == null || poiPrefix2 == null || poiPrefix1.equals(poiPrefix2)) {
                    text1 = poiName1;
                    text2 = poiName2;
                } else {
                    text1 = poifullName1;
                    text2 = poifullName2;
                }

                // Calcula la "similitud" entre los nombres
                // Y chequea si solo hay un cambio de 2 numeros en la diferencia para ajustarlo
                int distSyn = LevenshteinDistance.compute(text1, text2);
                double nameDist = 100.0 * (double) distSyn / (double) Math.max(text1.length(), text2.length());
                if (distSyn > 0 && distSyn < 3 && _checkNumericDiff(text1, text2))
                    nameDist = 50.0; // Regular

                // Calcula la distancia y la ajusta si es mayor que 5Km.
                double distKM = poi1.distance(poi2);
                if (distKM > 5000)
                    distKM = 5000;

                // Evalua las reglas de logica difusa
                fis.setVariable("name", nameDist);
                fis.setVariable("distance", distKM);
                fis.evaluate();
                double ratio = fis.getVariable("duplicated").defuzzify();

                // "Muy Parecido" => Valor< 25.
                // Se establece el nivel de corte algo mas restringuido
                if (ratio < 20) {
                    /**
                    System.out.println("--->\t" + ratio + " \t " + distKM + " \t " + nameDist + " \t " + distSyn);
                    System.out.println("  '" + poi1.getName() + "' \t -> \t" + poi1.getDesc());
                    System.out.println("  '" + poi2.getName() + "' \t -> \t" + poi2.getDesc());
                    System.out.println();
                    **/
                    set_duplicated.add(poi2);
                    duplicated.add(poi2);
                }
                                
            }
        }
    }

    private static boolean _checkNumericDiff(String name1, String name2) {
        int len = Math.max(name1.length(), name2.length());
        for (int n = 0; n < len; n++) {
            char c1 = n < name1.length() ? name1.charAt(n) : '0';
            char c2 = n < name2.length() ? name2.charAt(n) : '0';
            if (c1 != c2 && (!Character.isDigit(c1) || !Character.isDigit(c2))) {
                return false;
            }
        }
        return true;
    }

    private static FIS loadFuzzyLogic() throws Exception {
        
        URL url = DuplicatedPOIsChecker.class.getResource("duplicado.fcl");
        FIS fis = FIS.load(url.getFile(), true);
        if (fis == null) {
            throw new Exception("Can't load FCL file: '" + url + "'");
        }
        return fis;
    }

}
