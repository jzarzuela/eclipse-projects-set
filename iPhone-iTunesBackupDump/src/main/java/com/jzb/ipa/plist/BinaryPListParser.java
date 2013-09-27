/**
 * 
 */
package com.jzb.ipa.plist;

//import com.jidesoft.utils.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;


/**
 * Reads a binary PList file and returns it as a NanoXML XMLElement.
 * <p/>
 * The NanoXML XMLElement returned by this reader is equivalent to the XMLElement returned, if a PList file in XML format is parsed with NanoXML.
 * <p/>
 * Description about property list taken from <a href="http://developer.apple.com/documentation/Cocoa/Conceptual/PropertyLists/index.html#//apple_ref/doc/uid/10000048i"> Apple's online
 * documentation</a>:
 * <p/>
 *"A property list is a data representation used by Mac OS X Cocoa and Core Foundation as a convenient way to store, organize, and access standard object types. Frequently called a plist, a property
 * list is an object of one of several certain Cocoa or Core Foundation types, including arrays, dictionaries, strings, binary data, numbers, dates, and Boolean values. If the object is a container
 * (an array or dictionary), all objects contained within it must also be supported property list objects. (Arrays and dictionaries can contain objects not supported by the architecture, but are then
 * not property lists, and cannot be saved and restored with the various property list methods.)"
 * <p/>
 * XXX - This implementation can not read date values. Date values will always have the current date.
 * 
 * @author Werner Randelshofer
 * @version 0.1 June 18, 2005 Created.
 * @see XMLElement
 */
public class BinaryPListParser implements IPListParser {

    /*
     * Description of the binary plist format derived from
     * http://cvs.opendarwin.org/cgi-bin/cvsweb.cgi/~checkout~/src/CoreFoundation/Parsing.subproj/CFBinaryPList.c?rev=1.1.1.3&content-type=text/plain
     * 
     * EBNF description of the file format: <pre> bplist ::= header objectTable offsetTable trailer
     * 
     * header ::= magicNumber fileFormatVersion magicNumber ::= "bplist" fileFormatVersion ::= "00"
     * 
     * objectTable ::= { null | bool | fill | number | date | data | string | uid | array | dict }
     * 
     * null ::= 0b0000 0b0000
     * 
     * bool ::= false | true false ::= 0b0000 0b1000 true ::= 0b0000 0b1001
     * 
     * fill ::= 0b0000 0b1111 // fill byte
     * 
     * number ::= int | real int ::= 0b0001 0bnnnn byte*(2^nnnn) // 2^nnnn big-endian bytes real ::= 0b0010 0bnnnn byte*(2^nnnn) // 2^nnnn big-endian bytes
     * 
     * date ::= 0b0011 0b0011 byte*8 // 8 byte float big-endian bytes
     * 
     * data ::= 0b0100 0bnnnn [int] byte* // nnnn is number of bytes // unless 0b1111 then a int // variable-sized object follows // to indicate the number of bytes
     * 
     * string ::= asciiString | unicodeString asciiString ::= 0b0101 0bnnnn [int] byte* unicodeString ::= 0b0110 0bnnnn [int] short* // nnnn is number of bytes // unless 0b1111 then a int //
     * variable-sized object follows // to indicate the number of bytes
     * 
     * uid ::= 0b1000 0bnnnn byte* // nnnn+1 is # of bytes
     * 
     * array ::= 0b1010 0bnnnn [int] objref* // // nnnn is number of objref // unless 0b1111 then a int // variable-sized object follows // to indicate the number of objref
     * 
     * dict ::= 0b1010 0bnnnn [int] keyref* objref* // nnnn is number of keyref and // objref pairs // unless 0b1111 then a int // variable-sized object follows // to indicate the number of pairs
     * 
     * objref = byte | short // if refCount // is less than 256 then objref is // an unsigned byte, otherwise it // is an unsigned big-endian short
     * 
     * keyref = byte | short // if refCount // is less than 256 then objref is // an unsigned byte, otherwise it // is an unsigned big-endian short
     * 
     * unused ::= 0b0111 0bxxxx | 0b1001 0bxxxx | 0b1011 0bxxxx | 0b1100 0bxxxx | 0b1110 0bxxxx | 0b1111 0bxxxx
     * 
     * 
     * offsetTable ::= { int } // list of ints, byte size of which // is given in trailer // these are the byte offsets into // the file // number of these is in the trailer
     * 
     * trailer ::= refCount offsetCount objectCount topLevelOffset
     * 
     * refCount ::= byte*8 // unsigned big-endian long offsetCount ::= byte*8 // unsigned big-endian long objectCount ::= byte*8 // unsigned big-endian long topLevelOffset ::= byte*8 // unsigned
     * big-endian long </pre>
     */

