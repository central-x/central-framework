/*
 * MIT License
 *
 * Copyright (c) 2022-present Alan Yeh <alan@yeh.cn>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package central.util;

import central.lang.Assertx;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Universally Unique Lexicographically Sortable Identifier
 *
 * <pre>
 * 0                   1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                      32_bit_uint_time_high                    |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |     16_bit_uint_time_low      |       16_bit_uint_random      |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                       32_bit_uint_random                      |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                       32_bit_uint_random                      |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 * <p>
 * Usage:
 * <pre>{@code
 * var ulid = ULID.randomULID();
 * var value = ulid.toString();
 * var nextVal = ulid.increase();
 * }</pre>
 *
 * @author Alan Yeh
 * @since 2023/06/04
 */
public final class ULID implements Serializable, Comparable<ULID> {

    @Serial
    private static final long serialVersionUID = -2374524733859089730L;

    /**
     * Create a new ULID
     */
    public static ULID randomULID() {
        long timestamp = System.currentTimeMillis();

        byte[] random = new byte[RANDOMNESS_BYTE_LEN];
        ThreadLocalRandom.current().nextBytes(random);

        return new ULID(timestamp, random);
    }

    /**
     * Timestamp component mask
     */
    private static final long TIMESTAMP_MASK = 0xffff000000000000L;

    /**
     * Timestamp overflow flag, the 1st char of the input string must be between 0 and 7
     */
    private static final byte TIMESTAMP_OVERFLOW_FLAG = 0b11000;

    /**
     * The least significant 64 bits increase overflow, 0xffffffffffffffffL + 1
     */
    private static final long OVERFLOW = 0x0000000000000000L;
    /**
     * The length of randomness component of ULID
     */
    public static final int RANDOMNESS_BYTE_LEN = 10;

    /**
     * The most significant 64 bits of this ULID.
     */
    private final long msb;

    /**
     * The least significant 64 bits of this ULID.
     */
    private final long lsb;

    /**
     * Default alphabet of ULID
     */
    private static final char[] DEFAULT_ALPHABET = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
            'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'Z'};

    /**
     * Decoding table of ULID
     */
    private static final byte[] DECODING_TABLE = new byte[]{
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, 0x00, 0x01,
            0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, -1, -1,
            -1, -1, -1, -1, -1, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e,
            0x0f, 0x10, 0x11, -1, 0x12, 0x13, -1, 0x14, 0x15, -1,
            0x16, 0x17, 0x18, 0x19, 0x1a, -1, 0x1b, 0x1c, 0x1d, 0x1e,
            0x1f, -1, -1, -1, -1, -1, -1, 0x0a, 0x0b, 0x0c,
            0x0d, 0x0e, 0x0f, 0x10, 0x11, -1, 0x12, 0x13, -1, 0x14,
            0x15, -1, 0x16, 0x17, 0x18, 0x19, 0x1a, -1, 0x1b, 0x1c,
            0x1d, 0x1e, 0x1f, -1, -1, -1, -1, -1
    };

    /**
     * The length of bytes of ULID
     */
    private static final int ULID_BYTE_LEN = 0x1a;

    private ULID(long timestamp, byte[] random) {
        Assertx.mustTrue((timestamp & TIMESTAMP_MASK) == 0, "Invalid timestamp");
        Assertx.mustTrue(!Objects.isNull(random) && RANDOMNESS_BYTE_LEN == random.length, "Invalid timestamp");

        long msb = 0;
        long lsb = 0;
        byte[] bytes = new byte[16];
        byte[] ts = new byte[6];
        for (int i = 0; i < 6; i++) {
            ts[i] = (byte) ((timestamp >>> (40 - i * 8)) & 0xff);
        }
        System.arraycopy(ts, 0, bytes, 0, 6);
        System.arraycopy(random, 0, bytes, 6, 10);
        for (int i = 0; i < 8; i++) {
            msb = (msb << 8) | (bytes[i] & 0xff);
        }
        for (int i = 8; i < 16; i++) {
            lsb = (lsb << 8) | (bytes[i] & 0xff);
        }
        this.msb = msb;
        this.lsb = lsb;
    }

    private ULID(long msb, long lsb) {
        this.msb = msb;
        this.lsb = lsb;
    }

    /**
     * Get the most significant 64 bits of this ULID.
     */
    public long getMostSignificantBits() {
        return this.msb;
    }

