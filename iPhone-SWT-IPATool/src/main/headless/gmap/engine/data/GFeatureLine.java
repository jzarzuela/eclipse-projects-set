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
public class GFeatureLine extends GFeature {

    // ----------------------------------------------------------------------------------------------------
    public GFeatureLine(GLayer ownerLayer, String gid) {
        super(ownerLayer, gid);
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @see gmap.engine.data.GFeature#setGeometry(gmap.engine.data.GGeometry)
     */
    @Override
    public void setGeometry(GGeometry geometry) throws GMapException {
        if (geometry instanceof GGeometryLine) {
            super._sub_setGeometry(geometry);
        } else {
            throw new GMapException("Geometry must be an GGeoLine instance: " + geometry);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * @param style
     *            the style to set
     */
    @Override
    public void setStyle(GStyle style) throws GMapException {
        if (style instanceof GStyleLine) {
            super._sub_setStyle(style);
        } else {
            throw new GMapException("Style must be an GStyleBase instance: " + style);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    @Override
    protected String _sub_featureType() {
        return "GFLine";
    }

    // ----------------------------------------------------------------------------------------------------
    @Override
    protected void _sub_printValue(PrintWriter pw, String padding) {
    }

}
