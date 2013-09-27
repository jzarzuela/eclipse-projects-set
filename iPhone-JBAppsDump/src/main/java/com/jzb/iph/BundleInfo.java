/**
 * 
 */
package com.jzb.iph;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Formatter;

import com.dd.plist.NSDictionary;
import com.jzb.ipa.ren.NameComposer;

/**
 * @author jzarzuela
 * 
 */
public class BundleInfo {

    public static final String         NULL_VALUE = "";
    private static final DecimalFormat s_df       = new DecimalFormat("00.00");

    File                               ipaFile;
    String                             playlistName;
    String                             type;
    String                             bundleVersion;
    String                             itemId;
    String                             appleId;
    String                             purchaseDate;
    String                             price;
    String                             lastBundleVersion;

    public BundleInfo(File ipaFile) {
        this.ipaFile = ipaFile;
        this.playlistName = "[" + NameComposer.parseName(ipaFile.getName()) + "]";
        this.type = "" + NameComposer.getType(ipaFile.getName());
        this.bundleVersion = NULL_VALUE;
        this.itemId = NULL_VALUE;
        this.appleId = NULL_VALUE;
        this.purchaseDate = NULL_VALUE;
        this.price = "-00.00";
        this.lastBundleVersion = NULL_VALUE;
    }

    public BundleInfo(File ipaFile, NSDictionary dict) {
        this.ipaFile = ipaFile;
        this.playlistName = _readValue(dict, "playlistName");
        this.type = "" + NameComposer.getType(ipaFile.getName());
        this.bundleVersion = _readValue(dict, "bundleVersion");
        this.itemId = _readValue(dict, "itemId");
        this.appleId = _readValue(dict, "appleId");
        this.purchaseDate = _readValue(dict, "purchaseDate");
        this.price = _readFloatValue(dict, "price");
        this.lastBundleVersion = NULL_VALUE;

        if (this.playlistName == NULL_VALUE) {
            this.playlistName = "[" + NameComposer.parseName(ipaFile.getName()) + "]";
        }
        if (this.appleId == NULL_VALUE) {
            this.appleId = _readValue(dict, "com.apple.iTunesStore.downloadInfo/accountInfo/AppleID");
        }
        if (this.purchaseDate == NULL_VALUE) {
            this.purchaseDate = _readValue(dict, "com.apple.iTunesStore.downloadInfo/purchaseDate");
        }

    }

    public boolean isCracked() {
        return !appleId.equals("jzarzuela@yahoo.com");
    }

    private String _readValue(NSDictionary dict, String key) {
        String value = dict.getStrValue(key);
        return value != null ? value : NULL_VALUE;
    }

    private String _readFloatValue(NSDictionary dict, String key) {
        String value = dict.getStrValue(key);
        return value != null ? s_df.format(Double.parseDouble(value) / 1000.0).replace('.', ',') : "-00.00";
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        Formatter fmt = new Formatter();
        fmt.format("playlistName = '%s', bundleVersion = '%s', itemId = '%s', appleId = '%s', purchaseDate='%s', price='%s', lastBundleVersion ='%s'", playlistName, bundleVersion, itemId, appleId,
                purchaseDate, price, lastBundleVersion);

        return fmt.toString();
    }
}
