/**
 * 
 */
package com.jzb.dxc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author n63636
 * 
 */
public class GenericFilterRule implements IFilterRule {

    public static enum MODE {
        REMOVE, REPLACE, CUTRIGHT, SERIE_NAME, PREFIX, CHAPTER
    };

    private Pattern      m_pattern;
    private MODE         m_mode;
    private String       m_data;
    public static String lastSerieNameMatched = null;

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "GenericFilterRule - Mode: '" + m_mode + "', Pattern: '" + m_pattern + "', Data:'" + m_data + "'";
    }

    public static final MODE getMode(String mode) throws Exception {
        mode = mode.toLowerCase();
        if (mode.equals("remove"))
            return MODE.REMOVE;
        else if (mode.equals("replace"))
            return MODE.REPLACE;
        else if (mode.equals("cutright"))
            return MODE.CUTRIGHT;
        else if (mode.equals("serie_name"))
            return MODE.SERIE_NAME;
        else if (mode.equals("prefix"))
            return MODE.PREFIX;
        else if (mode.equals("chapter"))
            return MODE.CHAPTER;
        throw new Exception("Filter mode unknonw: " + mode);
    }

    public GenericFilterRule(MODE mode, String regexp, String data) {
        m_pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        m_mode = mode;
        m_data = data;
    }

    public String filter(String name) throws Exception {
        switch (m_mode) {
            case REMOVE:
                return filter_remove(name);
            case REPLACE:
                return filter_replace(name);
            case CUTRIGHT:
                return filter_cutRight(name);
            case SERIE_NAME:
                return filter_serieName(name);
            case PREFIX:
                return filter_prefix(name);
            case CHAPTER:
                return filter_Chapter(name);
            default:
                return name;
        }
    }

    private String filter_Chapter(String name) throws Exception {
        Matcher mc = m_pattern.matcher(name);

        if (!mc.find()) {
            return name;
        } else {
            String prefix = calcPrefix(name.substring(mc.start(), mc.end()));
            String s = prefix + name.substring(0, mc.start()) + name.substring(mc.end());
            return s;
        }
    }

    private String filter_serieName(String name) throws Exception {

        Matcher mc = m_pattern.matcher(name);

        if (mc.find()) {
            if (name.charAt(4) != '-') {
                throw new Exception("File matched a SERIE's name but no CHAPTER was found");
            } else {
                lastSerieNameMatched = this.m_data;
                return name.substring(0, 5) + m_data;
            }
        } else {
            return name;
        }

    }

    private String filter_prefix(String name) throws Exception {

        Matcher mc = m_pattern.matcher(name);

        if (!mc.find()) {
            return name;
        } else {
            int p1 = mc.start();
            int p2 = mc.end();
            String sl = "";
            String sr = "";
            String sb = name.substring(p1, p2);
            if (sb.indexOf('(') >= 0) {
                sl = sb.substring(0, sb.indexOf('(') + 1);
            }
            if (sb.lastIndexOf(')') >= 0) {
                sr = sb.substring(sb.lastIndexOf(')'));
            }
            String s = m_data + name.substring(0, p1) + sl + sr + name.substring(p2);
            return s;
        }
    }

    private String filter_cutRight(String name) throws Exception {

        Matcher mc = m_pattern.matcher(name);

        if (mc.find())
            return name.substring(0, mc.start());
        else
            return name;

    }

    private String filter_replace(String name) throws Exception {

        String s = "";
        int p1 = 0;

        Matcher mc = m_pattern.matcher(name);

        while (mc.find()) {
            s += name.substring(p1, mc.start());
            s += m_data;
            p1 = mc.end();
        }
        s += name.substring(p1);

        return s;
    }

    private String filter_remove(String name) throws Exception {

        String s = "";
        int p1 = 0;

        Matcher mc = m_pattern.matcher(name);

        while (mc.find()) {
            s += name.substring(p1, mc.start());
            p1 = mc.end();
        }
        s += name.substring(p1);

        return s;
    }

    private String calcPrefix(String s) {

        int p1 = 0, p2;
        String season, chapter;

        while (p1 < s.length() && !Character.isDigit(s.charAt(p1)))
            p1++;
        p2 = p1;
        while (p2 < s.length() && Character.isDigit(s.charAt(p2)))
            p2++;
        season = s.substring(p1, p2);

        p1 = p2;
        while (p1 < s.length() && !Character.isDigit(s.charAt(p1)))
            p1++;
        p2 = p1;
        while (p2 < s.length() && Character.isDigit(s.charAt(p2)))
            p2++;
        chapter = s.substring(p1, p2);

        if (chapter.length() == 0) {
            int p = season.length() - 2;
            chapter = season.substring(p);
            season = season.substring(0, p);
        }

        if (season.startsWith("0"))
            season = season.substring(1);

        return season + "x" + chapter + "-";

    }
}
