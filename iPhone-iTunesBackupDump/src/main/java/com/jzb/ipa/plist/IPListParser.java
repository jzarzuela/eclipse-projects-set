/**
 * 
 */
package com.jzb.ipa.plist;


/**
 * @author n000013
 * 
 */
public interface IPListParser {

    public T_PLDict parsePList(byte buffer[]) throws Exception;
}
