/**
 * 
 */
package gmap.engine;

import gmap.engine.parser.GMapServiceParser;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * @author jzarzuela
 *
 */
public class StylePrint {

    /**
     * 
     */
    public StylePrint() {
        // TODO Auto-generated constructor stub
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        long t1 = 0, t2 = 0;
        try {
            System.out.println("\n***** EXECUTION STARTED *****\n");
            StylePrint me = new StylePrint();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("\n***** EXECUTION FINISHED [" + (t2 - t1) + "]*****\n");
            System.exit(1);
        } catch (Throwable th) {
            t2 = System.currentTimeMillis();
            System.out.println("***** EXECUTION FAILED [" + (t2 - t1) + "]*****");
            th.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {

        File f = new File("/Users/jzarzuela/Documents/Java/github-repos/eclipse-projects-set/Google My Maps/src/main/java/gmap/engine/style.txt");
        FileReader fr = new FileReader(f);
        char buffer[] = new char[(int) f.length() + 4];
        int len = fr.read(buffer);
        fr.close();
        if (len <= 0 || len == buffer.length) {
            throw new Exception("Error de longitud: " + len);
        }

        String str = new String(buffer, 0, len);
        ArrayList<Object> rootContainer = new ArrayList<Object>();
        //GMapServiceParser._parseStrToArrays(str, 0, rootContainer);
        String strStyle = "[\"zeLPXIl-X_4c.kAZUmasUiKNQ\", null, null, null, null, null, [\"zeLPXIl-X_4c.k0rVgcYf3XT4\", [\"Style3\", null, [[[1, [1, null, [[null, null, 1, 0], [[null, null, null, null, \"087AA507742099AF\", null, null, 0], null, null, 0]]], [], 0],[[503, null, null, [\"62AF44\", 1], null,[[\"OtraCol\", \"name\", \"description\", \"gx_image_links\", \"place_ref\"],\"{OtraCol|title}{gx_image_links|images}{name|attributerow}{description|attributerow}{place_ref|placeref}\"], null, null, [null, null, null, 0]],[1200, [\"62AF44\", 1.0], null,[[\"OtraCol\", \"name\", \"description\", \"gx_image_links\", \"place_ref\"],\"{OtraCol|title}{gx_image_links|images}{name|attributerow}{description|attributerow}{place_ref|placeref}\"], null, null, [null]],[[\"62AF44\", 1], 1200, [\"62AF44\", 1.0], null, [[\"OtraCol\", \"name\", \"description\", \"gx_image_links\", \"place_ref\"],\"{OtraCol|title}{gx_image_links|images}{name|attributerow}{description|attributerow}{place_ref|placeref}\"], 0]]], [[1, [1, null, [[null, null, 1, 0], [[null, null, null, null, \"087AA3FE26972ED0\", null, null, 0], null, null, 0]]], [], 0],[[965, null, null, [\"0000FF\", 1], null,[[\"OtraCol\", \"name\", \"description\", \"gx_image_links\", \"place_ref\"],\"{OtraCol|title}{gx_image_links|images}{name|attributerow}{description|attributerow}{place_ref|placeref}\"], null, null, [null, null, null, 0]],[1200, [\"62AF44\", 1.0], null,[[\"OtraCol\", \"name\", \"description\", \"gx_image_links\", \"place_ref\"],\"{OtraCol|title}{gx_image_links|images}{name|attributerow}{description|attributerow}{place_ref|placeref}\"], null, null, [null]],[[\"62AF44\", 1], 1200, [\"62AF44\", 1.0], null, [[\"OtraCol\", \"name\", \"description\", \"gx_image_links\", \"place_ref\"],\"{OtraCol|title}{gx_image_links|images}{name|attributerow}{description|attributerow}{place_ref|placeref}\"], 0]]], [null, [[503, null, null, [\"62AF44\", 1], null,[[\"OtraCol\", \"name\", \"description\", \"gx_image_links\", \"place_ref\"],\"{OtraCol|title}{gx_image_links|images}{name|attributerow}{description|attributerow}{place_ref|placeref}\"], null, null, [null, null, null, 0]],[1200, [\"62AF44\", 1.0], null,[[\"OtraCol\", \"name\", \"description\", \"gx_image_links\", \"place_ref\"],\"{OtraCol|title}{gx_image_links|images}{name|attributerow}{description|attributerow}{place_ref|placeref}\"], null, null, [null]],[[\"62AF44\", 1], 1200, [\"62AF44\", 1.0], null, [[\"OtraCol\", \"name\", \"description\", \"gx_image_links\", \"place_ref\"],\"{OtraCol|title}{gx_image_links|images}{name|attributerow}{description|attributerow}{place_ref|placeref}\"], 0]]]]],[6, [1200, 1200, 0.3, \"000000\", \"DB4436\", null, \"000000\", 503], null, null, [], [], [0, 1], null, null,[\"OtraCol\", \"name\", \"description\"], null, null, null, null,[null], null, [null]]], null, null, null, null, null, null, null, null, null, [[]]]";
        GMapServiceParser._parseStrToArrays(strStyle, 0, rootContainer);

        String txt = GMapServiceParser._prettyPrintArrays(rootContainer);
        System.out.println(txt);

    }
}
