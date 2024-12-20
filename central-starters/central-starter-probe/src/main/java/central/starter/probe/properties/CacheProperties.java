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

package central.starter.probe.properties;

import central.validation.Label;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Duration;

/**
 * 缓存配置
 *
 * @author Alan Yeh
 * @since 2024/01/04
 */
@Data
public class CacheProperties {
    /**
     * 是否启用缓存
     */
    @Label("是否启用缓存功能")
    private boolean enabled = false;
    /**
     * 缓存有效期
     * <p>
     * 设置缓存有效期时，应注意缓存有效期应短于外部探测间隔
     */
    @NotNull
    @Label("缓存超时时间")
    private Long timeout = Duration.ofSeconds(9).toMillis();
}
