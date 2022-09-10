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

package central.io;

import central.lang.PublicApi;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * 资源处理
 *
 * @author Alan Yeh
 * @since 2022/08/11
 */
@PublicApi
@UtilityClass
public class Resourcex {

    /**
     * 获取资源
     * 资源路径支持 classpath://、file:// 等协议
     *
     * @param resourcePath 资源路径
     * @return InputStream
     */
    public static InputStream getInputStream(URI resourcePath) throws IOException {
        switch (resourcePath.getScheme()) {
            case "classpath" -> {
                return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath.getRawPath());
            }
            case "file" -> {
                return Files.newInputStream(Path.of(resourcePath.getRawPath()), StandardOpenOption.READ);
            }
            case "http", "https" -> {
                return resourcePath.toURL().openStream();
            }
            default -> {
                throw new IllegalArgumentException("不支持的协议: " + resourcePath.getScheme());
            }
        }
    }
}
