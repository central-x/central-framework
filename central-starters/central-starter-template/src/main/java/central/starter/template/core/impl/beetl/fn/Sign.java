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
import org.beetl.core.Context;
import org.beetl.core.Function;

import java.nio.charset.StandardCharsets;

/**
 * 参数签名
 * <p>
 * 计算规则: md5('arg0:' + args[0].hashCode() + '[' + args[0].toString() + ']' + ',arg1:' + args[1].hashCode() + '[' + args[1].toString() + ']' + ...)
 *
 * @author Alan Yeh
 * @since 2022/11/16
 */
public class Sign implements Function {
    @Override
    public Object call(Object[] paras, Context ctx) {
        var para = Arrayx.getFirstOrNull(paras);
        if (para == null) {
            return "";
        }

        // 计算 args 的签名
        var string = new StringBuilder();
        if (para.getClass().isArray()) {
            var args = (Object[]) para;

            for (int i = 0; i < args.length; i++) {
                if (string.length() > 0) {
                    string.append(",");
                }

                string.append("arg").append(i).append(":");
                if (args[i] == null) {
                    string.append("null");
                } else {
                    string.append(args[i].getClass().getName()).append("@").append(args[i].hashCode()).append("[").append(args[i].toString()).append("]");
                }
            }
        } else {
            string.append(para.getClass().getName()).append("@").append(para.hashCode()).append("[").append(para.toString()).append("]");
        }

        return Digestx.SHA256.digest(string.toString(), StandardCharsets.UTF_8);
    }
}
