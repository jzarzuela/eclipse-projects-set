/**
 * 
 */
package com.jzb.img.tsk;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import com.jzb.futil.FileExtFilter;
import com.jzb.futil.FileExtFilter.IncludeFolders;
import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public class SplitByDate extends BaseTask {

    public static enum GroupByCloseness {
        NO, YES
    }

    private static class TGroupingInfo implements Comparable<TGroupingInfo> {

        public long    dist;
        public File    fImg;
        public boolean hasEXIFDate = true;
        public long    timestamp;

        /**
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(TGroupingInfo obj) {
            return (int) (this.timestamp - obj.timestamp);
        }
    }

    private static final long MIN_DISTANCE = 10L * 60000L;                      // En minutos
    private SimpleDateFormat  s_sdf1       = new SimpleDateFormat("yyyy=MM=dd");

    private SimpleDateFormat  s_sdf2       = new SimpleDateFormat("HH%mm%ss");

    // --------------------------------------------------------------------------------------------------------
    public SplitByDate(JustCheck justChecking, File baseFolder, RecursiveProcessing recursive) {
        super(justChecking, baseFolder, recursive);
    }

    // --------------------------------------------------------------------------------------------------------
    public void splitByDate(GroupByCloseness group, TimeStampShift shiftTimeStamp) {
        try {
            _checkBaseFolder();
            if (group == GroupByCloseness.YES) {
                _groupByDateCloseness(m_baseFolder, shiftTimeStamp);
            } else {
                _splitByDate(m_baseFolder, shiftTimeStamp);
            }
        } catch (Exception ex) {
            Tracer._error("Error processing action", ex);
        }
    }

    // ------------------------------------------------------------------------------------------------------------
    private long _calcExcessiveDist(List<TGroupingInfo> infos) {

        double avgDist = 0;
        double avgDev = 0;

        for (int n = 0; n < infos.size() - 1; n++) {
            avgDist += infos.get(n).dist;
        }
        avgDist /= infos.size() - 1;

        for (int n = 0; n < infos.size() - 1; n++) {
            avgDev += (infos.get(n).dist - avgDist) * (infos.get(n).dist - avgDist);
        }
        avgDev = Math.sqrt(avgDev / (infos.size() - 1));

        return (long) (avgDist + avgDev);
    }

    // --------------------------------------------------------------------------------------------------------
    private ArrayList<TGroupingInfo> _createGroupingInfo(ArrayList<File> allFiles, TimeStampShift shiftTimeStamp) {

        ArrayList<TGroupingInfo> grpInfo = new ArrayList<TGroupingInfo>();
        for (File fImg : allFiles) {
            @SuppressWarnings("synthetic-access")
            TGroupingInfo info = new TGroupingInfo();
            info.fImg = fImg;
            info.timestamp = _getExifDateTimestamp(fImg, shiftTimeStamp);
            if (info.timestamp < 0) {
                info.timestamp = -info.timestamp;
                info.hasEXIFDate = false;
            }
            grpInfo.add(info);
        }
        return grpInfo;
    }

    // ------------------------------------------------------------------------------------------------------------
    private long _getMaxDist(List<TGroupingInfo> infos) {

        long maxDist = 0;
        for (int n = 0; n < infos.size() - 1; n++) {
            if (infos.get(n).dist > maxDist) {
                maxDist = infos.get(n).dist;
            }
        }
        return maxDist;
    }

    // --------------------------------------------------------------------------------------------------------
    private void _groupByDateCloseness(File folder, TimeStampShift shiftTimeStamp) throws Exception {

        Tracer._debug("");
        Tracer._debug("Grouping files" + (m_recursive == RecursiveProcessing.YES ? " * RECURSIVELY * " : " ") + "attending to their creation date closeness in folder: '" + folder + "'");
        Tracer._debug("");

        // Gets just image files and folders
        File fList[] = folder.listFiles(FileExtFilter.imgFilter(IncludeFolders.YES));

        // Split files and folders to process them properly
        ArrayList<File> allFiles = new ArrayList<File>();
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
        ArrayList<TGroupingInfo> groupingInfos = _createGroupingInfo(allFiles, shiftTimeStamp);
        _sortAndCaldDist(groupingInfos);

        TreeMap<TGroupingInfo, List<TGroupingInfo>> groups = new TreeMap<TGroupingInfo, List<TGroupingInfo>>();
        _splitInGroups(groupingInfos, groups, MIN_DISTANCE);

        for (Map.Entry<TGroupingInfo, List<TGroupingInfo>> entry : groups.entrySet()) {

            if (entry.getValue().size() <= 0) {
                continue;
            }

            Date dateTS1 = new Date();
            dateTS1.setTime(entry.getKey().timestamp);

            Date dateTS2 = new Date();
            dateTS2.setTime(entry.getValue().get(entry.getValue().size() - 1).timestamp);

            String grpFolderName = (entry.getKey().hasEXIFDate ? "" : "*") + s_sdf1.format(dateTS1) + File.separator + s_sdf2.format(dateTS1) + " " + s_sdf2.format(dateTS2);

            for (TGroupingInfo info : entry.getValue()) {
                if (info.fImg.getParentFile().getAbsolutePath().endsWith(grpFolderName)) {
                    Tracer._debug("File already in folder with 'date' name: '" + info.fImg.getName() + "'");
                    continue;
                } else {
                    String newName = grpFolderName + File.separator + info.fImg.getName();
                    File newFile = new File(info.fImg.getParentFile(), newName);
                    _renameFile(info.fImg, newFile);
                }
            }
        }

        // ---------------------------------------------
        // Iterate subfolders
        if (m_recursive == RecursiveProcessing.YES) {
            for (File sfolder : subFolders) {
                if (sfolder.isDirectory()) {
                    _groupByDateCloseness(sfolder, shiftTimeStamp);
                }
            }
        }

    }

    // --------------------------------------------------------------------------------------------------------
    private void _sortAndCaldDist(ArrayList<TGroupingInfo> grpInfo) {

        Collections.sort(grpInfo);

        for (int n = 0; n < grpInfo.size() - 1; n++) {
            long dist = Math.abs(grpInfo.get(n).timestamp - grpInfo.get(n + 1).timestamp);
            grpInfo.get(n).dist = dist;
        }
    }

    // --------------------------------------------------------------------------------------------------------
    private void _splitByDate(File folder, TimeStampShift shiftTimeStamp) throws Exception {

        Tracer._debug("");
        Tracer._debug("Spliting files" + (m_recursive == RecursiveProcessing.YES ? " * RECURSIVELY * " : " ") + "attending to their creation date in folder: '" + folder + "'");
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
        for (File fImg : allFiles) {

            String fName = fImg.getName();
            String dateTimeFolderName = _getExifDateStr(fImg, shiftTimeStamp, true);

            if (fImg.getParentFile().getName().equals(dateTimeFolderName)) {
                Tracer._debug("File already in folder with 'date' name: '" + fName + "'");
                continue;
            }

            String newName = dateTimeFolderName + File.separator + fName;
            File newFile = new File(fImg.getParentFile(), newName);
            _renameFile(fImg, newFile);
        }

        // ---------------------------------------------
        // Iterate subfolders
        if (m_recursive == RecursiveProcessing.YES) {
            for (File sfolder : subFolders) {
                if (sfolder.isDirectory()) {
                    _splitByDate(sfolder, shiftTimeStamp);
                }
            }
        }

    }

    // --------------------------------------------------------------------------------------------------------
    private void _splitInGroups(List<TGroupingInfo> infos, TreeMap<TGroupingInfo, List<TGroupingInfo>> groups, long minDist) {

        if (infos == null || infos.size() == 0) {
            return;
        }

        long grpMaxDist = _getMaxDist(infos);
        if (infos.size() < 2 || grpMaxDist <= minDist) {
            groups.put(infos.get(0), infos);
            return;
        }

        long excessiveDist = _calcExcessiveDist(infos);

        ArrayList<List<TGroupingInfo>> groupsFound = new ArrayList<List<TGroupingInfo>>();

        int p1 = 0;
        for (int n = 0; n < infos.size() - 1; n++) {

            if (infos.get(n).dist > minDist && infos.get(n).dist > excessiveDist) {

                if (infos.get(n).fImg.getName().contains("8353")) {
                    System.out.println(infos.get(n).dist);
                }

                List<TGroupingInfo> group = infos.subList(p1, n + 1);
                groupsFound.add(group);
                p1 = n + 1;
            }
        }
        if (p1 < infos.size()) {
            List<TGroupingInfo> group = infos.subList(p1, infos.size());
            groupsFound.add(group);
        }

        if (groupsFound.size() > 1) {
            for (List<TGroupingInfo> infoGroup : groupsFound) {
                _splitInGroups(infoGroup, groups, minDist);
            }
        } else {
            groups.put(infos.get(0), infos);
        }

    }
}
