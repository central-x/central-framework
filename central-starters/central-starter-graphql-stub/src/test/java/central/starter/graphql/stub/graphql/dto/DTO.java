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

package central.starter.graphql.stub.graphql.dto;

import central.bean.KeyValue;
import central.bean.Page;
import central.sql.data.Entity;
import central.util.Listx;
import central.util.Mapx;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Data Transfer Object
 *
 * @author Alan Yeh
 * @since 2022/09/09
 */
public interface DTO {
    /**
     * 将单个 Entity 转换成 DTO
     *
     * @param source 源数据类型
     * @param dto    DTO 类型
     */
    @SneakyThrows({NoSuchMethodException.class, InstantiationException.class, IllegalAccessException.class, InvocationTargetException.class})
    static <T extends S, S extends Entity> T wrap(S source, Class<T> dto) {
        if (source == null) {
            return null;
        }

        var target = dto.getConstructor().newInstance();
        BeanUtils.copyProperties(source, target);
        return target;
    }

    /**
     * 将 {@code List<? extend Entity>} 转换成 DTO
     *
     * @param sources 源数据类型
     * @param dto     DTO 类型
     */
    static <T extends S, S extends Entity> List<T> wrap(List<? extends S> sources, Class<T> dto) {
        return Listx.asStream(sources).map(it -> DTO.wrap(it, dto)).toList();
    }

    /**
     * 将 {@code Map<String, ? extend Entity>} 转换成 DTO
     *
     * @param sources 源数据类型
     * @param dto     DTO 类型
     */
    static <T extends S, S extends Entity> Map<String, T> wrap(Map<String, ? extends S> sources, Class<T> dto) {
        return Mapx.asStream(sources).map(it -> KeyValue.of(it.getKey(), DTO.wrap(it.getValue(), dto))).collect(Collectors.toMap(KeyValue::getKey, KeyValue::getValue));
    }

    /**
     * 将 {@code Page<? extend Entity>} 转换成 DTO
     *
     * @param source 源数据分页类型
     * @param dto    DTO 类型
     */
    static <T extends S, S extends Entity> Page<T> wrap(Page<? extends S> source, Class<T> dto) {
        return new Page<T>(Listx.asStream(source.getData()).map(it -> DTO.wrap(it, dto)).toList(), source.getPager());
    }
}
