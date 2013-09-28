/**
 * 
 */

import java.io.File;
import java.util.Calendar;

import com.jzb.img.tsk.BaseTask;
import com.jzb.img.tsk.CleanName;
import com.jzb.img.tsk.CleanName.ForceClean;
import com.jzb.img.tsk.IPadResize;
import com.jzb.img.tsk.MoveFromSubfolders;
import com.jzb.img.tsk.NameComposer;
import com.jzb.img.tsk.RenameWithRegExpr;
import com.jzb.img.tsk.RenameWithFolders;
import com.jzb.img.tsk.RenameWithTimestamp;
import com.jzb.img.tsk.Renumerate;
import com.jzb.img.tsk.SplitByDate;
import com.jzb.img.tsk.BaseTask.JustCheck;
import com.jzb.img.tsk.BaseTask.RecursiveProcessing;
import com.jzb.img.tsk.MoveFromSubfolders.DeleteEmpty;
import com.jzb.img.tsk.SplitByDate.GroupByCloseness;

/**
 * @author jzarzuela
 * 
 */
@SuppressWarnings("unused")
public class _TestTasks {

    // --------------------------------------------------------------------------------------------------------
    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("***** TEST STARTED *****");
            _TestTasks me = new _TestTasks();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
            System.exit(1);
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    // --------------------------------------------------------------------------------------------------------
    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {

        // File baseFolder = new File("/Users/jzarzuela/Documents/_TMP_/test/2010_03_18-21_Leon");
        // File baseFolder = new File("/Users/jzarzuela/Documents/_TMP_/test/2006_03_20-24_Dublin");
        File baseFolder = new File("/Users/jzarzuela/Desktop/jzb/backup-discoextr/_Backup_/_Fotos_/Business_Jose");

        // _test_CompoundNames();
        // _test_NameComposer();

        // _test_CleanName(JustCheck.YES, baseFolder, RecursiveProcessing.YES, ForceClean.YES);
        // _test_MoveFromSubfolders(JustCheck.YES, baseFolder, RecursiveProcessing.YES);
        // _test_RenameWithTimestamp(JustCheck.YES, baseFolder, RecursiveProcessing.YES);
        // _test_SplitByDate(JustCheck.YES, baseFolder, RecursiveProcessing.YES);
        // _test_RenameAsFolder(JustCheck.YES, baseFolder, RecursiveProcessing.YES, AggregateFolders.YES);
        // _test_Renumerate(JustCheck.YES, baseFolder, RecursiveProcessing.YES, 10, ResetByFolder.YES);
        // _test_SearchNamePattern(JustCheck.YES, baseFolder, RecursiveProcessing.YES);
        // _test_RanameByPattern(JustCheck.YES, baseFolder, RecursiveProcessing.YES,"Dublin_(@@@@@)-([^-]*)-IMGP(@@@@@)","Dublin-#\\1_\\2_[IMGP\\3]");
        // _test_RanameTryCompoundName(JustCheck.YES, baseFolder, RecursiveProcessing.YES);

        baseFolder = new File("/Users/jzarzuela/Documents/_TMP_/test/__done__/Business_Jose/2012_11_15-Zurich/Organizadas/");
        baseFolder = new File("/Users/jzarzuela/Desktop/pp");
        baseFolder = new File("/Users/jzarzuela/Documents/_TMP_/test/__done__");
        _test_IPadResize(JustCheck.YES, baseFolder, RecursiveProcessing.YES);

    }

    // --------------------------------------------------------------------------------------------------------
    private void _test_IPadResize(JustCheck justChecking, File baseFolder, RecursiveProcessing recursive) throws Exception {

        File dstFolder = new File("/Users/jzarzuela/Desktop/Zurich-ipad2");

        IPadResize task = new IPadResize(justChecking, baseFolder, recursive);
        task.resize(dstFolder,"Filtradas_NO","Organizadas");
    }

    // --------------------------------------------------------------------------------------------------------
    private void _test_CleanName(JustCheck justChecking, File baseFolder, RecursiveProcessing recursive, ForceClean force) throws Exception {
        CleanName task = new CleanName(justChecking, baseFolder, recursive);
        task.cleanName(force);
    }

