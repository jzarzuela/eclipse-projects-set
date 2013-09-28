/**
 * 
 */
package com.jzb.j2s;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;



/**
 * @author n63636
 * 
 */
public class Test1 extends BaseTest1 {


    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            
            loadNativeJS();
            
            long t1, t2;
            System.out.println("***** TEST STARTED *****");
            Test1 me = new Test1();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    /**
     * @j2sNative
     * var d=document;
     * var x=d.createElement('SCRIPT');
     * x.src='./js/log4js.js';
     * d.getElementsByTagName('HEAD')[0].appendChild(x);
     */
    private static void loadNativeJS() {
    }
    
    
    

        
    /**
     * @j2sNative
     * ClazzLoader.setLoadingMode("script");
     * ClazzLoader.loadClass(clazzName);
     * return Clazz.evalType(clazzName);
     */
    private Object loadClass2(String clazzName) {
        return null;
    }
    
    /**
     * @j2sNative
     *   var cad="";
     *   obj = obj.prototype;
     *   for(var i in obj) {
     *       try {
     *           cad+="'"+i+"' = ";
     *           if(typeof(obj[i]) == "function")
     *             cad+="function{}";
     *           else
     *             cad+=obj[i];
     *       }
     *       catch(e) {
     *           cad+="*error*";
     *       }
     *       cad+="\n";
     *   }
     *   return cad;
     */
    private String dumpClass(Object obj) {
        return "";
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


        System.out.println("hola");

        method1("");
        method2("");
        method6("",null,null,0,0,0,null);
        method6(1,null,null,null,null,null,null,null);
        
        // ~S,~O,Number,~N,~N,~N,com.jzb.j2s.Test1
        // '~N,~A,~A,~A,~A,~A,~A,~A
        
        Integer i = new Integer(1023);
        System.out.println(i.longValue());

        try {
            System.out.println(Long.getLong("pepe",12));
        } catch(Throwable th) {
            System.out.println(th.getMessage());
        }
        
        //loadClasses();
/**
        Object obj=loadClass2("com.jzb.j2s.Test1");
        String str=dumpClass(obj);
        System.out.println(str);
**/
        
        /*
        Class clazz = Test1.class;
        while(clazz!=null && !clazz.getName().equals("java.lang.Object")) {
        for(Method m:clazz.getDeclaredMethods()) {
            if(Modifier.isPublic(m.getModifiers()))
                System.out.println(clazz.getName() + "  -  " +m);
        }
        clazz = clazz.getSuperclass();
        }
       */
        
        /*
        Pattern regExpr = Pattern.compile("(.*)-[A|M]-ISO_[0-9]*\\.jpg", Pattern.CASE_INSENSITIVE);
        Matcher matcher = regExpr.matcher("fichero-M-ISO_123.jpg");
        if (matcher.matches()) {
            System.out.println("si");
        }
        else {
            System.out.println("no");
        }
        
        Ayuda y=new Ayuda();
        
       
        Object o=new Integer(12);
        System.out.println(y.hola("p1","P2"));
     */   
    }
    
