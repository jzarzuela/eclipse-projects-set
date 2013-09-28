/**
 * 
 */
package com.jzb.img.tsk;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.jzb.futil.FileExtFilter;
import com.jzb.futil.FileExtFilter.IncludeFolders;
import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public class SplitByCompoundName extends BaseTask {

    // --------------------------------------------------------------------------------------------------------
    public SplitByCompoundName(JustCheck justChecking, File baseFolder, RecursiveProcessing recursive) {
        super(justChecking, baseFolder, recursive, true);
    }

    // --------------------------------------------------------------------------------------------------------
    public void splitByCompoundName(boolean keepOrder, boolean stopAtFirstSubgroup) {
        try {
            _checkBaseFolder();
            _splitByCompoundName(m_baseFolder, keepOrder, stopAtFirstSubgroup);
        } catch (Exception ex) {
            Tracer._error("Error processing action", ex);
        }
    }

    // --------------------------------------------------------------------------------------------------------
    private void _splitByCompoundName(File folder, boolean keepOrder, boolean stopAtFirstSubgroup) throws Exception {

        HashMap<String, Character> subgroupCounters = new HashMap<String, Character>();
        ArrayList<String> prevSGs = new ArrayList<String>();

        Tracer._debug("");
        Tracer._debug("Splitting files with compound name into subfolders (No recursive processing).");
        Tracer._debug("");

        // Gets just image files without folders
        File fList[] = folder.listFiles(FileExtFilter.imgFilter(IncludeFolders.NO));

        for (File fImg : fList) {
            if (NameComposer.isCompoundName(fImg.getName())) {

                if (fImg.getName().startsWith("New York-#00470-Lower Manhattan-Wall Street_[IMP2011-IMG_1765#CR2]")) {
                    System.out.println("pp");
                }

                m_nc.parse(fImg.getName());

                StringBuffer newName = new StringBuffer();

                for (String groupName : m_nc.getGroupNames()) {
                    newName.append(groupName).append(File.separator);
                }

                String grpNames = "#" + m_nc.getGroupNames().toString();
                String sgPath = _calSubgroupsPath(keepOrder, subgroupCounters, grpNames, prevSGs, m_nc.getSubgroupNames());
                prevSGs = new ArrayList(m_nc.getSubgroupNames());
                if (stopAtFirstSubgroup) {
                    int index = 1 + sgPath.indexOf(File.separatorChar);
                    if (index < sgPath.length()) {
                        sgPath = sgPath.substring(0, index);
                    }
                }
                newName.append(sgPath);

                newName.append(fImg.getName());

                File newFile = new File(m_baseFolder, newName.toString());

                _renameFile(fImg, newFile);
            }
        }

        _moveNothingFiles(folder);

    }

    // --------------------------------------------------------------------------------------------------------
    private void _moveNothingFiles(File folder) throws Exception {

        // Gets just folders
        File folderList[] = folder.listFiles(FileExtFilter.folderFilter());

        if (folderList.length > 0) {

            // Gets just image files without folders
            File fileList[] = folder.listFiles(FileExtFilter.imgFilter(IncludeFolders.NO));
            for (File fImg : fileList) {

                File newFile;

                m_nc.parse(fImg.getName());
                if (m_nc.getSubgroupNames().size() == 0) {
                    newFile = new File(fImg.getParentFile() + File.separator + "@0" + SUBGROUP_COUNTER_CHAR1 + SUBGROUP_NOTHING, fImg.getName());
                } else {
                    newFile = new File(fImg.getParentFile() + File.separator + "0" + SUBGROUP_COUNTER_CHAR1 + SUBGROUP_NOTHING, fImg.getName());
                }

                _renameFile(fImg, newFile);
            }

            for (File subfolder : folderList) {
                _moveNothingFiles(subfolder);
            }
        }

    }

    // --------------------------------------------------------------------------------------------------------
    private char _getCurrentCounter(boolean keepOrder, String cuSG, HashMap<String, Character> subgroupCounters, String sgPath) {

        Character counterParent = subgroupCounters.get(sgPath);
        if (counterParent == null) {
            counterParent = '1';
            subgroupCounters.put(sgPath, counterParent);
        }

        Character counterMine = subgroupCounters.get(sgPath + "$$" + cuSG);
        if (counterMine == null) {
            counterMine = '1';
            subgroupCounters.put(sgPath + "$$" + cuSG, counterMine);
        }

        if (!keepOrder) {
            return counterMine;
        } else {
            return counterParent;
        }

    }

    // --------------------------------------------------------------------------------------------------------
    private char _getNextCounter(boolean keepOrder, String cuSG, HashMap<String, Character> subgroupCounters, String sgPath) {

        if (!keepOrder) {
            
            Character counterMine = subgroupCounters.get(sgPath + "$$" + cuSG);
            if (counterMine == null) {
                counterMine = _getNextCounter(true, cuSG, subgroupCounters, sgPath);
                subgroupCounters.put(sgPath + "$$" + cuSG, counterMine);
            }
            return counterMine;

        } else {

            Character counterParent = subgroupCounters.get(sgPath);
            if (counterParent == null) {
                counterParent = '1';
            }
            counterParent = (char) ((int) counterParent + 1);
            if (counterParent > '9' && counterParent < 'a') {
                counterParent = 'a';
            }
            if (counterParent > 'a' && counterParent < 'A') {
                counterParent = 'A';
            }
            subgroupCounters.put(sgPath, counterParent);
            return counterParent;
        }

    }

    // --------------------------------------------------------------------------------------------------------
    private String _calSubgroupsPath(boolean keepOrder, HashMap<String, Character> subgroupCounters, String basePath, ArrayList<String> prevSGs, ArrayList<String> curSGs) {

        String sgPath = basePath;
        boolean firstOne = true;
        StringBuffer sb = new StringBuffer();
        for (int n = 0; n < curSGs.size(); n++) {

            String pvSG = n < prevSGs.size() ? prevSGs.get(n) : SUBGROUP_NOTHING;
            String cuSG = curSGs.get(n);

            char counter;
            if (cuSG.equals(pvSG)) {
                counter = _getCurrentCounter(keepOrder, cuSG, subgroupCounters, sgPath);
            } else {
                counter = _getNextCounter(keepOrder, cuSG, subgroupCounters, sgPath);
            }

            if (firstOne) {
                sb.append(SUBGROUP_MARKER);
                firstOne = false;
            }
            sb.append(counter).append(SUBGROUP_COUNTER_CHAR1).append(cuSG).append(File.separator);

            sgPath += cuSG + "#";

        }

        if (curSGs.size() > 0 && prevSGs.size() > 0 && curSGs.size() < prevSGs.size() && curSGs.get(curSGs.size() - 1).equals(prevSGs.get(prevSGs.size() - 2))) {
            char counter = _getNextCounter(keepOrder, SUBGROUP_NOTHING, subgroupCounters, sgPath);
            sb.append(counter).append('*').append(SUBGROUP_NOTHING).append(File.separator);
            subgroupCounters.put(sgPath + SUBGROUP_NOTHING, '0');
        } else {
            if (subgroupCounters.get(sgPath + SUBGROUP_NOTHING) != null) {
                char counter = _getCurrentCounter(keepOrder, SUBGROUP_NOTHING, subgroupCounters, sgPath);
                sb.append(counter).append('*').append(SUBGROUP_NOTHING).append(File.separator);
            }
        }

        return sb.toString();
    }

}
