package me.jddev0.ep.util;

public final class ByteUtils {
    private ByteUtils() {}

    public static short get2Bytes(int value, int index) {
        return (short)((value >>> (16 * index)) & 0xFFFF);
    }

    public static short get2Bytes(long value, int index) {
        return (short)((value >>> (16 * index)) & 0xFFFF);
    }

    public static int with2Bytes(int value, short c, int index) {
        return value & ~(0xFFFF << (16 * index)) | ((c & 0xFFFF) << (16 * index));
    }

    public static long with2Bytes(long value, short c, int index) {
        return value & ~(((long)0xFFFF) << (16 * index)) | (((long)c & 0xFFFF) << (16 * index));
    }

    public static int from2ByteChunks(short c1, short c2) {
        return ((c2 & 0xFFFF) << 16) | (c1 & 0xFFFF);
    }

    public static long from2ByteChunks(short c1, short c2, short c3, short c4) {
        return (((long)c4 & 0xFFFF) << (3 * 16)) | (((long)c3 & 0xFFFF) << (2 * 16)) | (((long)c2 & 0xFFFF) << 16) | ((long)c1 & 0xFFFF);
    }
}
