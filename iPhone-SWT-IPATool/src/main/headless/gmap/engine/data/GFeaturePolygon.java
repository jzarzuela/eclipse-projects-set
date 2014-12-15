/**
 * 
 */
package gmap.engine.data;

import gmap.engine.GMapException;
import gmap.engine.data.GLayer;

import java.io.PrintWriter;

/**
 * @author jzarzuela
 *
 */
public class GFeaturePolygon extends GFeature {

    // ----------------------------------------------------------------------------------------------------
    public GFeaturePolygon(GLayer ownerLayer, String gid) {
        super(ownerLayer, gid);
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @see gmap.engine.data.GFeature#setGeometry(gmap.engine.data.GGeometry)
     */
    @Override
    public void setGeometry(GGeometry geometry) throws GMapException {
        if (geometry instanceof GGeoPolygon) {
            super._sub_setGeometry(geometry);
        } else {
            throw new GMapException("Geometry must be an GGeoPolygon instance: " + geometry);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @param style
     *            the style to set
     */
    @Override
    public void setStyle(GStyleBase style) throws GMapException {
        if (style instanceof GStylePolygon) {
            super._sub_setStyle(style);
        } else {
            throw new GMapException("Style must be an GStyleBase instance: " + style);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    @Override
    protected EFeatureType _sub_featureType() {
        return EFeatureType.GFPolygon;
    }

    // ----------------------------------------------------------------------------------------------------
    @Override
    protected void _sub_printValue(PrintWriter pw, String padding) {
    }

}
