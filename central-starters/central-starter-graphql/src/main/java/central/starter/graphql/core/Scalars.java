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

package central.starter.graphql.core;

import central.util.Stringx;
import graphql.language.StringValue;
import graphql.schema.*;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 标量
 *
 * @author Alan Yeh
 * @since 2022/09/09
 */
public class Scalars {

    static final GraphQLScalarType TIMESTAMP = GraphQLScalarType.newScalar()
            .name("Timestamp")
            .description("Scalar for java.sql.Timestamp.")
            .coercing(new Coercing<Timestamp, Long>() {
                @Override
                public Long serialize(@NotNull Object dataFetcherResult) throws CoercingSerializeException {
                    if (dataFetcherResult instanceof Timestamp timestamp) {
                        return timestamp.getTime();
                    } else {
                        throw new CoercingSerializeException(Stringx.format("不支持序列化 {} 类型", dataFetcherResult.getClass()));
                    }
                }

                @Override
                public @NotNull Timestamp parseValue(@NotNull Object input) throws CoercingParseValueException {
                    long value;
                    try {
                        value = Long.parseLong(input.toString());
                    } catch (NumberFormatException ex) {
                        throw new CoercingParseValueException(Stringx.format("无法将 {} 转换成 Timestamp 类型", input));
                    }
                    return new Timestamp(value);
                }

                @Override
                public @NotNull Timestamp parseLiteral(@NotNull Object input) throws CoercingParseLiteralException {
                    if (input instanceof StringValue string) {
                        return parseValue(string.getValue());
                    } else {
                        throw new CoercingParseValueException(Stringx.format("无法将 {} 转换成 Timestamp 类型", input));
                    }
                }
            })
            .build();

    static final GraphQLScalarType ANY = GraphQLScalarType.newScalar()
            .name("Any")
            .description("Scalar for Any type")
            .coercing(new Coercing<Object, Object>() {
                @Override
                public Object serialize(@NotNull Object dataFetcherResult) throws CoercingSerializeException {
                    return dataFetcherResult;
                }

                @Override
                public @NotNull Object parseValue(@NotNull Object input) throws CoercingParseValueException {
                    return input;
                }

                @Override
                public @NotNull Object parseLiteral(@NotNull Object input) throws CoercingParseLiteralException {
                    return input;
                }
            })
            .build();

    static final GraphQLScalarType LONG = GraphQLScalarType.newScalar()
            .name("Long")
            .description("Scalar for Long type")
            .coercing(new Coercing<Long, Long>() {
                @Override
                public Long serialize(@NotNull Object dataFetcherResult) throws CoercingSerializeException {
                    if (dataFetcherResult instanceof Number number) {
                        return number.longValue();
                    } else if (dataFetcherResult instanceof String string) {
                        BigDecimal value;
                        try {
                            value = new BigDecimal(string);
                        } catch (NumberFormatException ex) {
                            throw new CoercingSerializeException(Stringx.format("{} 不是有效的数字", string));
                        }

                        try {
                            return value.longValue();
                        } catch (ArithmeticException ex) {
                            throw new CoercingSerializeException(Stringx.format("在转换 {} 为数字时发生异常: {}", string, ex.getLocalizedMessage()), ex);
                        }
                    } else {
                        throw new CoercingSerializeException(Stringx.format("不支持序列化 {} 类型", dataFetcherResult.getClass()));
                    }
                }

                @Override
                public @NotNull Long parseValue(@NotNull Object input) throws CoercingParseValueException {
                    if (input instanceof Number number) {
                        return number.longValue();
                    } else if (input instanceof String string) {
                        BigDecimal value;
                        try {
                            value = new BigDecimal(string);
                        } catch (NumberFormatException ex) {
                            throw new CoercingSerializeException(Stringx.format("{} 不是有效的数字", string));
                        }

                        try {
                            return value.longValue();
                        } catch (ArithmeticException ex) {
                            throw new CoercingSerializeException(Stringx.format("在转换 {} 为数字时发生异常: {}", string, ex.getLocalizedMessage()), ex);
                        }
                    } else {
                        throw new CoercingSerializeException(Stringx.format("不支持序列化 {} 类型", input.getClass()));
                    }
                }

                @Override
                public @NotNull Long parseLiteral(@NotNull Object input) throws CoercingParseLiteralException {
                    return parseValue(input);
                }
            })
            .build();
}
