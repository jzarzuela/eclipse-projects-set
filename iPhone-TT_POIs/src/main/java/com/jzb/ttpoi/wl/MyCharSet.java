/**
 * 
 */
package com.jzb.ttpoi.wl;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * @author n63636
 * 
 */
@SuppressWarnings("synthetic-access")
public class MyCharSet extends Charset {

    private static class Decoder extends CharsetDecoder {

        private Decoder(Charset cs) {
            super(cs, 1.0f, 1.0f);
        }

        @Override
        protected CoderResult decodeLoop(ByteBuffer src, CharBuffer dst) {
            if (src.hasArray() && dst.hasArray())
                return decodeArrayLoop(src, dst);
            else
                return decodeBufferLoop(src, dst);
        }

        private CoderResult decodeArrayLoop(ByteBuffer src, CharBuffer dst) {
            byte[] sa = src.array();
            int sp = src.arrayOffset() + src.position();
            int sl = src.arrayOffset() + src.limit();
            assert (sp <= sl);
            sp = (sp <= sl ? sp : sl);
            char[] da = dst.array();
            int dp = dst.arrayOffset() + dst.position();
            int dl = dst.arrayOffset() + dst.limit();
            assert (dp <= dl);
            dp = (dp <= dl ? dp : dl);

            try {
                while (sp < sl) {
                    byte b = sa[sp];
                    if (b >= 0) {
                        if (dp >= dl)
                            return CoderResult.OVERFLOW;
                        da[dp++] = (char) b;
                        sp++;
                        continue;
                    }
                    return CoderResult.malformedForLength(1);
                }
                return CoderResult.UNDERFLOW;
            } finally {
                src.position(sp - src.arrayOffset());
                dst.position(dp - dst.arrayOffset());
            }
        }

        private CoderResult decodeBufferLoop(ByteBuffer src, CharBuffer dst) {
            int mark = src.position();
            try {
                while (src.hasRemaining()) {
                    byte b = src.get();
                    if (b >= 0) {
                        if (!dst.hasRemaining())
                            return CoderResult.OVERFLOW;
                        dst.put((char) b);
                        mark++;
                        continue;
                    }
                    return CoderResult.malformedForLength(1);
                }
                return CoderResult.UNDERFLOW;
            } finally {
                src.position(mark);
            }
        }

    }

    private static class Encoder extends CharsetEncoder {

        private final Surrogate.Parser sgp = new Surrogate.Parser();

        private Encoder(Charset cs) {
            super(cs, 1.0f, 1.0f);
        }

        @Override
        public boolean canEncode(char c) {
            return c < 0x80;
        }

        @Override
        protected CoderResult encodeLoop(CharBuffer src, ByteBuffer dst) {
            if (src.hasArray() && dst.hasArray())
                return encodeArrayLoop(src, dst);
            else
                return encodeBufferLoop(src, dst);
        }

        private CoderResult encodeArrayLoop(CharBuffer src, ByteBuffer dst) {
            char[] sa = src.array();
            int sp = src.arrayOffset() + src.position();
            int sl = src.arrayOffset() + src.limit();
            assert (sp <= sl);
            sp = (sp <= sl ? sp : sl);
            byte[] da = dst.array();
            int dp = dst.arrayOffset() + dst.position();
            int dl = dst.arrayOffset() + dst.limit();
            assert (dp <= dl);
            dp = (dp <= dl ? dp : dl);

            try {
                while (sp < sl) {
                    char c = sa[sp];
                    if (c < 0x80) {
                        if (dp >= dl)
                            return CoderResult.OVERFLOW;
                        da[dp] = (byte) c;
                        sp++;
                        dp++;
                        continue;
                    }
                    if (sgp.parse(c, sa, sp, sl) < 0)
                        return sgp.error();
                    return sgp.unmappableResult();
                }
                return CoderResult.UNDERFLOW;
            } finally {
                src.position(sp - src.arrayOffset());
                dst.position(dp - dst.arrayOffset());
            }
        }

        private CoderResult encodeBufferLoop(CharBuffer src, ByteBuffer dst) {
            int mark = src.position();
            try {
                while (src.hasRemaining()) {
                    char c = src.get();

                    // AQUI HACE EL CAMBIO DE ALGUNOS CARACTERES
                    if (c >= 0x0FF) {
                        Character ch = s_codesTable.get((int) c);
                        if (ch == null) {
                            System.err.println("Character without encoding in 'MyCharSet.properties' (" + (int) c + "): " + c);
                        } else {
                            c = ch;
                        }
                    }

                    if (c < 0x0FF) {
                        if (!dst.hasRemaining())
                            return CoderResult.OVERFLOW;
                        dst.put((byte) c);
                        mark++;
                        continue;
                    }

                    if (sgp.parse(c, src) < 0)
                        return sgp.error();
                    return sgp.unmappableResult();
                }
                return CoderResult.UNDERFLOW;
            } finally {
                src.position(mark);
            }
        }

    }

    private static HashMap<Integer, Character> s_codesTable = new HashMap<Integer, Character>();

    static {
        initTable();
    }

    private static void initTable() {
        try {
            Properties props = new Properties();
            props.load(MyCharSet.class.getResourceAsStream("MyCharSet.properties"));
            for (Object obj : props.values()) {
                String val = (String) obj;
                StringTokenizer st = new StringTokenizer(val, ",");
                while (st.hasMoreTokens()) {
                    String code = st.nextToken().trim();
                    String chr = st.nextToken().trim();
                    Integer i = Integer.parseInt(code);
                    Character c = chr.charAt(0);
                    s_codesTable.put(i, c);
                }
            }
            
            
        } catch (Exception ex) {
            System.err.println("Error reading correlation table: MyCharSet.properties");
            ex.printStackTrace(System.err);

        }
    }

    public MyCharSet() {
        super("MyCharSet", new String[] { "MyCharSet" });
    }

    @Override
    public boolean contains(Charset cs) {
        return (cs instanceof MyCharSet);
    }

    public String historicalName() {
        return "MyCharSet";
    }

    @Override
    public CharsetDecoder newDecoder() {
        return new Decoder(this);
    }

    @Override
    public CharsetEncoder newEncoder() {
        return new Encoder(this);
    }

}
