package com.lynp.ui.db;

/**
 * Created by niuminguo on 16/3/30.
 */
public class Convert {

    public static int getInt(byte[] b, int startIndex) {
        int ch1 = (b[3 + startIndex]) & 0xff;
        int ch2 = (b[2 + startIndex]) & 0xff;
        int ch3 = (b[1 + startIndex]) & 0xff;
        int ch4 = (b[0 + startIndex]) & 0xff;
        int r = (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
        return r;
    }

    public static short getShort(byte b[], int startIndex) {
        int ch3 = (b[startIndex + 1]) & 0xff;
        int ch4 = (b[startIndex + 0]) & 0xff;
        short r = (short) ((ch3 << 8) + (ch4 << 0));
        return r;
    }

    public static int getUShort(byte b[], int startIndex) {
        int ch3 = (b[startIndex + 1]) & 0xff;
        int ch4 = (b[startIndex + 0]) & 0xff;
        int r = ((ch3 << 8) + (ch4 << 0));
        return r;
    }

    public static boolean getBit(byte b, int index) {
        int d = (int) b;
        d = d << (32 - index);
        d = d >>> (32 - index);
        d = d >>> (index - 1);
        return d > 0;
    }

    public static byte[] copyString(byte[] b, int index, int length) {
        byte[] temp = new byte[length];
        System.arraycopy(b, index, temp, 0, length);
        return temp;
    }

    public static byte[] covertBytes(byte b) {
        byte[] bs = new byte[1];
        bs[0] = b;
        return bs;
    }

    public static byte[] convertInt(int parValue) {
        byte[] ret = new byte[4];
        ret[0] = (byte) (parValue & 0x000000FF);
        ret[1] = (byte) ((parValue >> 8) & 0x000000FF);
        ret[2] = (byte) ((parValue >> 16) & 0x000000FF);
        ret[3] = (byte) ((parValue >> 24) & 0x000000FF);
        return ret;
    }

    public static int getNum(byte b, int startIndex, int endIndex) {
        int d = (int) b;

        d = d << (32 - endIndex - 1);
        d = d >>> (32 - endIndex - 1);
        d = d >>> (startIndex);
        return d;
    }

    public static int getNum(short b, int startIndex, int endIndex) {
        int d = (int) b;
        d = d << (32 - endIndex);
        d = d >>> (32 - endIndex);
        d = d >>> (startIndex - 1);
        return d;
    }

    public static byte[] convertShort(int parValue) {
        byte[] ret = new byte[2];
        ret[0] = (byte) (parValue & 0x000000FF);
        ret[1] = (byte) ((parValue >> 8) & 0x000000FF);
        return ret;
    }

    public static void writeInt(byte[] src, int index, int parValue) {
        byte[] intData = convertInt(parValue);
        System.arraycopy(intData, 0, src, index, 4);
    }

    public static void writeShort(byte[] src, int index, short parValue) {
        byte[] shortData = convertShort(parValue);
        System.arraycopy(shortData, 0, src, index, 2);
    }

    public static void moveArray(byte[] src, int offsrc, byte[] dest,
                                 int offdest, int size) {
        byte[] tBytes = new byte[size];
        System.arraycopy(src, offsrc, tBytes, 0, size);
        System.arraycopy(tBytes, 0, dest, offdest, size);
    }

    public static String getString(byte b[], int index, int size) {
        try {
            return new String(b, index, size, "UTF-8");
        } catch (Exception ex) {
            return "";
        }
    }

    public static byte[] getSubBytes(byte b[], int index, int size) {
        byte[] bs = new byte[size];
        System.arraycopy(b, index, bs, 0, size);
        return bs;
    }

    public static byte[] get1BString(String s) {
        try {
            java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();

            byte[] b = s.getBytes("UTF-8");
            byte[] b0 = new byte[1];
            b0[0] = (byte) b.length;
            bout.write(b0);
            bout.write(b);
            return bout.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
            return new byte[1];
        }
    }

    public static byte[] get2BString(String s) {
        try {
            java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();

            byte[] b = s.getBytes("UTF-8");
            bout.write(Convert.convertShort(b.length));
            bout.write(b);
            return bout.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
            return new byte[1];
        }
    }

    public static double convertDouble(byte b[], int startIndex) {

        long l = 0;
        for (int i = 0; i < 8; i++) {
            l += ((long) (b[i + startIndex] & 0xff)) << (8 * i);
        }
        return Double.longBitsToDouble(l);

    }

    public static byte[] getDouble(double parValue) {

        byte[] buffer = new byte[8];
        long lValue = Double.doubleToLongBits(parValue);
        String s = Long.toHexString(lValue);

        String tmpStr;
        for (int i = 0; i < 8; i++) {
            tmpStr = s.substring(2 * i, 2 * i + 2);
            buffer[7 - i] = (byte) Integer.parseInt(tmpStr, 16);
        }
        return buffer;
    }

    public static void convert1bString(byte[] data, int offset, ConvertString cs) {
        try {
            cs.byteLength = data[offset];
            cs.value = new String(data, offset + 1, cs.byteLength, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            cs.byteLength = 0;
            cs.value = "";
        }
    }

    public static void convert2bString(byte[] data, int offset, ConvertString cs) {
        try {
            cs.byteLength = Convert.getShort(data, offset);
            cs.value = new String(data, offset + 2, cs.byteLength, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            cs.byteLength = 0;
            cs.value = "";
        }
    }

    /**
     *
     * @param byte[]
     * @return HexString
     */
    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }


    public static long byteToLong(byte[] b) {
        long s = 0;
        long s0 = b[0] & 0xff;// ???�?�?
        long s1 = b[1] & 0xff;
        long s2 = b[2] & 0xff;
        long s3 = b[3] & 0xff;
        long s4 = b[4] & 0xff;// ???�?�?
        long s5 = b[5] & 0xff;
        long s6 = b[6] & 0xff;
        long s7 = b[7] & 0xff; // s0�????
        s1 <<= 8;
        s2 <<= 16;
        s3 <<= 24;
        s4 <<= 8 * 4;
        s5 <<= 8 * 5;
        s6 <<= 8 * 6;
        s7 <<= 8 * 7;
        s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
        return s;
    }
}