/**
 * Rigel Services Model Infrastructure, Version 1.0
 *
 * Copyright (C) 2002 ISBAN.
 * All Rights Reserved.
 *
 **/

package com.jzb.vt;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;


/**
 * @author PS00A501
 *
 */
public class ExceptionMessage {

    public static void showExceptionMessage(Throwable th) {
        StringWriter sw=new StringWriter();
        PrintWriter pw=new PrintWriter(sw);
        th.printStackTrace(pw);
        pw.close();
        MessageBox mb=new MessageBox(new Shell(),SWT.ICON_ERROR);
        mb.setText("Error in application");
        mb.setMessage("Message: "+th.getMessage()+"\n\n"+sw.getBuffer());
        mb.open();
    }
}
