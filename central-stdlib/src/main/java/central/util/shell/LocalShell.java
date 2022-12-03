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

import central.io.IOStreamx;
import central.lang.Assertx;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 本地 Shell
 *
 * @author Alan Yeh
 * @since 2022/12/02
 */
public class LocalShell extends Shell {

    private final ProcessBuilder factory = new ProcessBuilder();

    @Override
    public void connect(Duration timeout) throws ShellException {

    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void disconnect() throws ShellException {

    }

    @Override
    @SneakyThrows(IOException.class)
    public boolean rm(Path remoteFile) throws ShellException {
        remoteFile = toAbsolute(remoteFile);
        var attributes = Files.readAttributes(remoteFile, BasicFileAttributes.class);
        if (attributes.isDirectory()) {
            try (var stream = Files.list(remoteFile)) {
                var files = stream.toList();
                for (var file : files) {
                    if (!this.rm(file)) {
                        return false;
                    }
                }
            }
        }
        return Files.deleteIfExists(remoteFile);
    }

    @Override
    public boolean mkdirs(Path remotePath) throws ShellException {
        try {
            Files.createDirectories(remotePath);
            return true;
        } catch (IOException ex) {
            throw new ShellException("创建文件夹失败: " + ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    public boolean transferTo(Path localFile) throws ShellException {
        return this.transferTo(localFile, this.workDir);
    }

    @Override
    public boolean transferTo(Path localFile, Path remotePath) throws ShellException {
        localFile = this.toAbsolute(localFile);
        remotePath = this.toAbsolute(remotePath);
        return this.transfer(localFile, remotePath);
    }

    @Override
    public boolean transferFrom(Path remoteFile, Path localPath) throws ShellException {
        remoteFile = this.toAbsolute(remoteFile);
        localPath = this.toAbsolute(localPath);
        return this.transfer(remoteFile, localPath);
    }

    private Path toAbsolute(Path path) {
        if (path.isAbsolute()) {
            return Path.of(path.toString());
        } else {
            var home = Path.of(System.getProperty("user.home"));
            switch (path.getName(0).toString()) {
                case "~", "." -> {
                    if (path.getNameCount() == 1) {
                        return home;
                    } else {
                        return home.resolve(path.subpath(1, path.getNameCount()));
                    }
                }
                case ".." -> {
                    if (path.getNameCount() == 1) {
                        return home.getParent();
                    } else {
                        return home.getParent().resolve(path.subpath(1, path.getNameCount()));
                    }
                }
                default -> {
                    return Path.of(path.toString());
                }
            }
        }
    }

    private boolean transfer(Path sourceFile, Path targetPath) throws ShellException {
        try {
            targetPath = targetPath.resolve(sourceFile.getFileName().toString());

            var attributes = Files.readAttributes(sourceFile, BasicFileAttributes.class);

            if (attributes.isDirectory()) {
                // 创建文件夹
                Files.createDirectories(targetPath);

                try (var stream = Files.list(sourceFile)) {
                    var files = stream.toList();
                    for (var file : files) {
                        if (!this.transfer(file, targetPath)) {
                            return false;
                        }
                    }
                }
                return true;
            } else {
                // 复制文件
                Files.copy(sourceFile, targetPath, StandardCopyOption.REPLACE_EXISTING);
                return true;
            }
        } catch (IOException ex) {
            throw new ShellException("复制文件失败: " + ex.getLocalizedMessage(), ex);
        }
    }

    private String process(String command) {
        return command.replace(" ", "\\ ").replace("(", "\\(").replace(")", "\\)");
    }

    @Override
    @SneakyThrows({IOException.class, InterruptedException.class})
    public int exec(String command, String... args) throws ShellException {
        StringBuilder commandBuilder = new StringBuilder(this.process(command));
        for (var arg : args) {
            commandBuilder.append(" ").append(process(arg));
        }

        IOStreamx.writeLine(this.stdout, "$ " + commandBuilder, this.charset);
        this.notifyStdoutListener("$ " + commandBuilder);

        // 设置命令
        factory.command(commandBuilder.toString().split(" "));

        // 设置工作目录
        if (this.workDir == null) {
            // 默认在用户主目录
            this.workDir = Path.of(System.getProperty("user.home"));
        }
        var workDirectory = this.toAbsolute(this.workDir).toFile();
        if (!workDirectory.exists() || !workDirectory.isDirectory()) {
            Assertx.mustTrue(workDirectory.mkdirs(), "无法方法工作目录: " + this.getWorkDir());
        }
        this.factory.directory(workDirectory);

        // 添加环境变量
        this.factory.environment().putAll(this.environments);

        var process = factory.start();
        try (var stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
             var stderrReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            while (true) {
                var isAlive = process.isAlive();

                while (stdoutReader.ready()) {
                    var line = stdoutReader.readLine();
                    IOStreamx.writeLine(this.stdout, line);
                    this.notifyStdoutListener(line);
                }

                while (stderrReader.ready()) {
                    var line = stderrReader.readLine();
                    IOStreamx.writeLine(this.stderr, line);
                    this.notifyStderrListener(line);
                }

                if (!isAlive) {
                    break;
                } else {
                    // 等待 50ms
                    TimeUnit.MILLISECONDS.sleep(20);
                }
            }

            return process.exitValue();
        }
    }
}