    /**
     * Get the least significant 64 bits of this ULID.
     */
    public long getLeastSignificantBits() {
        return this.lsb;
    }

    /**
     * Get the timestamp component of ULID
     *
     * @return the timestamp component
     */
    public long getTimestamp() {
        return this.msb >>> 16;
    }

    /**
     * Get next ULID
     *
     * @return A new ULID
     */
    public ULID increase() {
        long newMsb = this.msb;
        long newLsb = this.lsb + 1;
        if (newLsb == OVERFLOW) {
            newMsb += 1;
        }
        return new ULID(newMsb, newLsb);
    }

    /**
     * Format ULID to canonical string with default alphabet. Use 'formatUnsignedLong0' from Long.formatUnsignedLong0()
     *
     * @param alphabet The Alphabet used to encode
     * @return canonical string
     */
    private String toCanonicalString(char[] alphabet) {
        byte[] bytes = new byte[ULID_BYTE_LEN];
        formatUnsignedLong0(this.lsb & 0xffffffffffL, 5, bytes, 18, 8, alphabet);
        formatUnsignedLong0(((this.msb & 0xffffL) << 24) | (this.lsb >>> 40), 5, bytes, 10, 8, alphabet);
        formatUnsignedLong0(this.msb >> 16, 5, bytes, 0, 10, alphabet);
        return new String(bytes, StandardCharsets.US_ASCII);
    }

    /**
     * Reference to java.lang.Long.formatUnsignedLong0()
     */
    private static void formatUnsignedLong0(long val, int shift, byte[] buf, int offset, int len, char[] alphabet) {
        int charPos = offset + len;
        long radix = 1L << shift;
        long mask = radix - 1;
        do {
            buf[--charPos] = (byte) alphabet[(int) (val & mask)];
            val >>>= shift;
        } while (charPos > offset);
    }

    @Override
    public String toString() {
        return toCanonicalString(DEFAULT_ALPHABET);
    }

    public static ULID fromString(String value) {
        if (Objects.isNull(value) || ULID_BYTE_LEN != value.length()) {
            throw new IllegalArgumentException("Invalid length of ULID");
        }
        char[] chars = value.toCharArray();
        if ((DECODING_TABLE[chars[0]] & TIMESTAMP_OVERFLOW_FLAG) != 0) {
            throw new IllegalArgumentException("Time overflow");
        }
        for (char c : chars) {
            if (DECODING_TABLE[c] == -1) {
                throw new IllegalArgumentException("Invalid ULID canonical string for char '" + c + "'");
            }
        }
        long timestamp = decodeComponent(0, 0x00, 0x09, 5, 50, DECODING_TABLE, chars);
        long highRandomness = decodeComponent(0, 0x0a, 0x11, 5, 40, DECODING_TABLE, chars);
        long lowRandomness = decodeComponent(0, 0x12, 0x19, 5, 40, DECODING_TABLE, chars);
        return new ULID((timestamp << 16) | (highRandomness >>> 24), (highRandomness << 40) | (lowRandomness & 0xffffffffffL));
    }

    /**
     * Decode component from char buf.
     *
     * @return Decoded unsigned long value
     */
    private static long decodeComponent(long val, int start, int end, int shirt, int ms, byte[] table, char[] buf) {
        for (int i = start; i <= end; i++) {
            val |= (long) table[buf[i]] << (ms = ms - shirt);
        }
        return val;
    }


    /**
     * Create a new UUID from this ULID.
     *
     * @return A new UUID created with msb and lsb from this ULID
     */
    public UUID toUUID() {
        return new UUID(this.msb, this.lsb);
    }

    /**
     * Create a new ULID from another one UUID.
     *
     * @param uuid Another one UUID
     * @return A new ULID with the specified value
     */
    public static ULID fromUUID(UUID uuid) {
        return new ULID(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
    }

    @Override
    public int compareTo(@NotNull ULID o) {
        int mostSigBits = Long.compare(this.msb, o.msb);
        return mostSigBits != 0 ? mostSigBits : Long.compare(this.lsb, o.lsb);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ULID ulid = (ULID) o;
        return msb == ulid.msb && lsb == ulid.lsb;
    }

    @Override
    public int hashCode() {
        return Objects.hash(msb, lsb);
    }


}
