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

package central.sql;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * 条件操作符
 *
 * @author Alan Yeh
 * @since 2022/07/20
 */
@RequiredArgsConstructor
public enum Operators {
    /**
     * 等于[ = ? ]
     */
    EQ("EQ", "{} = {}"),
    /**
     * 不等于[ <> ? ]
     */
    NE("NE", "{} <> {}"),
    /**
     * 大于 [ > ? ]
     */
    GT("GT", "{} > {}"),
    /**
     * 大于等于 [ >= ? ]
     */
    GE("GE", "{} >= {}"),
    /**
     * 小于 [ < ? ]
     */
    LT("LT", "{} < {}"),
    /**
     * 小于等于 [ <= ? ]
     */
    LE("LE", "{} <= {}"),
    /**
     * 介于 [ between ? and ? ]
     */
    BETWEEN("BETWEEN", "{} BETWEEN {} AND {}"),
    /**
     * 不介于 [ not between ? and ? ]
     */
    NOT_BETWEEN("NOT_BETWEEN", "{} NOT BETWEEN {} AND {}"),
    /**
     * 类似 [ like ? ]
     */
    LIKE("LIKE", "{} LIKE {}"),
    /**
     * 不类似 [ not like ? ]
     */
    NOT_LIKE("NOT_LIKE", "{} NOT LIKE {}"),
    /**
     * 为空 [ is null ]
     */
    IS_NULL("IS_NULL", "{} IS NULL"),
    /**
     * 不为空 [ is not null ]
     */
    IS_NOT_NULL("IS_NOT_NULL", "{} IS NOT NULL"),
    /**
     * 范围 [ in (?, ?) ]
     */
    IN("IN", "{} IN ({})"),
    /**
     * 不在范围里 [ not in (?, ?) ]
     */
    NOT_IN("NOT_IN", "{} NOT IN ({})");

    @Getter
    private final String name;

    @Getter
    private final String value;

    private static final Map<String, Operators> OPS;

    static {
        OPS = Map.ofEntries(
                Map.entry(EQ.getName(), EQ),
                Map.entry(NE.getName(), NE),
                Map.entry(GT.getName(), GT),
                Map.entry(GE.getName(), GE),
                Map.entry(LT.getName(), LT),
                Map.entry(LE.getName(), LE),
                Map.entry(BETWEEN.getName(), BETWEEN),
                Map.entry(NOT_BETWEEN.getName(), NOT_BETWEEN),
                Map.entry(LIKE.getName(), LIKE),
                Map.entry(NOT_LIKE.getName(), NOT_LIKE),
                Map.entry(IS_NULL.getName(), IS_NULL),
                Map.entry(IS_NOT_NULL.getName(), IS_NOT_NULL),
                Map.entry(IN.getName(), IN),
                Map.entry(NOT_IN.getName(), NOT_IN)
        );
    }

    @JsonCreator
    public static @Nullable Operators resolve(String name) {
        return OPS.get(name.toUpperCase());
    }

    @Override
    public String toString() {
        return this.name;
    }
}
