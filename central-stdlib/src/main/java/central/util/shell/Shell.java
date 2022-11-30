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

package central.util.shell;

import central.lang.Attribute;
import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Shell
 *
 * @author Alan Yeh
 * @since 2022/11/25
 */
public abstract class Shell implements AutoCloseable {

    /**
     * 字符集
     */
    @Getter
    @Setter
    protected Charset charset = StandardCharsets.UTF_8;

    /**
     * 工作目录
     */
    @Getter
    @Setter
    protected Path workDir = Path.of("~");

    protected final ByteArrayOutputStream stdout = new ByteArrayOutputStream();

    protected final List<Consumer<String>> stdoutListeners = new ArrayList<>();

    protected final ByteArrayOutputStream stderr = new ByteArrayOutputStream();

    protected final List<Consumer<String>> stderrListeners = new ArrayList<>();

    @Getter
    protected final Map<String, String> environments = new HashMap<>(Map.of(
            "PATH", "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin",
            "LANG", "en_US.UTF-8"
    ));

    protected final Map<String, Object> features = new HashMap<>();

    /**
     * 设置环境变量
     *
     * @param name  变量名
     * @param value 变量值
     */
    public void setEnvironment(String name, String value) {
        this.environments.put(name, value);
    }

    /**
     * 设置功能
     *
     * @param feature 功能
     * @param value   值
     */
    public <T> void setFeature(Attribute<T> feature, T value) {
        this.features.put(feature.getCode(), value);
    }

    /**
     * 获取功能
     *
     * @param feature 功能
     */
    public <T> T getFeature(Attribute<T> feature) {
        return (T) this.features.getOrDefault(feature.getCode(), feature.getValue());
    }

    /**
     * 添加标准输出监听
     *
     * @param listener 监听
     */
    public void addStdoutListener(Consumer<String> listener) {
        this.stdoutListeners.add(listener);
    }

    /**
     * 添加标准错误输出监听
     *
     * @param listener 监听
     */
    public void addStderrListener(Consumer<String> listener) {
        this.stderrListeners.add(listener);
    }

    /**
     * 获取标准输出
     */
    public String getStdout() {
        return this.stdout.toString(this.charset);
    }

    /**
     * 获取标准错误
     */
    public String getStderr() {
        return this.stderr.toString(this.charset);
    }

    /**
     * 连接到服务器
     */
    public abstract void connect(Duration timeout) throws ShellException;

    /**
     * 当前是否已连接到服务器
     */
    public abstract boolean isConnected();

    /**
     * 断开连接
     */
    public abstract void disconnect() throws ShellException;

    @Override
    public void close() throws Exception {
        this.disconnect();
    }

    /**
     * 移除远程目录
     *
     * @param remoteFile 远程文件（夹），支持相对路径
     */
    public abstract boolean rm(Path remoteFile) throws ShellException;

    /**
     * 创建文件夹
     *
     * @param remotePath 远程目录，支持相对路径
     */
    public abstract boolean mkdirs(Path remotePath) throws ShellException;

    /**
     * 将本地文件写入当前工作目录
     *
     * @param localFile 本地文件（夹）
     */
    public abstract boolean transferTo(Path localFile) throws ShellException;

    /**
     * 将本地文件写入指定远程目录
     *
     * @param localFile  本地文件（夹）
     * @param remotePath 指定远程目录，支持相对路径
     */
    public abstract boolean transferTo(Path localFile, Path remotePath) throws ShellException;

    /**
     * 将指定的远程文件（夹）写入本地目录
     *
     * @param remoteFile 远程文件（夹），支持相对路径
     * @param localPath  本地文件目录
     */
    public abstract boolean transferFrom(Path remoteFile, Path localPath) throws ShellException;

    /**
     * 执行命令
     *
     * @param command 命令
     * @param args    参数
     * @return 执行结果（0 为正常，其余异常[一般抛异常]）
     */
    public abstract int exec(String command, String... args) throws ShellException;
}
