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

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ObservableList Test Cases
 *
 * @author Alan Yeh
 * @since 2022/11/11
 */
public class TestObservableList {
    @Test
    public void case1() throws Exception {
        var list = new ObservableList<String>();

        var count = new AtomicInteger(0);

        list.addObserver(event -> {
            if (event instanceof ObservableList.ElementAdded<String> added) {
                System.out.println(added.getElements());
                count.incrementAndGet();
            }
        });

        list.add("Test");

        assertEquals(1, count.get());
    }

    @Test
    public void case2() throws Exception {
        var count = new AtomicInteger();

        var list = new ObservableList<String>();
        list.addAll(List.of("test1", "test2", "test3", "test4"));
        list.addObserver(event -> {
            if (event instanceof ObservableList.ElementRemoved<String> removed) {
                count.incrementAndGet();
                assertEquals(1, removed.getElements().size());
                assertEquals("test1", removed.getElements().get(0));
            }
        });

        list.retainAll(List.of("test2", "test3", "test4", "test5"));
        assertEquals(3, list.size());
        assertEquals(1, count.get());
    }
}
