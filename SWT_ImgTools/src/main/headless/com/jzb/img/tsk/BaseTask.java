/**
 * 
 */
package com.jzb.img.tsk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public abstract class BaseTask {

    public static enum JustCheck {
        NO, YES
    }

    public static enum RecursiveProcessing {
        NO, YES
    }

    public static class TimeStampShift {

        public int years, months, days, hours, mins, secs;

        public TimeStampShift() {
        }

        public TimeStampShift(int shiftYears, int shiftMonths, int shiftDays, int shiftHours, int shiftMins, int shiftSecs) {
            this.years = shiftYears;
            this.months = shiftMonths;
            this.days = shiftDays;
            this.hours = shiftHours;
            this.mins = shiftMins;
            this.secs = shiftSecs;
        }
    }

    protected static class UndoInfo {

        public File newFile;
        public File origFile;
    }

    protected static final String         NO_TIME_STR            = "$0000=00=00 00=00=00$=";
    private static final SimpleDateFormat s_sdf_full             = new SimpleDateFormat("$yyyy=MM=dd HH=mm=ss$");
    private static final SimpleDateFormat s_sdf_days             = new SimpleDateFormat("$yyyy=MM=dd 00=00=00$");

    protected File                        m_baseFolder;
    protected JustCheck                   m_justChecking;

    protected NameComposer                m_nc                   = new NameComposer();

    protected RecursiveProcessing         m_recursive;
    private boolean                       m_dontUndo;
    private File                          m_undoFile;

    private boolean                       m_someFileProcessed;

    public static final String            SUBGROUP_NOTHING       = "!NOTHING!";
    public static final String            SUBGROUP_MARKER        = "@";
    public static final char              SUBGROUP_COUNTER_CHAR1 = '*';
    public static final char              SUBGROUP_COUNTER_CHAR2 = '%';

    // --------------------------------------------------------------------------------------------------------
    protected static void _deleteMacDotFiles(File folder) {
        for (File f : folder.listFiles()) {
            if (f.getName().equals(".DS_Store") || f.getName().equals("._.DS_Store")) {
                f.delete();
            }
        }
    }

    // --------------------------------------------------------------------------------------------------------
    protected BaseTask(JustCheck justChecking, File baseFolder, RecursiveProcessing recursive) {
        this(justChecking, baseFolder, recursive, false);
    }

    // --------------------------------------------------------------------------------------------------------
    protected BaseTask(JustCheck justChecking, File baseFolder, RecursiveProcessing recursive, boolean dontUndo) {

        m_justChecking = justChecking;
        m_recursive = recursive;
        m_baseFolder = baseFolder;
        m_dontUndo = dontUndo;
        _resetUndoFile();
    }

    // --------------------------------------------------------------------------------------------------------
    protected void _checkBaseFolder() throws IllegalArgumentException {
        if (m_baseFolder == null || !m_baseFolder.exists()) {
            throw new IllegalArgumentException("Base folder doesn't exist: '" + m_baseFolder + "'");
        }
    }

    // --------------------------------------------------------------------------------------------------------
    protected void _deleteFolder(File folder, JustCheck justChecking) {
        boolean done = justChecking == JustCheck.YES ? false : folder.delete();
        if (justChecking == JustCheck.YES || done) {
            Tracer._debug("Deleted empty folder: '" + folder.getName() + "'");
        } else {
            Tracer._error("Deleting empty folder: '" + folder.getName() + "'");
        }
    }

    // --------------------------------------------------------------------------------------------------------
    protected String _getExifDateStr(File file, TimeStampShift shiftTimeStamp, boolean justDay) {

        try {

            long timestamp = _getExifDateTimestamp(file, shiftTimeStamp);

            boolean fromEXIF = true;
            if (timestamp < 0) {
                fromEXIF = false;
                timestamp = -timestamp;
            }
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timestamp);
            if (justDay) {
                return s_sdf_days.format(cal.getTime()) + (fromEXIF ? "" : "*") + "=";
            } else {
                return s_sdf_full.format(cal.getTime()) + (fromEXIF ? "" : "*") + "=";
            }

        } catch (Exception e) {
            return NO_TIME_STR;
        }
    }

    // --------------------------------------------------------------------------------------------------------
    protected long _getExifDateTimestamp(File file, TimeStampShift shiftTimeStamp) {

        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            ExifSubIFDDirectory dir = metadata.getDirectory(ExifSubIFDDirectory.class);
            Date d = dir.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);

            Calendar cal = Calendar.getInstance();
            cal.setTime(d);

            cal.add(Calendar.SECOND, shiftTimeStamp.secs);
            cal.add(Calendar.MINUTE, shiftTimeStamp.mins);
            cal.add(Calendar.HOUR_OF_DAY, shiftTimeStamp.hours);
            cal.add(Calendar.DAY_OF_MONTH, shiftTimeStamp.days);
            cal.add(Calendar.MONTH, shiftTimeStamp.months);
            cal.add(Calendar.YEAR, shiftTimeStamp.years);

            return cal.getTimeInMillis();

        } catch (Exception e1) {
            return -file.lastModified();
        }
    }

    // --------------------------------------------------------------------------------------------------------
    protected ArrayList<UndoInfo> _getUndoInfo() throws Exception {

        ArrayList<UndoInfo> info = new ArrayList<UndoInfo>();

        File undoFile = new File(m_baseFolder, "lastAction_undo.txt");
        if (undoFile.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(undoFile));
            while (reader.ready()) {
                String line = reader.readLine();
                String files[] = line.split("-->");
                UndoInfo ui = new UndoInfo();
                ui.origFile = new File(files[0]);
                ui.newFile = new File(files[1]);
                info.add(ui);
            }
            reader.close();
        }
        return info;
    }

    // --------------------------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------------------------
    protected void _renameFile(File oldFile, File newFile) throws Exception {

        if (oldFile.equals(newFile)) {
            Tracer._debug("File     '" + oldFile.getName() + "'     doesn't need to be processed");
            return;
        }

        if (newFile.exists()) {
            Tracer._error("File '" + newFile.getName() + "' already exists");
            return;
        }

        if (!oldFile.canWrite()) {
            Tracer._warn("File '" + oldFile.getName() + "' can't be written (maybe read-only)");
            return;
        }

        if (!newFile.getParentFile().exists()) {
            boolean done = m_justChecking == JustCheck.YES ? false : newFile.getParentFile().mkdirs();
            if (m_justChecking == JustCheck.NO && !done) {
                Tracer._error("Error creating file's folder '" + newFile.getParentFile() + "'");
                return;
            }
        }

        _updateUndoFile(oldFile, newFile);

        if (!m_someFileProcessed) {
            Tracer._info("Some files needed to be processed");
        }
        m_someFileProcessed = true;

        boolean done = m_justChecking == JustCheck.YES ? false : oldFile.renameTo(newFile);
        if (m_justChecking == JustCheck.YES || done) {
            if (oldFile.getParentFile().equals(newFile.getParentFile())) {
                Tracer._debug("Processed file from     '" + oldFile.getName() + "'     to     '" + newFile.getName() + "'");
            } else {
                Tracer._debug("Processed file from     '" + oldFile.getName() + "'     to     '" + newFile.getParentFile().getName() + File.separator + newFile.getName() + "'");
            }
        } else {
            if (oldFile.getParentFile().equals(newFile.getParentFile())) {
                Tracer._error("Error processing from     '" + oldFile.getName() + "'     to     '" + newFile.getName() + "'");
            } else {
                Tracer._error("Error processing from     '" + oldFile.getName() + "'     to     '" + newFile.getParentFile().getName() + File.separator + newFile.getName() + "'");
            }
        }

    }

    // --------------------------------------------------------------------------------------------------------
    protected void _resetUndoFile() {

        if (m_dontUndo || m_justChecking == JustCheck.YES) {
            return;
        }

        try {
            File undoFile = new File(m_baseFolder, "lastAction_undo.txt");
            PrintWriter pw = new PrintWriter(new FileOutputStream(undoFile, false));
            pw.close();
            m_undoFile = undoFile;
        } catch (Exception ex) {
            Tracer._error("Error reseting Undo file", ex);
        }
    }

    // --------------------------------------------------------------------------------------------------------
    protected void _updateUndoFile(File origFile, File newFile) throws Exception {

        if (m_dontUndo || m_justChecking == JustCheck.YES) {
            return;
        }

        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(m_undoFile, true));
            pw.print(origFile.getAbsolutePath());
            pw.print("-->");
            pw.print(newFile.getAbsolutePath());
            pw.println();
            pw.close();
        } catch (Exception ex) {
            throw new Exception("Error updating Undo file with '" + origFile + "' --> '" + newFile + "'", ex);
        }
    }
}
