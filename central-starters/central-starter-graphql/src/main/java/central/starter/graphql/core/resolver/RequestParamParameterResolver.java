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

package central.starter.graphql.core.resolver;

import central.lang.Arrayx;
import central.lang.Stringx;
import central.lang.reflect.TypeReference;
import central.util.*;
import central.validation.Validatable;
import central.validation.Validatex;
import graphql.GraphQLException;
import graphql.schema.*;
import jakarta.validation.groups.Default;
import org.dataloader.BatchLoaderEnvironment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 处理入参
 *
 * @author Alan Yeh
 * @see RequestParam
 * @since 2022/09/09
 */
public class RequestParamParameterResolver extends SpringAnnotatedParameterResolver {
    public RequestParamParameterResolver() {
        super(RequestParam.class);
    }

    @Nullable
    @Override
    public Object resolve(@NotNull Class<?> clazz, @NotNull Method method, @NotNull Parameter parameter, @NotNull Context context) {
        if (context.contains(BatchLoaderEnvironment.class)) {
            return resolve(method, parameter, context);
        }

        if (context.contains(DataFetchingEnvironment.class)) {
            return resolve(method, parameter, context.require(DataFetchingEnvironment.class));
        }

        return null;
    }

    public Object resolve(Method method, Parameter parameter, DataFetchingEnvironment environment) {
        RequestParam param = parameter.getAnnotation(RequestParam.class);
        String name = parameter.getName();
        if (Stringx.isNotBlank(param.name()) || Stringx.isNotBlank(param.value())) {
            name = Objectx.getOrDefault(param.name(), param.value());
        }

        Map<String, Object> arguments = environment.getArguments();

        GraphQLArgument argument = environment.getFieldDefinition().getArgument(name);
        if (argument == null) {
            throw new GraphQLException(Stringx.format("执行方法[{}.{}]错误: 参数[{}]在 GraphQL 中没有定义", method.getDeclaringClass().getSimpleName(), method.getName(), name));
        }
        Object value = arguments.get(name);

        // 设置默认值
        if (value == null) {
            if (argument.getArgumentDefaultValue().isSet()) {
                value = argument.getArgumentDefaultValue().getValue();
            }
        }

        if (value == null && !ValueConstants.DEFAULT_NONE.equals(param.defaultValue())) {
            value = param.defaultValue();
        }

        // 判断
        if (value == null && param.required()) {
            throw new GraphQLException(Stringx.format("参数[{}]缺失，请补全后再试", name));
        }

        // 判断类型是否可转换
        if (value != null) {
            // 获取参数类型
            if (GraphQLList.class.isAssignableFrom(argument.getType().getClass()) || GraphQLInputObjectType.class.isAssignableFrom(argument.getType().getClass())) {
                // 如果是 Input 类型，就使用 Json 序列化来绑定参数
                try {
                    String json = Jsonx.Default().serialize(Mapx.newHashMap("input", value));
                    Map<String, Object> map = Jsonx.Default().deserialize(json, TypeReference.ofMap(String.class, TypeReference.of(parameter.getAnnotatedType().getType())));

                    value = map.get("input");
                } catch (Exception ex) {
                    throw new GraphQLException(Stringx.format("执法方法[{}.{}]错误: 参数[{}]序列化异常", method.getDeclaringClass().getSimpleName(), method.getName(), name));
                }

                // Input 类型可以使用 @Validated 来做参数校验
                Validated validated = parameter.getAnnotation(Validated.class);

                if (validated != null) {
                    Class<?>[] groups = validated.value();
                    if (Arrayx.isNullOrEmpty(groups)) {
                        groups = new Class<?>[]{Default.class};
                    }

                    if (value instanceof Validatable validatable) {
                        try {
                            validatable.validate(groups);
                        } catch (Exception ex) {
                            throw new GraphQLException(Stringx.format("执行方法[{}.{}]错误: 参数[{}]验证异常", method.getDeclaringClass().getSimpleName(), method.getName(), name), ex);
                        }
                    } else if (value instanceof List<?> list) {
                        for (Object it : list) {
                            if (it instanceof Validatable validatable) {
                                try {
                                    validatable.validate();
                                } catch (Exception ex) {
                                    throw new GraphQLException(Stringx.format("执行方法[{}.{}]错误: 参数[{}]验证异常", method.getDeclaringClass().getSimpleName(), method.getName(), name), ex);
                                }
                            } else {
                                try {
                                    Validatex.Default().validate(it, groups);
                                } catch (Exception ex) {
                                    throw new GraphQLException(Stringx.format("执行方法[{}.{}]错误: 参数[{}]验证异常", method.getDeclaringClass().getSimpleName(), method.getName(), name), ex);
                                }
                            }
                        }
                    }
                }


            } else if (!parameter.getType().isAssignableFrom(value.getClass())) {
                // 如果值的类型与参数类型不匹配，则需要对其进行类型转换
                FormattingConversionService converter = this.getConverter(environment.getLocalContext());

                if (converter == null || !converter.canConvert(value.getClass(), parameter.getType())) {
                    throw new GraphQLException(Stringx.format("参数[{}]类型错误，无法将{}转换成{}类型，请改正后再试", name, value.getClass().getCanonicalName(), parameter.getType().getCanonicalName()));
                }

                value = converter.convert(value, parameter.getType());
            }
        }
        return value;
    }

    public Object resolve(Method method, Parameter parameter, Context context) {
        RequestParam param = parameter.getAnnotation(RequestParam.class);
        String name = parameter.getName();
        if (Stringx.isNotBlank(param.name()) || Stringx.isNotBlank(param.value())) {
            name = Objectx.getOrDefault(param.name(), param.value());
        }

        List<String> keys = context.get("keys");

        // BatchLoader 只有一个参数
        // BatchLoader 只可以接收 Set<String> 或 List<String> 类型的参数
        if ("ids".equals(name) || "keys".equals(name)) {
            if (List.class.isAssignableFrom(parameter.getType())) {
                // 对 keys 进行排序去重，可以增大缓存击中概率
                return Listx.asStream(keys).distinct().sorted().collect(Collectors.toList());
            } else if (Set.class.isAssignableFrom(parameter.getType())) {
                // 直接返回 Set
                return new HashSet<>(Objectx.getOrDefault(keys, Collections.emptyList()));
            } else {
                throw new GraphQLException(Stringx.format("执行 {}.{} 方法异常：BatchLoader 只接收 Set<String> 或 List<String> 类型的参数", method.getDeclaringClass().getSimpleName(), method.getName()));
            }
        }

        return null;
    }
}
