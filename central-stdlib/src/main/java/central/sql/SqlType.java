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

import central.bean.OptionalEnum;
import central.sql.resolver.*;
import central.lang.Arrayx;
import central.util.Listx;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collections;
import java.util.List;

/**
 * 数据库字段类型
 *
 * @author Alan Yeh
 * @since 2022/08/09
 */
@Getter
@AllArgsConstructor
public enum SqlType implements OptionalEnum<String> {
    STRING("字符串", "STRING", String.class, new StringTypeResolver(), Listx.of(Types.LONGVARCHAR, Types.NCHAR, Types.NVARCHAR, Types.ROWID, Types.LONGVARCHAR, Types.CHAR, Types.VARCHAR)),
    CLOB("大字符串", "CLOB", String.class, new ClobTypeResolver(), Listx.of(Types.CLOB, Types.NCLOB)),
    INTEGER("整型", "INTEGER", Integer.class, new IntegerTypeResolver(), Listx.of(Types.TINYINT, Types.INTEGER, Types.SMALLINT)),
    LONG("长整型", "LONG", Long.class, new LongTypeResolver(), Listx.of(Types.BIGINT)),
    BIG_DECIMAL("大数字", "BIG_DECIMAL", BigDecimal.class, new BigDecimalTypeResolver(), Listx.of(Types.NUMERIC, Types.DECIMAL, Types.FLOAT, Types.REAL, Types.DOUBLE)),
    BLOB("二进制", "BLOB", byte[].class, new BlobTypeResolver(), Listx.of(Types.LONGVARBINARY, Types.VARBINARY, Types.BINARY, Types.BLOB)),
    DATETIME("日期", "DATETIME", Timestamp.class, new TimestampTypeResolver(), Listx.of(Types.DATE, Types.TIME, Types.TIMESTAMP)),
    BOOLEAN("布尔值", "BOOLEAN", Boolean.class, new BooleanTypeResolver(), Listx.of(Types.BIT, Types.BOOLEAN)),

    UNKNOWN("未知", "UNKNOWN", Object.class, null, Collections.emptyList());

    /**
     * 类型名称
     */
    private final String name;
    /**
     * 类型
     */
    private final String value;
    /**
     * 对应的 Java 数据类型
     */
    private final Class<?> type;
    /**
     * 对应的数据库类型解析器
     */
    private final SqlTypeResolver resolver;
    /**
     * 对应的数据库类型
     */
    private final List<Integer> types;

    public static @Nullable SqlType resolve(String value) {
        return Arrayx.asStream(SqlType.values()).filter(it -> it.isCompatibleWith(value)).findFirst().orElse(null);
    }

    public static @Nullable SqlType resolve(int type) {
        return Arrayx.asStream(SqlType.values()).filter(it -> it.getTypes().contains(type)).findFirst().orElse(null);
    }

    public static @Nullable SqlType resolve(Class<?> type) {
        return Arrayx.asStream(SqlType.values()).filter(it -> it.getType().equals(type)).findFirst().orElse(null);
    }

    @Override
    public boolean isCompatibleWith(Object value) {
        if (value instanceof SqlType) {
            return this.equals(value);
        }
        if (value instanceof String) {
            return this.getValue().equalsIgnoreCase(value.toString());
        }
        return false;
    }
}
