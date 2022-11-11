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

package central.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * ObservableMap Test Cases
 *
 * @author Alan Yeh
 * @since 2022/11/11
 */
public class TestObservableMap {

    @Test
    public void case1(){
        var map = new HashMap<String, String>();
        map.put("test1", "1");
        map.put("test2", "2");
        map.put("test3", "3");

        var removed = map.put("test1", "1");

        var map2 = new HashMap<String, String>();
        map2.put("test2", "22");
        map2.put("test3", "33");
        map2.put("test4", "44");

        map.putAll(map2);

        assertEquals(4, map.size());

//        var map = new ObservableMap<String, String>();
//        map.addObserver(event -> {
//            if (event instanceof ObservableMap.EntryRemoved<String, String> removed){
//
//            }
//        });
//
//        map.put
    }
}