    /**
     * Total count of objrefs and keyrefs.
     */
    private int       refCount;
    /**
     * Total count of ofsets.
     */
    private int       offsetCount;
    /**
     * Total count of objects.
     */
    private int       objectCount;
    /**
     * Offset in file of top level offset in offset table.
     */
    private int       topLevelOffset;

    /**
     * Object table. We gradually fill in objects from the binary PList object table into this list.
     */
    private ArrayList objectTable;


    /**
     * Creates a new instance.
     */
    public BinaryPListParser() {
    }

    private byte[] _readBuffer(long size, InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte buffer[] = new byte[(int)size];
        while (is.available() > 0) {
            int lread = is.read(buffer);
            if (lread != -1) {
                baos.write(buffer, 0, lread);
            }
        }
        baos.close();
        return baos.toByteArray();
    }
    
    private int _readInt(byte buffer[], int pos) {
        int a0 = (0x00FF & buffer[pos + 0]) << 24;
        int a1 = (0x00FF & buffer[pos + 1]) << 16;
        int a2 = (0x00FF & buffer[pos + 2]) << 8;
        int a3 = (0x00FF & buffer[pos + 3]);
        int z = a0 + a1 + a2 + a3;
        return z;
    }
    
    private long _readLong(byte buffer[], int pos) {
        long a0 = (0x000000FF & buffer[pos + 0]) << 56;
        long a1 = (0x000000FF & buffer[pos + 1]) << 48;
        long a2 = (0x000000FF & buffer[pos + 2]) << 40;
        long a3 = (0x000000FF & buffer[pos + 3]) << 32;
        long a4 = (0x000000FF & buffer[pos + 4]) << 24;
        long a5 = (0x000000FF & buffer[pos + 5]) << 16;
        long a6 = (0x000000FF & buffer[pos + 6]) << 8;
        long a7 = (0x000000FF & buffer[pos + 7]);
        long z = a0 + a1 + a2 + a3 + a4 + a5 + a6 + a7;
        return z;
    }
    
