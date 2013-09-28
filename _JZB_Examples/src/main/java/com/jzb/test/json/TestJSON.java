/**
 * 
 */
package com.jzb.test.json;

import org.json.JSONObject;


/**
 * @author jzarzuela
 * 
 */
public class TestJSON {

    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("\n***** EXECUTION STARTED *****\n");
            TestJSON me = new TestJSON();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("\n***** EXECUTION FINISHED [" + (t2 - t1) + "]*****\n");
            System.exit(1);
        } catch (Throwable th) {
            System.out.println("\n***** EXECUTION FAILED *****\n");
            th.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {

        JSONObject jsonData = new JSONObject();
        jsonData.put("param1", "value1");
        JSONObject req = new JSONObject();
        req.put("request", jsonData);

        String rspBodyText = "some json over http response";

        JSONObject jo = new JSONObject(rspBodyText);
        if (!jo.getString("code").equals("200")) {
            System.out.println("ok");
        }
    }
}
