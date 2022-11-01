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

import central.bean.OptionalEnum;
import central.lang.CompareResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * 容量
 *
 * @author Alan Yeh
 * @since 2022/11/01
 */
public final class Capacity implements Serializable, Comparable<Capacity> {
    @Serial
    private static final long serialVersionUID = 4068595332892344041L;

    private static final BigInteger RADIX = BigInteger.valueOf(1024);

    @Getter
    @RequiredArgsConstructor
    public enum Unit implements OptionalEnum<BigInteger> {
        B("B", BigInteger.ONE),
        KB("KB", RADIX),
        MB("MB", RADIX.pow(2)),
        GB("GB", RADIX.pow(3)),
        TB("TB", RADIX.pow(4)),
        PB("PB", RADIX.pow(5)),
        EB("EB", RADIX.pow(6)),
        ZB("ZB", RADIX.pow(7));

        private final String name;
        private final BigInteger value;
    }

    /**
     * 字节长度
     */
    @Getter
    private final BigInteger length;

    private Capacity(BigInteger length) {
        this.length = length;
    }

    public static Capacity of(long length, Unit unit) {
        return new Capacity(BigInteger.valueOf(length).multiply(unit.getValue()));
    }

    public static Capacity ofB(long length) {
        return of(length, Unit.B);
    }

    public static Capacity ofKB(long length) {
        return of(length, Unit.KB);
    }

    public static Capacity ofMB(long length) {
        return of(length, Unit.MB);
    }

    public static Capacity ofGB(long length) {
        return of(length, Unit.GB);
    }

    public static Capacity ofTB(long length) {
        return of(length, Unit.TB);
    }

    public static Capacity ofPB(long length) {
        return of(length, Unit.PB);
    }

    public static Capacity ofEB(long length) {
        return of(length, Unit.EB);
    }

    public static Capacity ofZB(long length) {
        return of(length, Unit.ZB);
    }

    /**
     * 直接输出 B
     */
    @Override
    public String toString() {
        return this.length + " B";
    }

    /**
     * 保留小数点位数
     */
    public String toString(int digit) {
        if (CompareResult.LE.matches(this.length, Unit.KB.getValue())) {
            return this.length + " " + Unit.B.getName();
        } else if (CompareResult.LE.matches(this.length, Unit.MB.getValue())) {
            return String.format("%." + digit + "f KB", Math.round(this.length.multiply(BigInteger.valueOf(10).pow(digit + 1)).divide(Unit.KB.getValue()).floatValue()) / Math.pow(10d, digit + 1));
        } else if (CompareResult.LE.matches(this.length, Unit.GB.getValue())) {
            return String.format("%." + digit + "f MB", Math.round(this.length.multiply(BigInteger.valueOf(10).pow(digit + 1)).divide(Unit.MB.getValue()).floatValue()) / Math.pow(10d, digit + 1));
        } else if (CompareResult.LE.matches(this.length, Unit.TB.getValue())) {
            return String.format("%." + digit + "f GB", Math.round(this.length.multiply(BigInteger.valueOf(10).pow(digit + 1)).divide(Unit.GB.getValue()).floatValue()) / Math.pow(10d, digit + 1));
        } else if (CompareResult.LE.matches(this.length, Unit.PB.getValue())) {
            return String.format("%." + digit + "f TB", Math.round(this.length.multiply(BigInteger.valueOf(10).pow(digit + 1)).divide(Unit.TB.getValue()).floatValue()) / Math.pow(10d, digit + 1));
        } else if (CompareResult.LE.matches(this.length, Unit.EB.getValue())) {
            return String.format("%." + digit + "f PB", Math.round(this.length.multiply(BigInteger.valueOf(10).pow(digit + 1)).divide(Unit.PB.getValue()).floatValue()) / Math.pow(10d, digit + 1));
        } else if (CompareResult.LE.matches(this.length, Unit.ZB.getValue())) {
            return String.format("%." + digit + "f EB", Math.round(this.length.multiply(BigInteger.valueOf(10).pow(digit + 1)).divide(Unit.EB.getValue()).floatValue()) / Math.pow(10d, digit + 1));
        } else {
            return String.format("%." + digit + "f ZB", Math.round(this.length.multiply(BigInteger.valueOf(10).pow(digit + 1)).divide(Unit.ZB.getValue()).floatValue()) / Math.pow(10d, digit + 1));
        }
    }

    @Override
    public int hashCode() {
        return this.length.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (this == o) return true;

        if (o instanceof Capacity capacity) {
            return this.getLength().equals(capacity.getLength());
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(@NotNull Capacity o) {
        return this.getLength().compareTo(o.getLength());
    }

    public boolean lessThan(Capacity other) {
        return CompareResult.LT.matches(this, other);
    }

    public boolean greaterThan(Capacity other) {
        return CompareResult.GT.matches(this, other);
    }
}