    private void loadClasses() throws Exception {
        /*
        loadClass2("java.io.BufferedInputStream");
        loadClass2("java.io.BufferedOutputStream");
        loadClass2("java.io.BufferedReader");
        loadClass2("java.io.BufferedWriter");
        loadClass2("java.io.ByteArrayInputStream");
        loadClass2("java.io.ByteArrayOutputStream");
        loadClass2("java.io.CharArrayReader");
        loadClass2("java.io.CharArrayWriter");
        loadClass2("java.io.CharConversionException");
        loadClass2("java.io.Closeable");
        loadClass2("java.io.DataInput");
        loadClass2("java.io.DataOutput");
        loadClass2("java.io.EOFException");
        loadClass2("java.io.Externalizable");
        loadClass2("java.io.FileInputStream");
        loadClass2("java.io.FileNotFoundException");
        loadClass2("java.io.FileOutputStream");
        loadClass2("java.io.FilterInputStream");
        loadClass2("java.io.FilterOutputStream");
        loadClass2("java.io.Flushable");
        loadClass2("java.io.InputStream");
        loadClass2("java.io.InterruptedIOException");
        loadClass2("java.io.InvalidClassException");
        loadClass2("java.io.InvalidObjectException");
        loadClass2("java.io.IOException");
        loadClass2("java.io.NotActiveException");
        loadClass2("java.io.NotSerializableException");
        loadClass2("java.io.ObjectStreamException");
        loadClass2("java.io.ObjectStreamField");
        loadClass2("java.io.OptionalDataException");
        loadClass2("java.io.OutputStream");
        loadClass2("java.io.Reader");
        loadClass2("java.io.Serializable");
        loadClass2("java.io.StreamCorruptedException");
        loadClass2("java.io.StringBufferInputStream");
        loadClass2("java.io.StringReader");
        loadClass2("java.io.StringWriter");
        loadClass2("java.io.SyncFailedException");
        loadClass2("java.io.UnsupportedEncodingException");
        loadClass2("java.io.UTFDataFormatException");
        loadClass2("java.io.WriteAbortedException");
        loadClass2("java.io.Writer");
        */
        //loadClass2("java.lang.AbstractMethodError");
        //loadClass2("java.lang.AbstractStringBuilder");
        /*
        loadClass2("java.lang.annotation.Annotation");
        loadClass2("java.lang.annotation.AnnotationFormatError");
        loadClass2("java.lang.annotation.AnnotationTypeMismatchException");
        loadClass2("java.lang.annotation.Documented");
        loadClass2("java.lang.annotation.ElementType");
        loadClass2("java.lang.annotation.IncompleteAnnotationException");
        loadClass2("java.lang.annotation.Inherited");
        loadClass2("java.lang.annotation.Retention");
        loadClass2("java.lang.annotation.RetentionPolicy");
        loadClass2("java.lang.annotation.Target");
        */
        loadClass2("java.lang.Appendable");
        loadClass2("java.lang.ArithmeticException");
        loadClass2("java.lang.ArrayIndexOutOfBoundsException");
        loadClass2("java.lang.ArrayStoreException");
        loadClass2("java.lang.AssertionError");
        loadClass2("java.lang.Boolean");
        loadClass2("java.lang.Byte");
        loadClass2("java.lang.Character");
        loadClass2("java.lang.CharSequence");
        loadClass2("java.lang.Class");
        loadClass2("java.lang.ClassCastException");
        loadClass2("java.lang.ClassCircularityError");
        loadClass2("java.lang.ClassExt");
        loadClass2("java.lang.ClassFormatError");
        loadClass2("java.lang.ClassLoader");
        loadClass2("java.lang.ClassLoaderProgressMonitor");
        loadClass2("java.lang.ClassNotFoundException");
        loadClass2("java.lang.Cloneable");
        loadClass2("java.lang.CloneNotSupportedException");
        loadClass2("java.lang.Comparable");
        loadClass2("java.lang.Console");
        loadClass2("java.lang.Double");
        loadClass2("java.lang.Encoding");
        loadClass2("java.lang.Enum");
        loadClass2("java.lang.Error");
        loadClass2("java.lang.Exception");
        loadClass2("java.lang.ExceptionInInitializerError");
        loadClass2("java.lang.Float");
        loadClass2("java.lang.IllegalAccessError");
        loadClass2("java.lang.IllegalAccessException");
        loadClass2("java.lang.IllegalArgumentException");
        loadClass2("java.lang.IllegalMonitorStateException");
        loadClass2("java.lang.IllegalStateException");
        loadClass2("java.lang.IllegalThreadStateException");
        loadClass2("java.lang.IncompatibleClassChangeError");
        loadClass2("java.lang.IndexOutOfBoundsException");
        loadClass2("java.lang.InstantiationError");
        loadClass2("java.lang.InstantiationException");
        loadClass2("java.lang.Integer");
        loadClass2("java.lang.InternalError");
        loadClass2("java.lang.InterruptedException");
        loadClass2("java.lang.Iterable");
        loadClass2("java.lang.LinkageError");
        loadClass2("java.lang.Long");
        loadClass2("java.lang.NegativeArraySizeException");
        loadClass2("java.lang.NoClassDefFoundError");
        loadClass2("java.lang.NoSuchFieldError");
        loadClass2("java.lang.NoSuchFieldException");
        loadClass2("java.lang.NoSuchMethodError");
        loadClass2("java.lang.NoSuchMethodException");
        loadClass2("java.lang.NullPointerException");
        loadClass2("java.lang.Number");
        loadClass2("java.lang.NumberFormatException");
        loadClass2("java.lang.OutOfMemoryError");
        loadClass2("java.lang.Readable");
        /*
        loadClass2("java.lang.reflect.AccessibleObject");
        loadClass2("java.lang.reflect.AnnotatedElement");
        loadClass2("java.lang.reflect.Array");
        loadClass2("java.lang.reflect.Constructor");
        loadClass2("java.lang.reflect.Field");
        loadClass2("java.lang.reflect.GenericDeclaration");
        loadClass2("java.lang.reflect.GenericSignatureFormatError");
        loadClass2("java.lang.reflect.InvocationHandler");
        loadClass2("java.lang.reflect.InvocationTargetException");
        loadClass2("java.lang.reflect.MalformedParameterizedTypeException");
        loadClass2("java.lang.reflect.Member");
        loadClass2("java.lang.reflect.Method");
        loadClass2("java.lang.reflect.Modifier");
        loadClass2("java.lang.reflect.Proxy");
        loadClass2("java.lang.reflect.ReflectPermission");
        loadClass2("java.lang.reflect.TypeVariable");
        loadClass2("java.lang.reflect.UndeclaredThrowableException");
        loadClass2("java.lang.reflect.z");
        */
        loadClass2("java.lang.Runnable");
        loadClass2("java.lang.RuntimeException");
        loadClass2("java.lang.SecurityException");
        loadClass2("java.lang.Short");
        loadClass2("java.lang.StackOverflowError");
        loadClass2("java.lang.StackTraceElement");
        loadClass2("java.lang.StrictMath");
        loadClass2("java.lang.String");
        loadClass2("java.lang.StringBuffer");
        loadClass2("java.lang.StringBuilder");
        loadClass2("java.lang.StringBuilder.z");
        loadClass2("java.lang.StringIndexOutOfBoundsException");
        loadClass2("java.lang.Thread");
        loadClass2("java.lang.ThreadDeath");
        loadClass2("java.lang.ThreadGroup");
        loadClass2("java.lang.Throwable");
        loadClass2("java.lang.TypeNotPresentException");
        loadClass2("java.lang.UnknownError");
        loadClass2("java.lang.UnsatisfiedLinkError");
        loadClass2("java.lang.UnsupportedClassVersionError");
        loadClass2("java.lang.UnsupportedOperationException");
        loadClass2("java.lang.VerifyError");
        loadClass2("java.lang.VirtualMachineError");
        loadClass2("java.lang.Void");
        /*
        loadClass2("java.net.URL");
        loadClass2("java.net.URLDecoder");
        loadClass2("java.net.URLEncoder");
        */
        loadClass2("java.text.Annotation");
        loadClass2("java.text.MessageFormat");
        loadClass2("java.text.SimpleDateFormat");
        loadClass2("java.util.AbstractCollection");
        loadClass2("java.util.AbstractList");
        loadClass2("java.util.AbstractMap");
        loadClass2("java.util.AbstractQueue");
        loadClass2("java.util.AbstractSequentialList");
        loadClass2("java.util.AbstractSet");
        loadClass2("java.util.ArrayList");
        loadClass2("java.util.Arrays");
        loadClass2("java.util.Collection");
        loadClass2("java.util.Collections");
        loadClass2("java.util.Comparator");
        loadClass2("java.util.ConcurrentModificationException");
        loadClass2("java.util.Date");
        loadClass2("java.util.Dictionary");
        loadClass2("java.util.DuplicateFormatFlagsException");
        loadClass2("java.util.EmptyStackException");
        loadClass2("java.util.Enumeration");
        loadClass2("java.util.EventListener");
        loadClass2("java.util.EventListenerProxy");
        loadClass2("java.util.EventObject");
        loadClass2("java.util.FormatFlagsConversionMismatchException");
        loadClass2("java.util.FormatterClosedException");
        loadClass2("java.util.HashMap");
        loadClass2("java.util.HashSet");
        loadClass2("java.util.Hashtable");
        loadClass2("java.util.IdentityHashMap");
        loadClass2("java.util.IllegalFormatCodePointException");
        loadClass2("java.util.IllegalFormatConversionException");
        loadClass2("java.util.IllegalFormatException");
        loadClass2("java.util.IllegalFormatFlagsException");
        loadClass2("java.util.IllegalFormatPrecisionException");
        loadClass2("java.util.IllegalFormatWidthException");
        loadClass2("java.util.InputMismatchException");
        loadClass2("java.util.InvalidPropertiesFormatException");
        loadClass2("java.util.Iterator");
        loadClass2("java.util.LinkedHashMap");
        loadClass2("java.util.LinkedHashSet");
        loadClass2("java.util.LinkedList");
        loadClass2("java.util.List");
        loadClass2("java.util.ListIterator");
        loadClass2("java.util.ListResourceBundle");
        loadClass2("java.util.Locale");
        loadClass2("java.util.Map");
        loadClass2("java.util.MapEntry");
        loadClass2("java.util.MissingFormatArgumentException");
        loadClass2("java.util.MissingFormatWidthException");
        loadClass2("java.util.MissingResourceException");
        loadClass2("java.util.NoSuchElementException");
        loadClass2("java.util.Observable");
        loadClass2("java.util.Observer");
        loadClass2("java.util.Properties");
        loadClass2("java.util.Queue");
        loadClass2("java.util.Random");
        loadClass2("java.util.RandomAccess");
        loadClass2("java.util.regex.Matcher");
        loadClass2("java.util.regex.MatchResult");
        loadClass2("java.util.regex.Pattern");
        loadClass2("java.util.regex.PatternSyntaxException");
        loadClass2("java.util.ResourceBundle");
        loadClass2("java.util.Set");
        loadClass2("java.util.SortedMap");
        loadClass2("java.util.SortedSet");
        loadClass2("java.util.Stack");
        loadClass2("java.util.StringTokenizer");
        loadClass2("java.util.TooManyListenersException");
        loadClass2("java.util.TreeMap");
        loadClass2("java.util.TreeSet");
        loadClass2("java.util.UnknownFormatConversionException");
        loadClass2("java.util.UnknownFormatFlagsException");
        loadClass2("java.util.Vector");
        loadClass2("java.util.WeakHashMap");
    }
    
    @Override
    public void method2(String cad) {
        System.out.println("-- MT --> Test1.method2(cad)");
        super.method2(cad);
    }
    
    public void method6(String cad, Object o, Number n, int i, long l, float f, Test1 me) {
        System.out.println("-- MT --> Test1.method6-1(cad, o, n, i, l, f, me)");
        if(cad!=null) cad.toCharArray();
    }
    
    public void method6(int x,String cad[], Object o[], Number n[], int i[], long l[], float f[], Test1 me[]) {
        System.out.println("-- MT --> Test1.method6-2(cad, o, n, i, l, f, me)");
        if(cad!=null) cad[0].toCharArray();
    }
}
