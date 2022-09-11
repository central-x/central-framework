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

package central.starter.graphql.data.dto;

import central.bean.Page;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alan Yeh
 * @since 2022/09/09
 */
public interface DTO {
    static <T extends S, S extends Serializable> T wrap(S source, Class<T> dto) {
        if (source == null) {
            return null;
        }
        try {
            T target = dto.newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    static <T extends S, S extends Serializable> List<T> wrap(List<S> sources, Class<T> dto) {
        return sources.stream().map(it -> DTO.wrap(it, dto)).collect(Collectors.toList());
    }

    static <T extends S, S extends Serializable> Page<T> wrap(Page<S> source, Class<T> dto) {
        return new Page<>(source.getData().stream().map(it -> DTO.wrap(it, dto)).collect(Collectors.toList()), source.getPager());
    }
}
