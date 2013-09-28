package com.jzb.dxc;

import java.io.File;

public class DivxCleaner {

    private int m_counter = 1;

    public static void main(String[] args) {
        try {
            System.out.println("***** EXECUTION STARTED *****");
            DivxCleaner me = new DivxCleaner();
            long t1 = System.currentTimeMillis();
            me.doIt(args);
            long t2 = System.currentTimeMillis();
            System.out.println("***** EXECUTION FINISHED [" + (t2 - t1) + "]*****");
        } catch (Throwable th) {
            System.out.println("***** EXECUTION FAILED *****");
            th.printStackTrace(System.out);
            if (Tracer.isInit())
                Tracer.error("***** EXECUTION FAILED *****", th);
        }
    }

    public void doIt(String[] args) throws Exception {

        if(args.length<1) {
            System.out.println("A 'config.xml' file must be passed as argument.");
            return;
        }
        
        File cfgFile = new File(args[0]);
        Config.loadConfig(cfgFile);

        for (File srcFolder : Config.getSrcFolders()) {
            Tracer.trace("\n*** Filtering files in folder: %s\n", srcFolder.getAbsolutePath());
            processFolder(srcFolder);
        }
        Tracer.trace("\n*** Filtering again files in destination folder: %s\n", Config.getDstFolder().getAbsolutePath());
        processFolder(Config.getDstFolder());
    }

    private File _getDstFolder(String newName) {
        File df;
        if ((Character.isDigit(newName.charAt(0))) && (newName.charAt(4) == '-')) {
            if (GenericFilterRule.lastSerieNameMatched != null)
                df = new File(Config.getDstFolder(), "Series/" + GenericFilterRule.lastSerieNameMatched);
            else
                df = new File(Config.getDstFolder(), "Series");
        } else {
            df = Config.getDstFolder();
        }
        return df;
    }

    private String _getExtension(String name) {
        int p = name.lastIndexOf('.');
        if (p > 0) {
            return name.substring(p);
        }
        return "";
    }

    private String _getName(String name) {
        int p = name.lastIndexOf('.');
        if (p > 0) {
            return name.substring(0, p);
        }
        return name;
    }

    private boolean _phisycalOSMove(File org, File dst) {
        try {
            //String[] pCmd = { "mv", "-n", "-v", "-T", org.toString(), dst.toString() };
            String[] pCmd = { "mv", "-n", "-v", org.toString(), dst.toString() };

            Process proc = Runtime.getRuntime().exec(pCmd);

            int returnVal = proc.waitFor();

            return returnVal == 0;
        } catch (Exception e) {
            Tracer.error("Error moving file '" + org + "' to '" + dst + "'", e);
        }
        return false;
    }


    private void processFolder(File srcFolder) {

        if(srcFolder.getName().startsWith("__")) {
            Tracer.trace("Skiping processing for folder with special name:" +srcFolder.getAbsolutePath());
            return;
        }
        
        for (File f : srcFolder.listFiles()) {

            if (f.isDirectory()) {
                processFolder(f);
            } else {

                String ext = _getExtension(f.getName());
                String name = _getName(f.getName());

                if (!Config.getExts().contains(ext.toLowerCase()))
                    continue;

                if(f.length()<10240) {
                    Tracer.trace("Deleting file to short: " + f.getAbsolutePath());
                    if(!f.delete()) {
                        Tracer.error("Error deleting file to short: " + f.getAbsolutePath());
                    }
                    continue;
                }

                String newName = Filters.filterName(name);
                
                File df = _getDstFolder(newName);
                File newFile = new File(df, newName + ext);
                
                if ((name.equals(newName)) && (df.equals(f.getParentFile()))) {
                    continue;
                }

                if (newFile.exists()) {

                    if (name.contains("_[#")) {
                        continue;
                    }

                    newName = newName + "_[#" + System.currentTimeMillis() + "_" + (this.m_counter++) + "#]";
                    newFile = new File(df, newName + ext);
                }

                Tracer.trace("Processing file: " + f.getAbsolutePath());
                Tracer.trace("    File moved to '%s'", newFile.getName());
                newFile.getParentFile().mkdirs();
                if (!_phisycalOSMove(f, newFile))
                    Tracer.error("Error moving file '%s' to '%s'", new Object[] { f, newFile });
            }
        }
    }
}