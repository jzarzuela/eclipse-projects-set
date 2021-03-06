/*
 * plist - An open source library to parse and generate property lists Copyright (C) 2012 Daniel Dreibrodt
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.dd.plist;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A NSDictionary is a collection of keys and values, essentially a Hashtable. The keys are simple Strings whereas the values can be any kind of NSObject.
 * 
 * You can access the keys through the function <code>allKeys()</code>. Access to the objects stored for each key is given through the function <code>objectoForKey(String key)</code>.
 * 
 * @see java.util.Hashtable
 * @see com.dd.plist.NSObject
 * @author Daniel Dreibrodt
 */
public class NSDictionary extends NSObject {

    private HashMap<String, NSObject> dict;

    /**
     * Creates a new empty NSDictionary.
     */
    public NSDictionary() {
        // With a linked HashMap the order of elements in the dictionary is kept.
        dict = new LinkedHashMap<String, NSObject>();
    }

    public String getStrCompoundValue(String key) {
        int p1 = key.indexOf('/');
        if(p1>0) {
            String subkey = key.substring(0,p1);
            String subkey2 = key.substring(p1+1);
            NSObject obj = objectForKey(subkey);
            if(obj!=null && obj instanceof NSDictionary) {
                return ((NSDictionary)obj).getStrCompoundValue(subkey2);
            } else {
                return null;
            }
        } else {
            return getStrValue(key);
        }
    }
    
    public String getStrValue(String key) {
        NSObject obj = objectForKey(key);
        return obj != null ? obj.toString() : null;
    }

    /**
     * Gets the NSObject stored for the given key.
     * 
     * @param key
     *            The key.
     * @return The object.
     */
    public NSObject objectForKey(String key) {
        return dict.get(key);
    }

    public void putAll(NSDictionary other) {
    
        dict.putAll(other.dict);
    }
    
    /**
     * Puts a new key-value pair into this dictionary.
     * 
     * @param key
     *            The key.
     * @param obj
     *            The value.
     */
    public void put(String key, NSObject obj) {
        dict.put(key, obj);
    }

    /**
     * Puts a new key-value pair into this dictionary.
     * 
     * @param key
     *            The key.
     * @param obj
     *            The value.
     */
    public void put(String key, String obj) {
        put(key, new NSString(obj));
    }

    /**
     * Puts a new key-value pair into this dictionary.
     * 
     * @param key
     *            The key.
     * @param obj
     *            The value.
     */
    public void put(String key, int obj) {
        put(key, new NSNumber(obj));
    }

    /**
     * Puts a new key-value pair into this dictionary.
     * 
     * @param key
     *            The key.
     * @param obj
     *            The value.
     */
    public void put(String key, long obj) {
        put(key, new NSNumber(obj));
    }

    /**
     * Puts a new key-value pair into this dictionary.
     * 
     * @param key
     *            The key.
     * @param obj
     *            The value.
     */
    public void put(String key, double obj) {
        put(key, new NSNumber(obj));
    }

    /**
     * Puts a new key-value pair into this dictionary.
     * 
     * @param key
     *            The key.
     * @param obj
     *            The value.
     */
    public void put(String key, boolean obj) {
        put(key, new NSNumber(obj));
    }

    /**
     * Puts a new key-value pair into this dictionary.
     * 
     * @param key
     *            The key.
     * @param obj
     *            The value.
     */
    public void put(String key, Date obj) {
        put(key, new NSDate(obj));
    }

    /**
     * Puts a new key-value pair into this dictionary.
     * 
     * @param key
     *            The key.
     * @param obj
     *            The value.
     */
    public void put(String key, byte[] obj) {
        put(key, new NSData(obj));
    }

    /**
     * Removes a key-value pair from this dictionary.
     * 
     * @param key
     *            The key
     */
    public void remove(String key) {
        dict.remove(key);
    }

