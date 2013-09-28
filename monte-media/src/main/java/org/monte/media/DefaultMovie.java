/*
 * @(#)DefaultMovie.java  1.0  2011-09-01
 * 
 * Copyright (c) 2011 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 * 
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */
package org.monte.media;

import java.io.IOException;
import org.monte.media.math.Rational;

/**
 * {@code DefaultMovie}.
 *
 * @author Werner Randelshofer
 * @version 1.0 2011-09-01 Created.
 */
public class DefaultMovie extends AbstractMovie {
    private MovieReader reader;
    private Rational playhead;
    private Rational in;
    private Rational out;

    public MovieReader getReader() {
        return reader;
    }

    public void setReader(MovieReader reader) {
        this.reader = reader;
        try {
            this.out=reader.getDuration();
        } catch (IOException ex) {
            InternalError ie= new InternalError("Can't read duration.");
            ie.initCause(ex);
            throw ie;
        }
        this.playhead=new Rational(0,1);
        this.in=new Rational(0,1);
    }

    
    
    public Rational getDuration() {
        try {
        return reader.getDuration();
        } catch (IOException ex) {
            InternalError ie= new InternalError("Can't read duration.");
            ie.initCause(ex);
            throw ie;
        }
    }

    
    public Rational getInsertionPoint() {
        return playhead;
    }

    public void setInsertionPoint(Rational newValue) {
        Rational oldValue=this.playhead;
        this.playhead = newValue;
        firePropertyChange(PLAYHEAD_PROPERTY, oldValue, newValue);
    }

    public Rational getSelectionStart() {
        return in;
    }

    public void setSelectionStart(Rational newValue) {
        Rational oldValue=in;
        this.in = newValue;
        firePropertyChange(IN_PROPERTY, oldValue, newValue);
    }

    public Rational getSelectionEnd() {
        return out;
    }

    public void setSelectionEnd(Rational newValue) {
        Rational oldValue=out;
        this.out = newValue;
        firePropertyChange(OUT_PROPERTY, oldValue, newValue);
    }



    public long timeToSample(int track, Rational time) {
        try {
            return reader.timeToSample(track, time);
        } catch (IOException ex) {
            return 0;
        }
    }

    public Rational sampleToTime(int track, long sample) {
        try {
            return reader.sampleToTime(track, sample);
        } catch (IOException ex) {
            return new Rational(0);
        }
    }

    public int getTrackCount() {
        try {
        return reader.getTrackCount();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Format getFormat(int track) {
        try {
            return reader.getFormat(track);
        } catch (IOException ex) {
           return null;
        }
    }

    public Format getFileFormat() {
        try {
            return reader.getFileFormat();
        } catch (IOException ex) {
            return null;
        }
    }
}
