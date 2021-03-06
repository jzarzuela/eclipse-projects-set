/**
 * 
 */
package gmap.engine.data;

import gmap.engine.GMapException;

import java.io.PrintWriter;

/**
 * @author jzarzuela
 *
 */
public class GFeaturePoint extends GFeature {

    // ----------------------------------------------------------------------------------------------------
    public GFeaturePoint(GLayer ownerLayer, String gid) {
        super(ownerLayer, gid);
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @see gmap.engine.data.GFeature#setGeometry(gmap.engine.data.GGeometry)
     */
    @Override
    public void setGeometry(GGeometry geometry) throws GMapException {
        if (geometry instanceof GGeometryPoint) {
            super._sub_setGeometry(geometry);
        } else {
            throw new GMapException("Geometry must be an GGeoPoint instance: " + geometry);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @param style
     *            the style to set
     */
    @Override
    public void setStyle(GStyle style) throws GMapException {
        if (style instanceof GStyleIcon) {
            super._sub_setStyle(style);
        } else {
            throw new GMapException("Style must be an GStyleBase instance: " + style);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    @Override
    protected String _sub_featureType() {
        return "GFPoint";
    }

    // ----------------------------------------------------------------------------------------------------
    @Override
    protected void _sub_printValue(PrintWriter pw, String padding) {
    }

}