    public T_PLDict parsePList(byte buffer[]) throws Exception {
        
        int size = buffer.length;
        
        // Parse the HEADER
        // ----------------
        // magic number ("bplist")
        // file format version ("00")
        int bpli = _readInt(buffer,0);
        int st00 = _readInt(buffer,4);
        if (bpli != 0x62706c69 || st00 != 0x73743030) {
            throw new IOException("parseHeader: File does not start with 'bplist00' magic.");
        }

        // Parse the TRAILER
        // ----------------
        // byte size of offset ints in offset table
        // byte size of object refs in arrays and dicts
        // number of offsets in offset table (also is number of objects)
        // element # in offset table which is top level object

        // count of offset ints in offset table
        offsetCount = (int)_readLong(buffer,size-32);
        
        // count of object refs in arrays and dicts
        refCount = (int)_readLong(buffer,size-24);
        
        // count of offsets in offset table (also is number of objects)
        objectCount = (int)_readLong(buffer,size-16);
        
        // element # in offset table which is top level object
        topLevelOffset = (int)_readLong(buffer,size-8);
        
        int bufSize = topLevelOffset - 8;

        // Parse the OBJECT TABLE
        // ----------------------
        objectTable = new ArrayList();
        DataInputStream in = null;
        try {
            in = new DataInputStream(new ByteArrayInputStream(buffer,8,bufSize));
            parseObjectTable(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }

        // Convert the object table to XML and return it
        return (T_PLDict)objectTable.get(0);
    }

    /**
     * Object Formats (marker byte followed by additional info in some cases) null 0000 0000 bool 0000 1000 // false bool 0000 1001 // true fill 0000 1111 // fill byte int 0001 nnnn ... // # of bytes
     * is 2^nnnn, big-endian bytes real 0010 nnnn ... // # of bytes is 2^nnnn, big-endian bytes date 0011 0011 ... // 8 byte float follows, big-endian bytes data 0100 nnnn [int] ... // nnnn is number
     * of bytes unless 1111 then int count follows, followed by bytes string 0101 nnnn [int] ... // ASCII string, nnnn is # of chars, else 1111 then int count, then bytes string 0110 nnnn [int] ... //
     * Unicode string, nnnn is # of chars, else 1111 then int count, then big-endian 2-byte shorts 0111 xxxx // unused uid 1000 nnnn ... // nnnn+1 is # of bytes 1001 xxxx // unused array 1010 nnnn
     * [int] objref* // nnnn is count, unless '1111', then int count follows 1011 xxxx // unused 1100 xxxx // unused dict 1101 nnnn [int] keyref* objref* // nnnn is count, unless '1111', then int
     * count follows 1110 xxxx // unused 1111 xxxx // unused
     */
    private void parseObjectTable(DataInputStream in) throws IOException {
        int marker;
        while ((marker = in.read()) != -1) {
            // System.out.println("parseObjectTable "+objectTable.size()+": marker="+Integer.toHexString(marker));
            switch ((marker & 0xf0) >> 4) {
                case 0: {
                    parsePrimitive(in, marker & 0xf);
                    break;
                }
                case 1: {
                    int count = 1 << (marker & 0xf);
                    parseInt(in, count);
                    break;
                }
                case 2: {
                    int count = 1 << (marker & 0xf);
                    parseReal(in, count);
                    break;
                }
                case 3: {
                    if ((marker & 0xf) != 3) {
                        throw new IOException("parseObjectTable: illegal marker " + Integer.toBinaryString(marker));
                    }
                    parseDate(in);
                    break;
                }
                case 4: {
                    int count = marker & 0xf;
                    if (count == 15) {
                        count = readCount(in);
                    }
                    parseData(in, count);
                    break;
                }
                case 5: {
                    int count = marker & 0xf;
                    if (count == 15) {
                        count = readCount(in);
                    }
                    parseAsciiString(in, count);
                    break;
                }
                case 6: {
                    int count = marker & 0xf;
                    if (count == 15) {
                        count = readCount(in);
                    }
                    parseUnicodeString(in, count);
                    break;
                }
                case 7: {
                    System.out.println("parseObjectTable: illegal marker " + Integer.toBinaryString(marker));
                    return;
                    // throw new IOException("parseObjectTable: illegal marker "+Integer.toBinaryString(marker));
                    // break;
                }
                case 8: {
                    int count = (marker & 0xf) + 1;
                    System.out.println("uid " + count);
                    break;
                }
                case 9: {
                    throw new IOException("parseObjectTable: illegal marker " + Integer.toBinaryString(marker));
                    // break;
                }
                case 10: {
                    int count = marker & 0xf;
                    if (count == 15) {
                        count = readCount(in);
                    }
                    if (refCount > 255) {
                        parseShortArray(in, count);
                    } else {
                        parseByteArray(in, count);
                    }
                    break;
                }
                case 11: {
                    throw new IOException("parseObjectTable: illegal marker " + Integer.toBinaryString(marker));
                    // break;
                }
                case 12: {
                    throw new IOException("parseObjectTable: illegal marker " + Integer.toBinaryString(marker));
                    // break;
                }
                case 13: {
                    int count = marker & 0xf;
                    if (count == 15) {
                        count = readCount(in);
                    }
                    if (refCount > 256) {
                        parseShortDict(in, count);
                    } else {
                        parseByteDict(in, count);
                    }
                    break;
                }
                case 14: {
                    throw new IOException("parseObjectTable: illegal marker " + Integer.toBinaryString(marker));
                    // break;
                }
                case 15: {
                    throw new IOException("parseObjectTable: illegal marker " + Integer.toBinaryString(marker));
                    // break;
                }
            }
            // System.out.println(objectTable.get(objectTable.size() - 1));
        }
    }

    /**
     * Reads a count value from the object table. Count values are encoded using the following scheme:
     * <p/>
     * int 0001 nnnn ... // # of bytes is 2^nnnn, big-endian bytes
     */
    private int readCount(DataInputStream in) throws IOException {
        int marker = in.read();
        if (marker == -1) {
            throw new IOException("variableLengthInt: Illegal EOF in marker");
        }
        if (((marker & 0xf0) >> 4) != 1) {
            throw new IOException("variableLengthInt: Illegal marker " + Integer.toBinaryString(marker));
        }
        int count = 1 << (marker & 0xf);
        int value = 0;
        for (int i = 0; i < count; i++) {
            int b = in.read();
            if (b == -1) {
                throw new IOException("variableLengthInt: Illegal EOF in value");
            }
            value = (value << 8) | b;
        }
        return value;
    }

    /**
     * null 0000 0000 bool 0000 1000 // false bool 0000 1001 // true fill 0000 1111 // fill byte
     */
    private void parsePrimitive(DataInputStream in, int primitive) throws IOException {
        switch (primitive) {
            case 0:
                objectTable.add(null);
                break;
            case 8:
                objectTable.add(Boolean.FALSE);
                break;
            case 9:
                objectTable.add(Boolean.TRUE);
                break;
            case 15:
                // fill byte: don't add to object table
                break;
            default:
                throw new IOException("parsePrimitive: illegal primitive " + Integer.toBinaryString(primitive));
        }
    }

    /**
     * array 1010 nnnn [int] objref* // nnnn is count, unless '1111', then int count follows
     */
    private void parseByteArray(DataInputStream in, int count) throws IOException {
        T_PLArray arr = new T_PLArray();
        arr.objectTable = objectTable;
        arr.objref = new int[count];

        for (int i = 0; i < count; i++) {
            arr.objref[i] = in.readByte() & 0xff;
            if (arr.objref[i] == -1) {
                throw new IOException("parseByteArray: illegal EOF in objref*");
            }
        }

        objectTable.add(arr);
    }

    /**
     * array 1010 nnnn [int] objref* // nnnn is count, unless '1111', then int count follows
     */
    private void parseShortArray(DataInputStream in, int count) throws IOException {
        T_PLArray arr = new T_PLArray();
        arr.objectTable = objectTable;
        arr.objref = new int[count];

        for (int i = 0; i < count; i++) {
            arr.objref[i] = in.readShort() & 0xffff;
            if (arr.objref[i] == -1) {
                throw new IOException("parseShortArray: illegal EOF in objref*");
            }
        }

        objectTable.add(arr);
    }

    /*
     * data 0100 nnnn [int] ... // nnnn is number of bytes unless 1111 then int count follows, followed by bytes
     */
    private void parseData(DataInputStream in, int count) throws IOException {
        byte[] data = new byte[count];
        in.readFully(data);
        objectTable.add(data);
    }

    /**
     * byte dict 1101 nnnn keyref* objref* // nnnn is less than '1111'
     */
    private void parseByteDict(DataInputStream in, int count) throws IOException {
        T_PLDict dict = new T_PLDict();
        dict.objectTable = objectTable;
        dict.keyref = new int[count];
        dict.objref = new int[count];

        for (int i = 0; i < count; i++) {
            dict.keyref[i] = in.readByte() & 0xff;
        }
        for (int i = 0; i < count; i++) {
            dict.objref[i] = in.readByte() & 0xff;
        }
        objectTable.add(dict);
    }

    /**
     * short dict 1101 ffff int keyref* objref* // int is count
     */
    private void parseShortDict(DataInputStream in, int count) throws IOException {
        T_PLDict dict = new T_PLDict();
        dict.objectTable = objectTable;
        dict.keyref = new int[count];
        dict.objref = new int[count];

        for (int i = 0; i < count; i++) {
            dict.keyref[i] = in.readShort() & 0xffff;
        }
        for (int i = 0; i < count; i++) {
            dict.objref[i] = in.readShort() & 0xffff;
        }
        objectTable.add(dict);
    }

    /**
     * string 0101 nnnn [int] ... // ASCII string, nnnn is # of chars, else 1111 then int count, then bytes
     */
    private void parseAsciiString(DataInputStream in, int count) throws IOException {
        byte[] buf = new byte[count];
        in.readFully(buf);
        String str = new String(buf, "ASCII");
        objectTable.add(str);
    }

    /**
     * int 0001 nnnn ... // # of bytes is 2^nnnn, big-endian bytes
     */
    private void parseInt(DataInputStream in, int count) throws IOException {
        if (count > 8) {
            throw new IOException("parseInt: unsupported byte count:" + count);
        }
        long value = 0;
        for (int i = 0; i < count; i++) {
            int b = in.read();
            if (b == -1) {
                throw new IOException("parseInt: Illegal EOF in value");
            }
            value = (value << 8) | b;
        }
        objectTable.add(value);
    }

    /**
     * real 0010 nnnn ... // # of bytes is 2^nnnn, big-endian bytes
     */
    private void parseReal(DataInputStream in, int count) throws IOException {
        switch (count) {
            case 4:
                objectTable.add(in.readFloat());
                break;
            case 8:
                objectTable.add(in.readDouble());
                break;
            default:
                throw new IOException("parseReal: unsupported byte count:" + count);
        }
    }

    /**
     * date 0011 0011 ... // 8 byte float follows, big-endian bytes
     */
    private void parseDate(DataInputStream in) throws IOException {
        // XXX - This does not yield a date :(
        double date = in.readDouble();
        // objectTable.add(new Date((long) date));
        objectTable.add(new Date());
    }

    /**
     * string 0110 nnnn [int] ... // Unicode string, nnnn is # of chars, else 1111 then int count, then big-endian 2-byte shorts
     */
    private void parseUnicodeString(DataInputStream in, int count) throws IOException {
        char[] buf = new char[count];
        for (int i = 0; i < count; i++) {
            buf[i] = in.readChar();
        }
        String str = new String(buf);
        objectTable.add(str);
    }

}
