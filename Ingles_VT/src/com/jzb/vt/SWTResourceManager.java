/**
 * 
 */
package com.jzb.vt;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;

/**
 * @author PS00A501
 * 
 */
public class SWTResourceManager {

    public SWTResourceManager() {
    }

    public static void dispose() {
        /* 35 */disposeColors();
        /* 36 */disposeFonts();
        /* 37 */disposeImages();
        /* 38 */disposeCursors();
    }

    public static Color getColor(int systemColorID) {
        /* 56 */Display display = Display.getCurrent();
        /* 57 */return display.getSystemColor(systemColorID);
    }

    public static Color getColor(int r, int g, int b) {
        /* 68 */return getColor(new RGB(r, g, b));
    }

    public static Color getColor(RGB rgb) {
        /* 77 */Color color = (Color) m_ColorMap.get(rgb);
        /* 78 */if (color == null) {
            /* 79 */Display display = Display.getCurrent();
            /* 80 */color = new Color(display, rgb);
            /* 81 */m_ColorMap.put(rgb, color);
        }
        /* 83 */return color;
    }

    public static void disposeColors() {
        /* 90 */for (Iterator iter = m_ColorMap.values().iterator(); iter.hasNext(); ((Color) iter.next()).dispose())
            ;
        /* 92 */m_ColorMap.clear();
    }

    protected static Image getImage(InputStream is) {
        /* 115 */Display display = Display.getCurrent();
        /* 116 */ImageData data = new ImageData(is);
        /* 117 */if (data.transparentPixel > 0)
            /* 118 */return new Image(display, data, data.getTransparencyMask());
        /* 119 */else
            /* 119 */return new Image(display, data);
    }

    public static Image getImage(String path) {
        /* 128 */return getImage("default", path);
    }

    public static Image getImage(String section, String path) {
        /* 138 */String key = section + '|' + SWTResourceManager.class.getName() + '|' + path;
        /* 139 */Image image = (Image) m_ClassImageMap.get(key);
        /* 140 */if (image == null)
            /* 142 */try {
                /* 142 */FileInputStream fis = new FileInputStream(path);
                /* 143 */image = getImage(((InputStream) (fis)));
                /* 144 */m_ClassImageMap.put(key, image);
                /* 145 */fis.close();
            }
            /* 146 */catch (Exception _ex) {
                /* 147 */image = getMissingImage();
                /* 148 */m_ClassImageMap.put(key, image);
            }
        /* 151 */return image;
    }

    public static Image getImage(Class clazz, String path) {
        /* 161 */String key = clazz.getName() + '|' + path;
        /* 162 */Image image = (Image) m_ClassImageMap.get(key);
        /* 163 */if (image == null)
            /* 165 */try {
                /* 165 */if (path.length() > 0 && path.charAt(0) == '/') {
                    /* 166 */String newPath = path.substring(1, path.length());
                    /* 167 */image = getImage(((InputStream) (new BufferedInputStream(clazz.getClassLoader().getResourceAsStream(newPath)))));
                } else {
                    /* 169 */image = getImage(clazz.getResourceAsStream(path));
                }
                /* 171 */m_ClassImageMap.put(key, image);
            }
            /* 172 */catch (Exception _ex) {
                /* 173 */image = getMissingImage();
                /* 174 */m_ClassImageMap.put(key, image);
            }
        /* 177 */return image;
    }

    private static Image getMissingImage() {
        /* 182 */Image image = new Image(Display.getCurrent(), 10, 10);
        /* 184 */GC gc = new GC(image);
        /* 185 */gc.setBackground(getColor(3));
        /* 186 */gc.fillRectangle(0, 0, 10, 10);
        /* 187 */gc.dispose();
        /* 189 */return image;
    }

    public static Image decorateImage(Image baseImage, Image decorator) {
        /* 216 */return decorateImage(baseImage, decorator, 4);
    }

    public static Image decorateImage(Image baseImage, Image decorator, int corner) {
        /* 227 */HashMap decoratedMap = (HashMap) m_ImageToDecoratorMap.get(baseImage);
        /* 228 */if (decoratedMap == null) {
            /* 229 */decoratedMap = new HashMap();
            /* 230 */m_ImageToDecoratorMap.put(baseImage, decoratedMap);
        }
        /* 232 */Image result = (Image) decoratedMap.get(decorator);
        /* 233 */if (result == null) {
            /* 234 */Rectangle bid = baseImage.getBounds();
            /* 235 */Rectangle did = decorator.getBounds();
            /* 236 */result = new Image(Display.getCurrent(), bid.width, bid.height);
            /* 237 */GC gc = new GC(result);
            /* 238 */gc.drawImage(baseImage, 0, 0);
            /* 240 */if (corner == 1)
                /* 241 */gc.drawImage(decorator, 0, 0);
            /* 242 */else
            /* 242 */if (corner == 2)
                /* 243 */gc.drawImage(decorator, bid.width - did.width - 1, 0);
            /* 244 */else
            /* 244 */if (corner == 3)
                /* 245 */gc.drawImage(decorator, 0, bid.height - did.height - 1);
            /* 246 */else
            /* 246 */if (corner == 4)
                /* 247 */gc.drawImage(decorator, bid.width - did.width - 1, bid.height - did.height - 1);
            /* 250 */gc.dispose();
            /* 251 */decoratedMap.put(decorator, result);
        }
        /* 253 */return result;
    }

