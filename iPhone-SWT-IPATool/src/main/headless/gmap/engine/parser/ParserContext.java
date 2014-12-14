/**
 * 
 */
package gmap.engine.parser;

import gmap.engine.GMapException;
import gmap.engine.data.GAsset;
import gmap.engine.data.GStyleBase;

import java.util.HashMap;

/**
 * @author jzarzuela
 *
 */
public class ParserContext {

    private HashMap<String, GAsset>     m_unlinked_ChildID_OwnerAsset = new HashMap<String, GAsset>();
    private HashMap<String, GStyleBase> m_unlinked_FeatureID_Style    = new HashMap<String, GStyleBase>();

    private HashMap<String, GAsset>     m_unlinked_OwnerID_OwnerAsset = new HashMap<String, GAsset>();

    /**
     * 
     */
    public ParserContext() {
    }

    // ----------------------------------------------------------------------------------------------------
    public void addUnlinkedChildIDToAsset(String childID, GAsset ownerAsset) throws GMapException {
        if (!m_unlinked_ChildID_OwnerAsset.containsKey(childID)) {
            m_unlinked_ChildID_OwnerAsset.put(childID, ownerAsset);
        } else {
            throw new GMapException("There can't be duplicated child GIDs: " + childID);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    public void addUnlinkedFeatureIDToStyle(String feature_id, GStyleBase style) throws GMapException {
        if (!m_unlinked_FeatureID_Style.containsKey(feature_id)) {
            m_unlinked_FeatureID_Style.put(feature_id, style);
        } else {
            throw new GMapException("There can't be duplicated Feature GIDs: " + feature_id);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    public void addUnlinkedOwnerAsset(GAsset ownerAsset) throws GMapException {
        if (!m_unlinked_OwnerID_OwnerAsset.containsKey(ownerAsset.GID)) {
            m_unlinked_OwnerID_OwnerAsset.put(ownerAsset.GID, ownerAsset);
        } else {
            throw new GMapException("There can't be duplicated owner GIDs: " + ownerAsset.GID);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    public <T extends GAsset> T removeUnlinkedAssetForChildID(String childID) throws GMapException {
        T ownerAsset = (T) m_unlinked_ChildID_OwnerAsset.remove(childID);
        if (ownerAsset == null) {
            throw new GMapException("There isn't any owner Asset for given child GIDs: " + childID);
        }
        return ownerAsset;
    }

    // ----------------------------------------------------------------------------------------------------
    public GStyleBase removeUnlinkedStyleForFeatureID(String feature_id) throws GMapException {
        return m_unlinked_FeatureID_Style.remove(feature_id);
    }

    // ----------------------------------------------------------------------------------------------------
    public <T extends GAsset> T removeUnlinkedOwnerAsset(String ownerAssetID) throws GMapException {
        T ownerAsset = (T) m_unlinked_OwnerID_OwnerAsset.remove(ownerAssetID);
        if (ownerAsset == null) {
            throw new GMapException("There isn't any owner Asset for given owner Asset GIDs: " + ownerAssetID);
        }
        return ownerAsset;
    }

}
