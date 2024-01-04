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

package central.starter.probe.core.cache.memory;

import central.starter.probe.ProbeProperties;
import central.starter.probe.core.cache.Cache;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 内存缓存
 *
 * @author Alan Yeh
 * @since 2024/01/04
 */
@Component
@ConditionalOnProperty(value = "central.probe.cache.enabled", havingValue = "true")
public class MemoryCache implements Cache {

    @Setter(onMethod_ = @Autowired)
    private ProbeProperties properties;

    /**
     * 缓存产生时间
     */
    private long timestamp = 0;
    /**
     * 缓存
     */
    private Map<String, String> data;

    @Override
    public Map<String, String> get() {
        if (this.data == null) {
            return null;
        }
        if (System.currentTimeMillis() - this.timestamp > this.properties.getCache().getTimeout()) {
            this.data = null;
            return null;
        }
        return this.data;
    }

    @Override
    public void put(Map<String, String> data) {
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
}