    /**
     * Counts the number of contained key-value pairs.
     * 
     * @return The size of this NSDictionary.
     */
    public int count() {
        return dict.size();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj.getClass().equals(this.getClass()) && ((NSDictionary) obj).dict.equals(dict));
    }

    /**
     * Gets a list of all keys used in this NSDictionary.
     * 
     * @return The list of all keys used in this NSDictionary.
     */
    public String[] allKeys() {
        return dict.keySet().toArray(new String[0]);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.dict != null ? this.dict.hashCode() : 0);
        return hash;
    }

    @Override
    void toXML(StringBuilder xml, int level) {
        indent(xml, level);
        xml.append("<dict>");
        xml.append(NSObject.NEWLINE);
        for (String key : dict.keySet()) {
            NSObject val = objectForKey(key);
            indent(xml, level + 1);
            xml.append("<key>");
            // According to http://www.w3.org/TR/REC-xml/#syntax node values must not
            // contain the characters < or &. Also the > character should be escaped.
            if (key.contains("&") || key.contains("<") || key.contains(">")) {
                xml.append("<![CDATA[");
                xml.append(key.replaceAll("]]>", "]]]]><![CDATA[>"));
                xml.append("]]>");
            } else {
                xml.append(key);
            }
            xml.append("</key>");
            xml.append(NSObject.NEWLINE);
            val.toXML(xml, level + 1);
            xml.append(NSObject.NEWLINE);
        }
        indent(xml, level);
        xml.append("</dict>");
    }

    @Override
    void assignIDs(BinaryPropertyListWriter out) {
        super.assignIDs(out);
        for (Map.Entry<String, NSObject> entry : dict.entrySet()) {
            new NSString(entry.getKey()).assignIDs(out);
            entry.getValue().assignIDs(out);
        }
    }

    @Override
    void toBinary(BinaryPropertyListWriter out) throws IOException {
        out.writeIntHeader(0xD, dict.size());
        Set<Map.Entry<String, NSObject>> entries = dict.entrySet();
        for (Map.Entry<String, NSObject> entry : entries) {
            out.writeID(out.getID(new NSString(entry.getKey())));
        }
        for (Map.Entry<String, NSObject> entry : entries) {
            out.writeID(out.getID(entry.getValue()));
        }
    }

    /**
     * Generates a valid ASCII property list which has this NSDictionary as its root object. The generated property list complies with the format as described in <a
     * href="https://developer.apple.com/library/mac/#documentation/Cocoa/Conceptual/PropertyLists/OldStylePlists/OldStylePLists.html"> Property List Programming Guide - Old-Style ASCII Property
     * Lists</a>.
     * 
     * @return ASCII representation of this object.
     */
    public String toASCIIPropertyList() {
        StringBuilder ascii = new StringBuilder();
        toASCII(ascii, 0);
        ascii.append(NEWLINE);
        return ascii.toString();
    }

    /**
     * Generates a valid ASCII property list in GnuStep format which has this NSDictionary as its root object. The generated property list complies with the format as described in <a
     * href="http://www.gnustep.org/resources/documentation/Developer/Base/Reference/NSPropertyList.html"> GnuStep - NSPropertyListSerialization class documentation </a>
     * 
     * @return GnuStep ASCII representation of this object.
     */
    public String toGnuStepASCIIPropertyList() {
        StringBuilder ascii = new StringBuilder();
        toASCIIGnuStep(ascii, 0);
        ascii.append(NEWLINE);
        return ascii.toString();
    }

    @Override
    protected void toASCII(StringBuilder ascii, int level) {
        indent(ascii, level);
        ascii.append(ASCIIPropertyListParser.DICTIONARY_BEGIN_TOKEN);
        ascii.append(NSObject.NEWLINE);
        String[] keys = dict.keySet().toArray(new String[0]);
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            NSObject val = objectForKey(key);
            indent(ascii, level + 1);
            ascii.append("\"");
            ascii.append(NSString.escapeStringForASCII(key));
            ascii.append("\" =");
            Class<?> objClass = val.getClass();
            if (objClass.equals(NSDictionary.class) || objClass.equals(NSArray.class) || objClass.equals(NSData.class)) {
                ascii.append(NEWLINE);
                val.toASCII(ascii, level + 2);
            } else {
                ascii.append(" ");
                val.toASCII(ascii, 0);
            }
            ascii.append(ASCIIPropertyListParser.DICTIONARY_ITEM_DELIMITER_TOKEN);
            if (i != keys.length - 1) {
                ascii.append(NEWLINE);
            } else if (objClass.equals(NSDictionary.class) || objClass.equals(NSArray.class) || objClass.equals(NSData.class)) {
                ascii.append(NEWLINE);
                indent(ascii, level);
            }
        }
        ascii.append(ASCIIPropertyListParser.DICTIONARY_END_TOKEN);
    }

    @Override
    protected void toASCIIGnuStep(StringBuilder ascii, int level) {
        indent(ascii, level);
        ascii.append(ASCIIPropertyListParser.DICTIONARY_BEGIN_TOKEN);
        ascii.append(NSObject.NEWLINE);
        String[] keys = dict.keySet().toArray(new String[0]);
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            NSObject val = objectForKey(key);
            indent(ascii, level + 1);
            ascii.append("\"");
            ascii.append(NSString.escapeStringForASCII(key));
            ascii.append("\" =");
            Class<?> objClass = val.getClass();
            if (objClass.equals(NSDictionary.class) || objClass.equals(NSArray.class) || objClass.equals(NSData.class)) {
                ascii.append(NEWLINE);
                val.toASCIIGnuStep(ascii, level + 2);
            } else {
                ascii.append(" ");
                val.toASCIIGnuStep(ascii, 0);
            }
            ascii.append(ASCIIPropertyListParser.DICTIONARY_ITEM_DELIMITER_TOKEN);
            if (i != keys.length - 1) {
                ascii.append(NEWLINE);
            } else if (objClass.equals(NSDictionary.class) || objClass.equals(NSArray.class) || objClass.equals(NSData.class)) {
                ascii.append(NEWLINE);
                indent(ascii, level);
            }
        }
        ascii.append(ASCIIPropertyListParser.DICTIONARY_END_TOKEN);
    }
}