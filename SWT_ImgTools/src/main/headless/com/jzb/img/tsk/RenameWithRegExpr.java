/**
 * 
 */
package com.jzb.img.tsk;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jzb.futil.FileExtFilter;
import com.jzb.futil.FileExtFilter.IncludeFolders;
import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public class RenameWithRegExpr extends BaseTask {

    private static final Pattern s_imgPattern   = Pattern.compile("img[^0-9]*[0-9]+", Pattern.CASE_INSENSITIVE);

    private static final Pattern s_indexPattern = Pattern.compile("[0-9][0-9][0-9][0-9]+");

    // --------------------------------------------------------------------------------------------------------
    public static String getReplacementName(String replacement, Pattern regexpr, String fName) {

        Matcher matcher = regexpr.matcher(fName);

        if (!matcher.matches()) {
            return fName;
        }

        String result = replacement;

        for (int n = 1; n < 10; n++) {
            String repValue = "";
            if (n <= matcher.groupCount()) {
                repValue = matcher.group(n);
            }
            String grpIndex = "\\\\" + n;
            result = result.replaceAll(grpIndex, repValue);
        }

        return result;
    }

    // --------------------------------------------------------------------------------------------------------
    public static NameComposer tryToCalcCompoundName(String fname, String fext) {

        Matcher indexMatcher = s_indexPattern.matcher(fname);
        Matcher imgMatcher = s_imgPattern.matcher(fname);
        if (!indexMatcher.find() || !imgMatcher.find() || indexMatcher.start() >= imgMatcher.start()) {
            return new NameComposer("NO_MATCH" + fname + fext);
        }

        String groupNames = fname.substring(0, indexMatcher.start());
        groupNames = groupNames.replace('_', '-').replace('#', '-');

        String index = indexMatcher.group();

        String subgroupNames = fname.substring(indexMatcher.end(), imgMatcher.start());
        subgroupNames = subgroupNames.replace('_', '-').replace('#', '-');

        String imgFName = imgMatcher.group();

        if (imgMatcher.end() < fname.length()) {
            String more = fname.substring(imgMatcher.end());
            more = more.replace('_', '-').replace('#', '-');
            imgFName += more;
        }

        NameComposer nc = new NameComposer();

        String gns[] = groupNames.split("-");
        nc.setGroupNames(gns);

        nc.setIndex(Integer.parseInt(index));

        String sgns[] = subgroupNames.split("-");
        int length = sgns.length;
        while (length > 0 && (sgns[length - 1] == null || sgns[length - 1].length() == 0)) {
            length--;
        }
        if (length > 1) {
            for (int n = 0; n < length - 1; n++) {
                nc.addSubgroupName(sgns[n]);
            }
            nc.setName(sgns[length - 1]);
        } else {
            if (length > 0) {
                if (sgns[0].trim().length() > 0) {
                    nc.setName(sgns[0]);
                }
            }
        }

        nc.setImgFileName(imgFName);

        if (fext != null && fext.trim().length() > 0) {
            nc.setFileExt(fext);
        }

        return nc;
    }

    // --------------------------------------------------------------------------------------------------------
    public RenameWithRegExpr(JustCheck justChecking, File baseFolder, RecursiveProcessing recursive) {
        super(justChecking, baseFolder, recursive);
    }

    // --------------------------------------------------------------------------------------------------------
    public void renameByRegExpr(String regexpr, String replacement) {

        try {
            _checkBaseFolder();
            regexpr = regexpr.replaceAll("\\^@", "[\\^0-9]");
            regexpr = regexpr.replaceAll("@", "[0-9]");
            Pattern pt = Pattern.compile(regexpr);
            _renameByRegExpr(m_baseFolder, pt, replacement);
        } catch (Exception ex) {
            Tracer._error("Error processing action", ex);
        }
    }

    // --------------------------------------------------------------------------------------------------------
    public void renameTryCompoundName() {

        try {
            _checkBaseFolder();
            _renameTryCompoundName(m_baseFolder);
        } catch (Exception ex) {
            Tracer._error("Error processing action", ex);
        }
    }

    // --------------------------------------------------------------------------------------------------------
    public void searchPatterns() {

        try {
            _checkBaseFolder();
            _searchPatterns(m_baseFolder);
        } catch (Exception ex) {
            Tracer._error("Error processing action", ex);
        }
    }

    // --------------------------------------------------------------------------------------------------------
    private String _filterFileName(String name) {
        name = name.replace('0', '@').replace('1', '@').replace('2', '@').replace('3', '@').replace('4', '@');
        name = name.replace('5', '@').replace('6', '@').replace('7', '@').replace('8', '@').replace('9', '@');
        return name;
    }

    // --------------------------------------------------------------------------------------------------------
    private void _renameByRegExpr(File folder, Pattern regexpr, String replacement) throws Exception {

        Tracer._debug("");
        Tracer._debug("Renaming files using RegExpr" + (m_recursive == RecursiveProcessing.YES ? " * RECURSIVELY * " : " ") + "in folder: '" + folder + "'");
        Tracer._debug("");

        // Gets just image files and folders
        File fList[] = folder.listFiles(FileExtFilter.imgFilter(IncludeFolders.YES));

        // Split files and folders to process them properly
        TreeSet<File> allFiles = new TreeSet<File>();
        TreeSet<File> subFolders = new TreeSet<File>();
        for (File fImg : fList) {
            if (fImg.isDirectory()) {
                subFolders.add(fImg);
            } else {
                allFiles.add(fImg);
            }
        }

        // ---------------------------------------------
        // Iterate files in subfolder
        TreeMap<String, File> newFileNames = new TreeMap<String, File>();
        for (File fImg : allFiles) {

            String fExt = "";
            String fName = fImg.getName();
            int p1 = fName.lastIndexOf('.');
            if (p1 >= 0) {
                fExt = fName.substring(p1);
                fName = fName.substring(0, p1);
            }

            String newName = getReplacementName(replacement, regexpr, fName) + fExt;
            File existingFile = newFileNames.put(newName, fImg);
            if (existingFile != null) {
                Tracer._error("Two files would be renamed with the same name: '" + existingFile.getName() + "' and '" + fImg.getName() + "'");
                return;
            }

        }

        for (Map.Entry<String, File> entry : newFileNames.entrySet()) {
            File fImg = entry.getValue();
            File newFile = new File(fImg.getParentFile(), entry.getKey());
            _renameFile(fImg, newFile);
        }

        // ---------------------------------------------
        // Iterate subfolders
        if (m_recursive == RecursiveProcessing.YES) {
            for (File sfolder : subFolders) {
                if (sfolder.isDirectory()) {
                    _renameByRegExpr(sfolder, regexpr, replacement);
                }
            }
        }

    }

    // --------------------------------------------------------------------------------------------------------
    private void _renameTryCompoundName(File folder) throws Exception {

        Tracer._debug("");
        Tracer._debug("Renaming files trying to match a compound name" + (m_recursive == RecursiveProcessing.YES ? " * RECURSIVELY * " : " ") + "in folder: '" + folder + "'");
        Tracer._debug("");

        // Gets just image files and folders
        File fList[] = folder.listFiles(FileExtFilter.imgFilter(IncludeFolders.YES));

        // Split files and folders to process them properly
        TreeSet<File> allFiles = new TreeSet<File>();
        TreeSet<File> subFolders = new TreeSet<File>();
        for (File fImg : fList) {
            if (fImg.isDirectory()) {
                subFolders.add(fImg);
            } else {
                allFiles.add(fImg);
            }
        }

        // ---------------------------------------------
        // Iterate files in subfolder
        TreeMap<File, NameComposer> newFileNames = new TreeMap<File, NameComposer>();
        HashMap<String, Integer> namesCount = new HashMap<String, Integer>();
        HashMap<String, File> existingFiles = new HashMap<String, File>();
        for (File fImg : allFiles) {

            String fExt = "";
            String fName = fImg.getName();
            int p1 = fName.lastIndexOf('.');
            if (p1 >= 0) {
                fExt = fName.substring(p1);
                fName = fName.substring(0, p1);
            }

            NameComposer nc = tryToCalcCompoundName(fName, fExt);

            newFileNames.put(fImg, nc);

            String justName = nc.getName();
            if (justName != null) {
                Integer count = namesCount.get(justName);
                if (count == null) {
                    count = 0;
                }
                namesCount.put(justName, count + 1);
            }

            File existingFile = existingFiles.put(nc.compose(), fImg);
            if (existingFile != null) {
                Tracer._error("Two files would be renamed with the same name: '" + existingFile.getName() + "' and '" + fImg.getName() + "'");
                return;
            }
        }

        for (Map.Entry<File, NameComposer> entry : newFileNames.entrySet()) {

            NameComposer nc = entry.getValue();

            // ---------------------------------------------------------
            if (nc.compose().startsWith("_NO_MATCH_"))
                continue;
            // ---------------------------------------------------------

            String justName = nc.getName();
            if (justName != null) {
                Integer count = namesCount.get(justName);
                if (count != null && count > 3) {
                    nc.addSubgroupName(justName);
                    nc.emptyName();
                }
            }
            File fImg = entry.getKey();
            File newFile = new File(fImg.getParentFile(), nc.compose());
            _renameFile(fImg, newFile);

        }

        // ---------------------------------------------
        // Iterate subfolders
        if (m_recursive == RecursiveProcessing.YES) {
            for (File sfolder : subFolders) {
                if (sfolder.isDirectory()) {
                    _renameTryCompoundName(sfolder);
                }
            }
        }

    }

    // --------------------------------------------------------------------------------------------------------
    private void _searchPatterns(File folder) throws Exception {

        Tracer._debug("");
        Tracer._debug("Searching for file's name patterns" + (m_recursive == RecursiveProcessing.YES ? " * RECURSIVELY * " : " ") + "in folder: '" + folder + "'");
        Tracer._debug("");

        // Gets just image files and folders
        File fList[] = folder.listFiles(FileExtFilter.imgFilter(IncludeFolders.YES));

        // Split files and folders to process them properly
        TreeSet<File> allFiles = new TreeSet<File>();
        TreeSet<File> subFolders = new TreeSet<File>();
        for (File fImg : fList) {
            if (fImg.isDirectory()) {
                subFolders.add(fImg);
            } else {
                allFiles.add(fImg);
            }
        }

        // ---------------------------------------------
        // Iterate files in subfolder
        String lastPattern = "*!*!";
        for (File fImg : allFiles) {

            String fName = fImg.getName();
            int p1 = fName.lastIndexOf('.');
            if (p1 >= 0) {
                fName = fName.substring(0, p1);
            }

            String filteredName = _filterFileName(fName);
            if (!fName.equals(lastPattern)) {
                lastPattern = filteredName;
                Tracer._debug("File name pattern: " + lastPattern);
            }
        }

        // ---------------------------------------------
        // Iterate subfolders
        if (m_recursive == RecursiveProcessing.YES) {
            for (File sfolder : subFolders) {
                if (sfolder.isDirectory()) {
                    _searchPatterns(sfolder);
                }
            }
        }

    }

}
