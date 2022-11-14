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

package central.starter.template.core.impl.beetl.fn;

import central.lang.Arrayx;
import central.security.Digestx;
import lombok.SneakyThrows;
import org.beetl.core.Context;
import org.beetl.core.Function;

import java.nio.charset.StandardCharsets;

/**
 * 摘要算法
 *
 * @author Alan Yeh
 * @since 2022/11/14
 */
public class SHA256 implements Function {
    @Override
    @SneakyThrows
    public Object call(Object[] paras, Context ctx) {
        var para = Arrayx.getFirstOrNull(paras);
        if (para != null) {
            ctx.byteWriter.write(Digestx.SHA256.digest(para.toString(), StandardCharsets.UTF_8).toCharArray());
        }
        return "";
    }
}
