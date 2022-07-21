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

import central.lang.Assertx;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Markdown 文件加载器
 * <p>
 * 这个功能是从 Beetl 中抄过来的，使用 Markdown 来管理一些自定义语句非常方便，而且可视化效果非常好。
 * <p>
 * 格式如下：
 * <p>
 * {@code
 * command
 * ===
 * * 这是一个命令
 * <p>
 * ```sql
 * SELECT * FROM TABLE WHERE 1 = 1
 * }
 *
 * @author Alan Yeh
 * @since 2022/07/13
 */
public class MarkdownResources implements Map<String, MarkdownResources.Resource> {
    private Map<String, Resource> resources;

    public MarkdownResources() {
        resources = new LinkedHashMap<>();
    }

    /**
     * 加载指定流
     *
     * @param stream Markdown 文件流
     */
    public MarkdownResources(@Nonnull InputStream stream) throws IOException {
        Assertx.mustNotNull(stream, "[MarkdownResources] 参数错误: stream 不能为空");
        this.load(stream);
    }

    /**
     * 加载指定文件
     *
     * @param markdown Markdown 文件
     */
    public MarkdownResources(@Nonnull File markdown) throws IOException {
        Assertx.mustNotNull(markdown, "[MarkdownResources] 参数错误: markdown 不能为空");
        Assertx.mustTrue(markdown.isFile(), "[MarkdownResources] 参数错误: markdown 不是文件类型");
        Assertx.mustTrue(".md".equals(Stringx.substringAfter(markdown.getName(), ".")), "[MarkdownResources] 参数错误: markdown 不是 .md 文件类型");

        this.load(new FileInputStream(markdown));
    }

    // 加载文件
    private void load(InputStream stream) throws IOException {
        this.resources = new LinkedHashMap<>();

        try (Reader reader = new Reader(stream)) {
            do {
                Resource resource = reader.next();
                if (resource == null) {
                    break;
                }
                resources.put(resource.getId(), resource);
            } while (true);
        }
    }

    /**
     * Markdown Item
     */
    @Data
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Resource {
        /**
         * ID
         */
        private String id;
        /**
         * 注释
         */
        private String comment;
        /**
         * 内容
         */
        private String content;
        /**
         * 行号
         */
        private int line;

        @Override
        public String toString() {
            return content;
        }
    }

    /**
     * Markdown 解析工具
     */
    private static class Reader implements Closeable {
        private final BufferedReader bufferedReader;
        private int line; // 当前读取哪一行
        private String lastLine; // 前一行
        private String penultimateLine; // 前两行
        private int status = 0; // 解析器状态，如果 status == END 表示文件已经读到末尾了
        private static final int END = 1;

        static String lineSeparator = System.getProperty("line.separator", "\n");

        public Reader(InputStream stream) throws IOException {
            this.bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            skipHeader();
        }

        @Override
        public void close() throws IOException {
            this.bufferedReader.close();
        }

        // 读取下一条内容
        public Resource next() throws IOException {
            // 读取 ID
            String id = readId();
            if (status == END) {
                return null;
            }

            // 读注释
            String comment = readComment();
            if (status == END) {
                return null;
            }

            // 读取内容
            int line = this.line; // 记录行号
            String content = readContent();

            return new Resource(id, comment, content, line);
        }

        // 跳过头部信息
        // 从第一个 === 开始读
        private void skipHeader() throws IOException {
            while (true) {
                String line = nextLine();
                if (status == END) {
                    return;
                }
                if (line.startsWith("===")) {
                    return;
                }
            }
        }

        // 读注释
        // 注释是指 * 后面接着的文本
        private String readComment() throws IOException {
            LinkedList<String> list = new LinkedList<>();

            while (true) {
                String line = nextLine();
                if (status == END) { // 读取最后一行了
                    break;
                }

                String str = line.trim();

                if (str.length() > 0 && !str.startsWith("*")) {
                    break;
                }

                list.add(line);
            }

            Iterator<String> it = list.descendingIterator();
            while (it.hasNext()) {
                String str = it.next();
                str = str.trim();
                if (str.length() > 0) {
                    break;
                }
                it.remove();
            }

            return Stringx.join(list.toArray(), lineSeparator);
        }

        // 读内容
        private String readContent() throws IOException {
            LinkedList<String> list = new LinkedList<>();
            list.add(lastLine);

            while (true) {
                String line = nextLine();

                if (status == END) {
                    break;
                }

                if (line.startsWith("===")) {
                    // 读到下一块内容的 ID 了，说明本块内容读完了
                    list.removeLast();
                    break;
                }
                list.add(line);
            }

            // 去前后的 ```
            Iterator<String> it = list.iterator();
            while (it.hasNext()) {
                String str = it.next().trim();
                if (str.length() > 0 && !str.startsWith("```") && !str.startsWith("~~~")) {
                    break;
                }
                it.remove();
            }

            it = list.descendingIterator();
            while (it.hasNext()) {
                String str = it.next().trim();
                if (str.length() > 0 && !str.startsWith("```") && !str.startsWith("~~~")) {
                    break;
                }
                it.remove();
            }

            return Stringx.join(list.toArray(), lineSeparator);
        }

        // 读取 ID
        private String readId() {
            // 最后一行是 === 的话，最后两行就是 id
            if (Stringx.isNullOrBlank(penultimateLine)) {
                return null;
            } else {
                return penultimateLine.trim();
            }
        }

        /**
         * 读取下一行文本
         */
        private String nextLine() throws IOException {
            String text = bufferedReader.readLine();
            this.line++;
            if (text == null) {
                status = END; // 将 Reader 的状态设置为已读完了
            }
            // 保存最后读的两行
            // 最后一行是 === 的话，最后两行就是 id
            penultimateLine = lastLine;
            lastLine = text;
            return text;
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Map Implementation
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public int size() {
        return this.resources.size();
    }

    @Override
    public boolean isEmpty() {
        return this.resources.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.resources.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.resources.containsValue(value);
    }

    @Override
    public Resource get(Object key) {
        return this.resources.get(key);
    }

    @Override
    public Resource put(String key, Resource value) {
        throw new UnsupportedOperationException("[MarkdownResources] 不支持修改内容");
    }

    @Override
    public Resource remove(Object key) {
        throw new UnsupportedOperationException("[MarkdownResources] 不支持修改内容");
    }

    @Override
    public void putAll(Map<? extends String, ? extends Resource> m) {
        throw new UnsupportedOperationException("[MarkdownResources] 不支持修改内容");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("[MarkdownResources] 不支持修改内容");
    }

    @Override
    public Set<String> keySet() {
        return this.resources.keySet();
    }

    @Override
    public Collection<Resource> values() {
        return this.resources.values();
    }

    @Override
    public Set<Entry<String, Resource>> entrySet() {
        return this.resources.entrySet();
    }
}
