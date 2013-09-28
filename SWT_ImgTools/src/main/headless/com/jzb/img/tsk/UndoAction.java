/**
 * 
 */
package com.jzb.img.tsk;

import java.io.File;
import java.util.ArrayList;

import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public class UndoAction extends BaseTask {

    // --------------------------------------------------------------------------------------------------------
    public UndoAction(JustCheck justChecking, File baseFolder, RecursiveProcessing recursive) {
        super(justChecking, baseFolder, recursive, true);
    }

    // --------------------------------------------------------------------------------------------------------
    public void undo() {
        try {
            _checkBaseFolder();
            _undo(m_baseFolder);
        } catch (Exception ex) {
            Tracer._error("Error processing action", ex);
        }
    }

    // --------------------------------------------------------------------------------------------------------
    private void _undo(File folder) throws Exception {

        Tracer._debug("");
        Tracer._debug("Undoing last action made on files.");
        Tracer._debug("");

        ArrayList<UndoInfo> info = _getUndoInfo();
        for (UndoInfo ui : info) {
            _renameFile(ui.newFile, ui.origFile);
        }

    }
}
