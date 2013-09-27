/**
 * 
 */
package com.jzb.ttpoi.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author n63636
 */
public class TPOIFileData {

    /**
     * 
     */
    private ArrayList<TPOIData>                  allPOIs         = new ArrayList<TPOIData>();
    /**
     * 
     */
    private TreeMap<String, ArrayList<TPOIData>> categorizedPOIs = new TreeMap<String, ArrayList<TPOIData>>();
    /**
     * 
     */
    private String                               name;

    /**
     * 
     */
    private String                               fileName;
    
    /**
     * 
     */
    private Boolean                               wasKMLFile;
    
    
    /**
     * 
     */
    public TPOIFileData() {
    }

    public void addAllPOIs(ArrayList<TPOIData> pois) {
        for(TPOIData poi:pois) {
            addPOI(poi);
        }
    }
    
    public void addPOI(TPOIData poi) {
        TPOIData poi2 = _searchPOI(poi);
        if (poi2 != null) {
            System.out.println("** WARNING, duplicated POI name:");
            System.out.println("      " + poi2);
            System.out.println("      " + poi);
            System.out.println("      distance(m) = " + poi.distance(poi2));
        }
        getAllPOIs().add(poi);
        getCategory(poi.getCategory()).add(poi);
    }

    private TPOIData _searchPOI(TPOIData poi) {
        for (TPOIData poi2 : allPOIs) {
            if (poi.getName().equals(poi2.getName()))
                return poi2;
        }
        return null;
    }

    /**
     * @return the allPOIs
     */
    public ArrayList<TPOIData> getAllPOIs() {
        return allPOIs;
    }

    /**
     * @return the allPOIs
     */
    public ArrayList<TPOIData> getAllCategoryPOIs(String category) {
        return categorizedPOIs.get(category);
    }
    
    /**
     * @return the catPOIs
     */
    public TreeMap<String, ArrayList<TPOIData>> getCategorizedPOIs() {
        return categorizedPOIs;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the name
     */
    public boolean getWasKMLFile() {
        return wasKMLFile;
    }
    
    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }
    
    public Set<String> getCategories() {
        return categorizedPOIs.keySet();
    }
    
    public void sort() {
        Collections.sort(this.allPOIs);
        for (ArrayList<TPOIData> td : this.categorizedPOIs.values()) {
            Collections.sort(td);
        }
    }

    /**
     * @param allPOIs
     *            the allPOIs to set
     */
    public void setAllPOIs(ArrayList<TPOIData> allPOIs) {
        this.allPOIs = allPOIs;
    }

    /**
     * @param catPOIs
     *            the catPOIs to set
     */
    public void setCategorizedPOIs(TreeMap<String, ArrayList<TPOIData>> catPOIs) {
        this.categorizedPOIs = catPOIs;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param wasKMLFile
     *            the wasKMLFile to set
     */
    public void setWasKMLFile(boolean wasKMLFile) {
        this.wasKMLFile = wasKMLFile;
    }
    
    /**
     * @param fileName
     *            the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public void translateCategories(HashMap<String, String> transMap) {

        // La conversion es con expresiones regulares
        for (Map.Entry<String, String> entry : transMap.entrySet()) {

            String styleRE = entry.getKey();
            String cat = entry.getValue();

            Pattern pattern = Pattern.compile(styleRE);
            ArrayList<String> actualCats = new ArrayList(categorizedPOIs.keySet());
            for (String actualCat : actualCats) {
                Matcher m = pattern.matcher(actualCat);
                if (m.matches()) {

                    ArrayList<TPOIData> pois = categorizedPOIs.remove(actualCat);
                    for (TPOIData poi : pois) {
                        poi.setCategory(cat);
                    }

                    ArrayList<TPOIData> newCatpois = categorizedPOIs.get(cat);
                    if (newCatpois == null) {
                        categorizedPOIs.put(cat, pois);
                    } else {
                        newCatpois.addAll(pois);
                    }

                }
            }

        }
    }

    private ArrayList<TPOIData> getCategory(String cat) {

        if (cat == null || cat.length() == 0)
            cat = TPOIData.UNDEFINED_CATEGORY;

        ArrayList<TPOIData> list = getCategorizedPOIs().get(cat);
        if (list == null) {
            list = new ArrayList<TPOIData>();
            getCategorizedPOIs().put(cat, list);
        }
        return list;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Name: " + name + "\nPOIs: " + allPOIs.toString();
    }
}