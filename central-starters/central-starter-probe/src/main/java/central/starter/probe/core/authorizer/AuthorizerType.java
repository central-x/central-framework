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

package central.starter.probe.core.authorizer;

import central.bean.OptionalEnum;
import central.starter.probe.core.authorizer.fixed.FixedAuthorizer;
import central.starter.probe.core.authorizer.jwt.JwtAuthorizer;
import central.starter.probe.core.authorizer.none.NoneAuthorizer;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 探针保护类型
 *
 * @author Alan Yeh
 * @since 2024/01/03
 */
@Getter
@AllArgsConstructor
public enum AuthorizerType implements OptionalEnum<Class<? extends Authorizer>> {
    /**
     * JWT 监权
     */
    JWT("jwt", JwtAuthorizer.class),
    /**
     * 固定监权
     */
    FIXED("fixed", FixedAuthorizer.class),
    /**
     * 无监权
     */
    NONE("none", NoneAuthorizer.class);

    private final String name;
    private final Class<? extends Authorizer> value;
}
