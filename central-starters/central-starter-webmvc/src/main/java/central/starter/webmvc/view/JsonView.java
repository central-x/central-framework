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

package central.starter.webmvc.view;

import central.util.Jsonx;
import jakarta.annotation.Nonnull;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Json View
 *
 * @author Alan Yeh
 * @since 2023/11/04
 */
public class JsonView extends TextView {
    public JsonView(@Nonnull Map<String, Object> map) {
        super(Jsonx.Default().serialize(map));
    }

    public JsonView(@Nonnull Object obj) {
        super(Jsonx.Default().serialize(obj));
    }

    @Override
    public String getContentType() {
        return new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8).toString();
    }
}
