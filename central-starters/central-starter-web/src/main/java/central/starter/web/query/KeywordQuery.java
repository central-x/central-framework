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

package central.starter.web.query;

import central.util.Arrayx;
import central.util.Stringx;
import lombok.Data;

import javax.annotation.Nonnull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 模板查询
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
@Data
public class KeywordQuery implements Serializable {
    @Serial
    private static final long serialVersionUID = -1863820286887438610L;

    /**
     * 通过模糊查询
     * 使用空格分隔多个关键字
     */
    private String keyword;

    public @Nonnull List<String> getKeywords() {
        if (Stringx.isNullOrBlank(this.keyword)) {
            return Collections.emptyList();
        } else {
            return Arrayx.asStream((keyword.trim().split("[ ]")))
                    .map(String::trim)
                    .filter(Stringx::isNotBlank)
                    .map(it -> "%" + it + "%")
                    .toList();
        }
    }
}