    // --------------------------------------------------------------------------------------------------------
    private void _test_CompoundNames() throws Exception {
        String name = "uno-dos-#00000-juan-$0000=00=00-00=00=00$*=pedro_pepe_[pepe].pppppp";
        System.out.println(NameComposer.isCompoundName(name));
    }

    // --------------------------------------------------------------------------------------------------------
    private void _test_MoveFromSubfolders(JustCheck justChecking, File baseFolder, RecursiveProcessing recursive) throws Exception {
        MoveFromSubfolders task = new MoveFromSubfolders(justChecking, baseFolder, recursive);
        task.moveFromSubfolder(DeleteEmpty.YES);
    }

    // --------------------------------------------------------------------------------------------------------
    private void _test_NameComposer() throws Exception {

        NameComposer nc = new NameComposer();

        nc.addGroupName("grp1");
        nc.addGroupName("grp2");
        nc.setIndex(12);
        nc.addSubgroupName("sgrp1");
        nc.addSubgroupName("sgrp2");
        nc.setName("name1");
        nc.setImgFileName("imgname");
        nc.setFileExt(".jpg");

        String compName = nc.compose();
        System.out.println(compName);

        System.out.println();

        nc.parse(compName);
        System.out.println(nc.compose());

        System.out.println();

        nc.parse("grp1-grp2-#00012-sgrp1-sgrp2_name1_[imgname].jpg");
        nc.parse("#00012_juan-pepe_[imgname].jpg");
        System.out.println(nc.compose());
    }

    // --------------------------------------------------------------------------------------------------------
    private void _test_RanameByPattern(JustCheck justChecking, File baseFolder, RecursiveProcessing recursive, String regexpr, String replacement) throws Exception {
        RenameWithRegExpr task = new RenameWithRegExpr(justChecking, baseFolder, recursive);
        task.renameByRegExpr(regexpr, replacement);
    }

    // --------------------------------------------------------------------------------------------------------
    private void _test_RanameTryCompoundName(JustCheck justChecking, File baseFolder, RecursiveProcessing recursive) throws Exception {
        RenameWithRegExpr task = new RenameWithRegExpr(justChecking, baseFolder, recursive);
        task.renameTryCompoundName();
    }

    // --------------------------------------------------------------------------------------------------------
    private void _test_RenameAsFolder(JustCheck justChecking, File baseFolder, RecursiveProcessing recursive) throws Exception {
        RenameWithFolders task = new RenameWithFolders(justChecking, baseFolder, recursive);
        task.renameAsSubfolder();
    }

    // --------------------------------------------------------------------------------------------------------
    private void _test_RenameWithTimestamp(JustCheck justChecking, File baseFolder, RecursiveProcessing recursive) throws Exception {
        RenameWithTimestamp task = new RenameWithTimestamp(justChecking, baseFolder, recursive);
        BaseTask.TimeStampShift tshift = new BaseTask.TimeStampShift(100, 10, 0, 0, 0, 0);
        task.addTimeDate(tshift);
        // task.removeTimeDate();
    }

    // --------------------------------------------------------------------------------------------------------
    private void _test_Renumerate(JustCheck justChecking, File baseFolder, RecursiveProcessing recursive, int baseCounter) throws Exception {
        Renumerate task = new Renumerate(justChecking, baseFolder, recursive);
        task.renumerate(baseCounter);
    }

    // --------------------------------------------------------------------------------------------------------
    private void _test_SearchNamePattern(JustCheck justChecking, File baseFolder, RecursiveProcessing recursive) throws Exception {
        RenameWithRegExpr task = new RenameWithRegExpr(justChecking, baseFolder, recursive);
        task.searchPatterns();
    }

    // --------------------------------------------------------------------------------------------------------
    private void _test_SplitByDate(JustCheck justChecking, File baseFolder, RecursiveProcessing recursive) throws Exception {
        SplitByDate task = new SplitByDate(justChecking, baseFolder, recursive);
        task.splitByDate(GroupByCloseness.NO, new BaseTask.TimeStampShift());
    }

}
