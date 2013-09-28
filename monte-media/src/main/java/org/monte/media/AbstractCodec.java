/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.monte.media;

import java.util.ArrayList;
/**
 * {@code AbstractCodec}.
 *
 * @author Werner Randelshofer
 * @version 1.0 2011-03-12 Created.
 */
public abstract class AbstractCodec implements Codec {

    protected Format[] inputFormats;
    protected Format[] outputFormats;
    protected Format inputFormat;
    protected Format outputFormat;
    protected String name="unnamed codec";

    public AbstractCodec(Format[] supportedInputFormats, Format[] supportedOutputFormats) {
        this.inputFormats = supportedInputFormats;
        this.outputFormats = supportedOutputFormats;
    }
    public AbstractCodec(Format[] supportedInputOutputFormats) {
        this.inputFormats = supportedInputOutputFormats;
        this.outputFormats = supportedInputOutputFormats;
    }

    public Format[] getInputFormats() {
        return inputFormats.clone();
    }

    public Format[] getOutputFormats(Format input) {
        ArrayList<Format>of=new ArrayList<Format>(outputFormats.length);
        for (Format f:outputFormats) {
            of.add(input==null?f:f.append(input));
        }
        return of.toArray(new Format[of.size()]);
    }

    public Format setInputFormat(Format f) {
        if (f!=null)
        for (Format sf : getInputFormats()) {
            if (sf.matches(f)) {
                this.inputFormat = sf.append(f);
                return inputFormat;
            }
        }
        this.inputFormat=null;
        return null;
    }

    public Format setOutputFormat(Format f) {
        for (Format sf : getOutputFormats(f)) {
            if (sf.matches(f)) {
                this.outputFormat = f;
                return sf;
            }
        }
        this.outputFormat=null;
        return null;
    }

    public Format getInputFormat() {
        return inputFormat;
    }

    public Format getOutputFormat() {
        return outputFormat;
    }

    public String getName() {
        return name;
    }

    /** Empty implementation of the reset method. Don't call super. */
    public void reset() {
        // empty
    }

    
    public String toString() {
        String className=getClass().getName();
        int p=className.lastIndexOf('.');
        return className.substring(p+1)+"{" + "inputFormat=" + inputFormat + ", outputFormat=" + outputFormat+'}';
    }
    
    
}