    public static void disposeImages() {
        /* 260 */for (Iterator I = m_ClassImageMap.values().iterator(); I.hasNext(); ((Image) I.next()).dispose())
            ;
        /* 262 */m_ClassImageMap.clear();
        /* 264 */for (Iterator I = m_ImageToDecoratorMap.values().iterator(); I.hasNext();) {
            /* 265 */HashMap decoratedMap = (HashMap) I.next();
            Image image;
            /* 266 */for (Iterator J = decoratedMap.values().iterator(); J.hasNext(); image.dispose())
                /* 267 */image = (Image) J.next();

        }

    }

    public static void disposeImages(String section) {
        /* 278 */for (Iterator I = m_ClassImageMap.keySet().iterator(); I.hasNext();) {
            /* 279 */String key = (String) I.next();
            /* 280 */if (key.startsWith(section + '|')) {
                /* 282 */Image image = (Image) m_ClassImageMap.get(key);
                /* 283 */image.dispose();
                /* 284 */I.remove();
            }
        }

    }

    public static Font getFont(String name, int height, int style) {
        /* 310 */return getFont(name, height, style, false, false);
    }

    public static Font getFont(String name, int size, int style, boolean strikeout, boolean underline) {
        /* 325 */String fontName = name + '|' + size + '|' + style + '|' + strikeout + '|' + underline;
        /* 326 */Font font = (Font) m_FontMap.get(fontName);
        /* 327 */if (font == null) {
            /* 328 */FontData fontData = new FontData(name, size, style);
            /* 329 */if (strikeout || underline)
                /* 331 */try {
                    /* 331 */Class logFontClass = Class.forName("org.eclipse.swt.internal.win32.LOGFONT");
                    /* 332 */Object logFont = org.eclipse.swt.graphics.FontData.class.getField("data").get(fontData);
                    /* 333 */if (logFont != null && logFontClass != null) {
                        /* 334 */if (strikeout)
                            /* 335 */logFontClass.getField("lfStrikeOut").set(logFont, new Byte((byte) 1));
                        /* 337 */if (underline)
                            /* 338 */logFontClass.getField("lfUnderline").set(logFont, new Byte((byte) 1));
                    }
                }
                /* 341 */catch (Throwable e) {
                    /* 342 */System.err.println("Unable to set underline or strikeout (probably on a non-Windows platform). " + e);
                }
            /* 346 */font = new Font(Display.getCurrent(), fontData);
            /* 347 */m_FontMap.put(fontName, font);
        }
        /* 349 */return font;
    }

    public static Font getBoldFont(Font baseFont) {
        /* 359 */Font font = (Font) m_FontToBoldFontMap.get(baseFont);
        /* 360 */if (font == null) {
            /* 361 */FontData fontDatas[] = baseFont.getFontData();
            /* 362 */FontData data = fontDatas[0];
            /* 363 */font = new Font(Display.getCurrent(), data.getName(), data.getHeight(), 1);
            /* 364 */m_FontToBoldFontMap.put(baseFont, font);
        }
        /* 366 */return font;
    }

    public static void disposeFonts() {
        /* 373 */for (Iterator iter = m_FontMap.values().iterator(); iter.hasNext(); ((Font) iter.next()).dispose())
            ;
        /* 375 */m_FontMap.clear();
    }

    public static void fixCoolBarSize(CoolBar bar) {
        /* 387 */CoolItem items[] = bar.getItems();
        /* 389 */for (int i = 0; i < items.length; i++) {
            /* 390 */CoolItem item = items[i];
            /* 391 */if (item.getControl() == null)
                /* 392 */item.setControl(new Canvas(bar, 0) {

                    public Point computeSize(int wHint, int hHint, boolean changed) {
                        /* 394 */return new Point(20, 20);
                    }

                });
        }

        /* 399 */for (int i = 0; i < items.length; i++) {
            /* 400 */CoolItem item = items[i];
            /* 401 */Control control = item.getControl();
            /* 402 */control.pack();
            /* 403 */Point size = control.getSize();
            /* 404 */item.setSize(item.computeSize(size.x, size.y));
        }

    }

    public static Cursor getCursor(int id) {
        /* 423 */Integer key = new Integer(id);
        /* 424 */Cursor cursor = (Cursor) m_IdToCursorMap.get(key);
        /* 425 */if (cursor == null) {
            /* 426 */cursor = new Cursor(Display.getDefault(), id);
            /* 427 */m_IdToCursorMap.put(key, cursor);
        }
        /* 429 */return cursor;
    }

    public static void disposeCursors() {
        /* 436 */for (Iterator iter = m_IdToCursorMap.values().iterator(); iter.hasNext(); ((Cursor) iter.next()).dispose())
            ;
        /* 438 */m_IdToCursorMap.clear();
    }

    private static HashMap   m_ColorMap            = new HashMap();
    private static HashMap   m_ClassImageMap       = new HashMap();
    private static HashMap   m_ImageToDecoratorMap = new HashMap();
    //private static final int MISSING_IMAGE_SIZE    = 10;
    public static final int  TOP_LEFT              = 1;
    public static final int  TOP_RIGHT             = 2;
    public static final int  BOTTOM_LEFT           = 3;
    public static final int  BOTTOM_RIGHT          = 4;
    private static HashMap   m_FontMap             = new HashMap();
    private static HashMap   m_FontToBoldFontMap   = new HashMap();
    private static HashMap   m_IdToCursorMap       = new HashMap();

}
