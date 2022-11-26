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

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * IOStreamx Test Cases
 *
 * @author Alan Yeh
 * @since 2022/11/27
 */
public class TestIOStreamx {

    @Test
    public void case1() throws Exception {
        var text = """
                Dark light, just light each other. The responsibility that you and my shoulders take together, the such as one dust covers up.
                Afraid only afraid the light is suddenly put out in the endless dark night and countless loneliness.
                """.trim();
        var stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        var line = IOStreamx.readLine(stream, StandardCharsets.UTF_8);
        assertEquals("Dark light, just light each other. The responsibility that you and my shoulders take together, the such as one dust covers up.", line);

        line = IOStreamx.readLine(stream, StandardCharsets.UTF_8);
        assertEquals("Afraid only afraid the light is suddenly put out in the endless dark night and countless loneliness.", line);
    }
}
