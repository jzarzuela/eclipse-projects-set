/**
 * 
 */
package com.jzb.dxc;

import java.util.ArrayList;

/**
 * @author jzarzuela
 * 
 */
public class Filters {

    private static ArrayList<IFilterRule> s_filters = new ArrayList();

    public static void addFilter(IFilterRule filter) {
        s_filters.add(filter);
    }
    
    public static String filterName(String fname) {
        
        String newName = fname;

        GenericFilterRule.lastSerieNameMatched = null;

        for (IFilterRule f : s_filters) {
            try {
                newName = f.filter(newName + " ");
                newName = newName.toLowerCase().trim();
                if (newName.length() == 0)
                    newName = fname;
                
                //System.out.println(f);
                //System.out.println(" "+newName);
                
            } catch (Exception ex) {
                Tracer.trace("* Error filtering file name '%s': %s", fname, ex.getMessage());
                return fname;
            }
        }

        return newName;
    }

}
