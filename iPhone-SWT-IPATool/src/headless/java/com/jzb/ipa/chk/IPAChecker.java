/**
 * 
 */
package com.jzb.ipa.chk;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.jzb.ipa.ren.NameComposer;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
@SuppressWarnings("synthetic-access")
public class IPAChecker {

    private ArrayList<File> m_iphoneIPAs = new ArrayList<File>();
    private ArrayList<File> m_ipadIPAs   = new ArrayList<File>();
    private ArrayList<File> m_mixedIPAs  = new ArrayList<File>();

    private static class FNameComparator implements Comparator<File> {

        public int compare(File f1, File f2) {
            return f1.getName().compareToIgnoreCase(f2.getName());
        }
    }
    
    public void check(File iphoneFolder, File ipadFolder, File mixedFolder) throws Exception {

        Tracer._info("Reading info for existing IPAs");

        Tracer._info("Reading info for iPhone IPAs from: " + iphoneFolder);
        _readFiles(iphoneFolder, m_iphoneIPAs);

        Tracer._info("Reading info for iPad IPAs from: " + ipadFolder);
        _readFiles(ipadFolder, m_ipadIPAs);

        Tracer._info("Reading info for mixed IPAs from: " + mixedFolder);
        _readFiles(mixedFolder, m_mixedIPAs);

        FNameComparator comp= new FNameComparator();
        Collections.sort(m_iphoneIPAs, comp);
        Collections.sort(m_ipadIPAs, comp);
        Collections.sort(m_mixedIPAs, comp);
        
        _iPhoneCheck();
        _iPadCheck();

    }

    private void _iPadCheck() throws Exception {

        Tracer._debug("--------------------------------------------------------");
        Tracer._info("Checking iPad IPAs");
        Tracer._debug("--------------------------------------------------------");

        JaroWinklerDistance jwd = new JaroWinklerDistance(0.7, 4);

        // Check iPad
        for (File f : m_ipadIPAs) {

            String fname = f.getName();
            String name1 = _cleanName(NameComposer.parseName(fname));

            if (!NameComposer.isIPadIPA(fname)) {
                Tracer._debug("** Bad IPA in iPad folders: " + f);
            } else {
                for (File ff : m_mixedIPAs) {

                    String ffname = ff.getName();
                    String name2 = _cleanName(NameComposer.parseName(ffname));

                    if (jwd.distance(name1, name2) <= 0.20) {
                        Tracer._debug("** Possible duplicate iPad IPA in Mixed folders: ");
                        Tracer._debug("      " + NameComposer.parseName(f.getName()) + " - " + f);
                        Tracer._debug("      " + NameComposer.parseName(ff.getName()) + " - " + ff);
                        Tracer._debug("");
                    }
                }
            }
        }

    }

    private void _iPhoneCheck() throws Exception {

        Tracer._debug("--------------------------------------------------------");
        Tracer._info("Checking iPhone IPAs");
        Tracer._debug("--------------------------------------------------------");

        JaroWinklerDistance jwd = new JaroWinklerDistance(0.7, 4);

        // Check universal
        for (File f : m_mixedIPAs) {
            String fname = f.getName();
            if (NameComposer.isIPadIPA(fname)) {
                Tracer._debug("** Bad IPA in Mixed folders: " + f);
            }
        }

        // Check iPhone
        for (File f : m_iphoneIPAs) {
            String fname = f.getName();
            String name1 = _cleanName(NameComposer.parseName(fname));

            if (!NameComposer.isIPhoneIPA(fname)) {
                Tracer._debug("** Bad IPA in iPhone folders: " + f);
            } else {
                for (File ff : m_mixedIPAs) {

                    String ffname = ff.getName();
                    String name2 = _cleanName(NameComposer.parseName(ffname));

                    if (jwd.distance(name1, name2) <= 0.20) {
                        Tracer._debug("** Possible duplicate iPhone IPA in Mixed folders: ");
                        Tracer._debug("      " + NameComposer.parseName(f.getName()) + " - " + f);
                        Tracer._debug("      " + NameComposer.parseName(ff.getName()) + " - " + ff);
                        Tracer._debug("");
                    }
                }
            }
        }

    }

    private void _readFiles(File folder, ArrayList<File> files) throws Exception {

        for (File f : folder.listFiles()) {
            if (f.isDirectory()) {
                _readFiles(f, files);
            } else {
                if (f.getName().toLowerCase().endsWith(".ipa")) {
                    if (!NameComposer.isComposedName(f.getName())) {
                        Tracer._warn("IPA name doesn't have the addecuate format: " + f);
                    } else {
                        files.add(f);
                    }
                }
            }
        }

    }

    final static String REP_STR[] = { "ipad", "hd", "_", "-", " " };

    private String _cleanName(String s) {
        String x = s.toLowerCase();
        for (String r : REP_STR) {
            x = x.replaceAll(r, "");
        }
        return x;
    }
}
