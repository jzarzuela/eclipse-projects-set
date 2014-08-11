/**
 * 
 */
package com.jzb.ttpoi.data;

import java.util.ArrayList;

/**
 * @author n63636
 */
public class TPOIPolylineData {

    public static class Coordinate {

        public double lat;
        public double lng;
        
        @Override
        public String toString() {
            return "(lat="+lat+", lng="+lng+")";
        }
    }

    private ArrayList<Coordinate> m_coordinates = new ArrayList<>();
    private String                m_desc        = "";
    private String                m_name        = "";

    /**
     * 
     */
    public TPOIPolylineData() {
    }

    /**
     * @return the coordinates
     */
    public ArrayList<Coordinate> getCoordinates() {
        return m_coordinates;
    }

    /**
     * @return the desc
     */
    public String getDesc() {
        return m_desc;
    }

    /**
     * @return the name
     */
    public String getName() {
        return m_name;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        String key = this.toString();
        return key.hashCode();
    }

    /**
     * @param coordinates
     *            the coordinates to set
     */
    public void setCoordinates(ArrayList<Coordinate> coordinates) {
        m_coordinates = coordinates;
    }

    /**
     * @param desc
     *            the desc to set
     */
    public void setDesc(String desc) {
        if (desc == null)
            desc = "";
        m_desc = desc.trim();
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
        String value = "TPOIPolilineData: '" + m_name + ", desc= '" + m_desc + "', coordinates("+this.m_coordinates.size()+") = [";
        boolean firtTime = true;
        for (Coordinate c : this.m_coordinates) {
            if (!firtTime)
                value += ", ";
            value += c.toString();
            firtTime = false;
        }
        value+="]";
        
        return value;
    }
}