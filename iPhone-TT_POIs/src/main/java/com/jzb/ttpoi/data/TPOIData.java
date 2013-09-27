/**
 * 
 */
package com.jzb.ttpoi.data;

/**
 * @author n63636
 */
public class TPOIData implements Comparable<TPOIData> {

    public static final String UNDEFINED_CATEGORY = "UNDEFINED";

    /**
     * 
     */
    private String             m_category         = "";
    /**
     * 
     */
    private String             m_desc             = "";
    /**
     * 
     */
    private String             m_iconStyle        = null;

    /**
     * 
     */
    private double             m_lat              = 0;

    /**
     * 
     */
    private double             m_lng              = 0;

    /**
     * 
     */
    private String             m_name             = "";

    /**
     * 
     */
    public TPOIData() {
    }

    /**
     * @param maxDistance
     *            en metros para indicar qué se considera "cercano"
     * 
     * @return 0 - Exactamente iguales 1 - Nombre igual y cercano 2 - Nombre igual y lejano 3 - Nombre diferente y cercano 4 - Completamente distintos
     */
    public int comparedToPOI(double maxDistance, TPOIData poi2) {

        double dist = distance(poi2);
        boolean areNear = (dist < maxDistance); // A menos de x metros

        if (getName().equals(poi2.getName())) {
            if (dist == 0) {
                // Iguales
                return 0;
            } else if (areNear) {
                System.out.println("1 - POI with same name and nearby:");
                System.out.println("  " + getName());
                System.out.println("  " + poi2.getName());
                System.out.println("  Distance= " + dist);

                return 1;
            } else {
                System.out.println("2 - POI with same name and different location:");
                System.out.println("  " + getName());
                System.out.println("  " + poi2.getName());
                System.out.println("  Distance= " + dist);

                return 2;
            }
        } else if (areNear) {
            if (dist == 0 && (getName().toLowerCase().contains(poi2.getName().toLowerCase()) || poi2.getName().toLowerCase().contains(getName().toLowerCase()))) {
                System.out.println("0 - POI with very similar name and exact location:");
                System.out.println("  " + getName());
                System.out.println("  " + poi2.getName());
                System.out.println("  Distance= " + dist);
                return 0;
            } else {
                System.out.println("3 - POI with different name and nearby:");
                System.out.println("  " + getName());
                System.out.println("  " + poi2.getName());
                System.out.println("  Distance= " + dist);

                return 3;
            }
        } else {
            return 4;
        }
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(TPOIData poi) {
        int c = m_category.compareTo(poi.m_category);
        if (c != 0)
            return c;
        else
            return m_name.compareTo(poi.m_name);
    }

    /**
     * Distancia en metros
     */
    public double distance(TPOIData poi) {

        if (m_lat == poi.m_lat && m_lng == poi.m_lng)
            return 0;

        double rLat1 = m_lat * Math.PI / 180.0;
        double rLng1 = m_lng * Math.PI / 180.0;
        double rLat2 = poi.m_lat * Math.PI / 180.0;
        double rLng2 = poi.m_lng * Math.PI / 180.0;
        double earthRadius = 6378000; // In meters

        double result = Math.acos(Math.cos(rLat1) * Math.cos(rLng1) * Math.cos(rLat2) * Math.cos(rLng2) + Math.cos(rLat1) * Math.sin(rLng1) * Math.cos(rLat2) * Math.sin(rLng2) + Math.sin(rLat1)
                * Math.sin(rLat2))
                * earthRadius;

        return result;

    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof TPOIData)) return false;
        
        TPOIData poi2 = (TPOIData) obj;
        return  m_name.equals(poi2.m_name) &&
                m_desc.equals(poi2.m_desc) &&
                m_lat == poi2.m_lat &&
                m_lng == poi2.m_lng;
    }

    /**
     * @return the categoty
     */
    public String getCategory() {
        return m_category;
    }

    /**
     * @return the desc
     */
    public String getDesc() {
        return m_desc;
    }

    /**
     * @return the iconStyle
     */
    public String getIconStyle() {
        return m_iconStyle;
    }

    /**
     * @return the lat
     */
    public double getLat() {
        return m_lat;
    }

    /**
     * @return the lng
     */
    public double getLng() {
        return m_lng;
    }

    /**
     * @return the name
     */
    public String getName() {
        return m_name;
    }

    /**
     * @param categoty
     *            the categoty to set
     */
    public void setCategory(String category) {
        if (category != null)
            m_category = category;
        else
            m_category = UNDEFINED_CATEGORY;
    }

    /**
     * @param desc
     *            the desc to set
     */
    public void setDesc(String desc) {
        if (desc == null)
            desc = "";
        m_desc = desc;
    }

    /**
     * @param iconStyle
     *            the iconStyle to set
     */
    public void setIconStyle(String iconStyle) {
        m_iconStyle = iconStyle;
    }

    /**
     * @param lat
     *            the lat to set
     */
    public void setLat(double lat) {
        m_lat = lat;
    }

    /**
     * @param lng
     *            the lng to set
     */
    public void setLng(double lng) {
        m_lng = lng;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        if (name == null)
            name = "";
        m_name = name.trim();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TPOIData: '" + m_name + "', lat=" + m_lat + ", lng=" + m_lng + ", category=" + m_category + ", desc=" + m_desc + ", iconStyle = " + m_iconStyle;
    }
    
    public boolean Is_TT_Cat_POI() {
        return m_name.toLowerCase().startsWith("@tt_cat") || m_name.toLowerCase().startsWith("@cat_tt");
    }
}