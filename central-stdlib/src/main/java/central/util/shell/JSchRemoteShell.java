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

import central.io.Filex;
import central.io.IOStreamx;
import central.lang.Arrayx;
import central.lang.Assertx;
import central.lang.Stringx;
import com.jcraft.jsch.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.KeyPair;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 使用 JSch 实现的 RemoteShell
 *
 * @author Alan Yeh
 * @since 2022/12/03
 */
@RequiredArgsConstructor
public class JSchRemoteShell extends Shell {
    /**
     * 主机
     */
    @Getter
    private final String host;
    /**
     * 端口
     */
    @Getter
    private final int port;
    /**
     * 用户名
     */
    @Getter
    private final String username;
    /**
     * 密码
     */
    @Getter
    private final String password;
    /**
     * SSH 密钥（公钥）
     */
    @Getter
    private final KeyPair publicKey;

    /**
     * 使用默认端口创建远程 Shell
     *
     * @param host     主机名
     * @param username 用户名
     * @param password 密码
     */
    public static JSchRemoteShell of(String host, String username, String password) {
        return new JSchRemoteShell(host, 22, username, password, null);
    }

    /**
     * 创建远程 Shell
     *
     * @param host     主机名
     * @param port     端口
     * @param username 用户名
     * @param password 密码
     */
    public static JSchRemoteShell of(String host, int port, String username, String password) {
        return new JSchRemoteShell(host, port, username, password, null);
    }

    /**
     * 使用默认端口创建远程 Shell
     *
     * @param host      主机名
     * @param username  用户名
     * @param publicKey 认证密钥（ssh public key）
     */
    public static JSchRemoteShell of(String host, int port, String username, KeyPair publicKey) {
        return new JSchRemoteShell(host, port, username, null, publicKey);
    }

    /**
     * 创建远程 Shell
     *
     * @param host      主机名
     * @param username  用户名
     * @param publicKey 认证密钥（ssh public key）
     */
    public static JSchRemoteShell of(String host, String username, KeyPair publicKey) {
        return new JSchRemoteShell(host, 22, username, null, publicKey);
    }

    /**
     * 工作目录
     * <p>
     * 远程工作目录没办法根据当前目录推导出相对路径，因此只能使用 '~' 或绝对路径来表达工作目录
     */
    @Override
    public void setWorkDir(Path workDir) {
        if (workDir.startsWith(Path.of(".")) || workDir.startsWith("..")) {
            throw new IllegalArgumentException("WorkDir cannot start with '.' or '..'");
        } else {
            super.setWorkDir(workDir);
        }
    }

    private final JSch factory = new JSch();
    private Session session;

    /**
     * 远程 Home 目录
     * <p>
     * 用于推导以 ~ 开头的远程目录
     */
    private Path remoteHome;

    @Override
    public void connect(Duration timeout) throws ShellException {
        try {
            var session = this.factory.getSession(this.username, this.host, this.port);
            if (Stringx.isNotBlank(this.password)) {
                session.setPassword(this.password);
            }
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect((int) timeout.toMillis());
            if (!session.isConnected()) {
                throw new ShellException("连接服务器失败");
            }
            this.session = session;

            ChannelSftp channel = null;
            try {
                channel = (ChannelSftp) session.openChannel("sftp");
                channel.connect(500);
                if (!channel.isConnected()) {
                    throw new ShellException("连接服务器失败");
                }

                this.remoteHome = Path.of(channel.getHome());
            } finally {
                if (channel != null) {
                    // 结束连接
                    channel.disconnect();
                }
            }
        } catch (JSchException | SftpException ex) {
            throw new ShellException("连接服务器失败: " + ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    public boolean isConnected() {
        return this.session != null && this.session.isConnected();
    }

    @Override
    public void disconnect() throws ShellException {
        if (this.session != null) {
            this.session.disconnect();
            this.session = null;
        }
    }

    @Override
    public boolean rm(Path remoteFile) throws ShellException, IOException {
        // sftp 不能删除一个非空的目录，因此如果要删除一个目录，需要递归删除它的子文件夹的子文件
        // 因此直接用 rm -rf 来删除会更高效一些
        remoteFile = this.toRemoteAbsolute(remoteFile);
        this.exec("rm", "-rf", remoteFile.toString());
        return true;
    }

    @Override
    public boolean mkdirs(Path remotePath) throws ShellException, IOException {
        ChannelSftp channel = null;
        try {
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect(500);
            if (!channel.isConnected()) {
                throw new ShellException("连接服务器失败");
            }

            remotePath = this.toRemoteAbsolute(remotePath);

            this.cd(channel, remotePath);
            return true;
        } catch (JSchException | SftpException ex) {
            throw new ShellException("创建文件夹失败: " + ex.getLocalizedMessage(), ex);
        } finally {
            if (channel != null) {
                // 结束连接
                channel.disconnect();
            }
        }
    }

    @Override
    public boolean transferTo(Path localFile) throws ShellException, IOException {
        return this.transferTo(localFile, this.workDir);
    }

    @Override
    public boolean transferTo(Path localFile, Path remotePath) throws ShellException, IOException {
        ChannelSftp channel = null;
        try {
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect(500);
            if (!channel.isConnected()) {
                throw new ShellException("连接服务器失败");
            }

            localFile = this.toLocalAbsolute(localFile);
            remotePath = this.toRemoteAbsolute(remotePath);

            this.cd(channel, remotePath);

            // 传输文件（夹）
            if (localFile.toFile().isDirectory()) {
                this.transferDirectoryTo(channel, localFile.toFile());
            } else if (localFile.toFile().isFile()) {
                this.transferFileTo(channel, localFile.toFile());
            } else {
                throw new IOException("不支持的文件类型: " + localFile);
            }
            return true;
        } catch (JSchException | SftpException ex) {
            throw new ShellException("传输文件失败: " + ex.getLocalizedMessage(), ex);
        } finally {
            if (channel != null) {
                // 结束连接
                channel.disconnect();
            }
        }
    }

    @Override
    public boolean transferFrom(Path remoteFile, Path localPath) throws ShellException, IOException {
        ChannelSftp channel = null;
        try {
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect(500);
            if (!channel.isConnected()) {
                throw new ShellException("连接服务器失败");
            }

            localPath = this.toLocalAbsolute(localPath);
            remoteFile = this.toRemoteAbsolute(remoteFile);

            // 查询路径状态
            // 文件不存在时会抛异常
            var stat = channel.stat(remoteFile.toString());

            if (stat.isDir()) {
                this.transferDirectoryFrom(channel, remoteFile, localPath);
            } else {
                this.transferFileFrom(channel, remoteFile, localPath);
            }

            return true;
        } catch (JSchException | SftpException ex) {
            throw new ShellException("传输文件失败: " + ex.getLocalizedMessage(), ex);
        } finally {
            if (channel != null) {
                // 结束连接
                channel.disconnect();
            }
        }
    }

    private void transferDirectoryFrom(ChannelSftp channel, Path remoteDirectory, Path localPath) throws IOException, SftpException {
        var localDirectory = localPath.resolve(remoteDirectory.getFileName());
        if (!localDirectory.toFile().exists() || !localDirectory.toFile().isDirectory()) {
            Assertx.mustTrue(localDirectory.toFile().mkdirs(), IOException::new, "无法访问本地指定路径: " + localPath);
        }

        var files = channel.ls(remoteDirectory.toString());
        for (var file : files) {
            if (file instanceof ChannelSftp.LsEntry entry) {
                if (".".equals(entry.getFilename()) || "..".equals(entry.getFilename())) {
                    continue;
                } else {
                    if (entry.getAttrs().isDir()) {
                        this.transferDirectoryFrom(channel, remoteDirectory.resolve(entry.getFilename()), localDirectory);
                    } else {
                        this.transferFileFrom(channel, remoteDirectory.resolve(entry.getFilename()), localDirectory);
                    }
                }
            }
        }
    }

    private void transferFileFrom(ChannelSftp channel, Path remoteFile, Path localPath) throws IOException, SftpException {
        var localFile = localPath.resolve(remoteFile.getFileName()).toFile();
        if (localFile.exists()) {
            if (localFile.isFile()) {
                // 覆盖
                Filex.delete(localFile);
            }
        }
        Assertx.mustTrue(localFile.createNewFile(), IOException::new, "无法访问路径: " + localPath);
        try (var output = IOStreamx.buffered(Files.newOutputStream(localFile.toPath(), StandardOpenOption.WRITE))) {
            channel.get(remoteFile.toString(), output);
        }
    }

    private void transferDirectoryTo(ChannelSftp channel, File directory) throws IOException, SftpException {
        this.cd(channel, directory.getName());
        var files = directory.listFiles();
        if (Arrayx.isNotEmpty(files)) {
            for (var file : files) {
                if (file.isDirectory()) {
                    this.transferDirectoryTo(channel, file);
                } else if (file.isFile()) {
                    this.transferFileTo(channel, file);
                } else {
                    throw new IOException("不支持的文件类型: " + file.getAbsolutePath());
                }
            }
        }
        this.cd(channel, "..");
    }

    private void transferFileTo(ChannelSftp channel, File file) throws IOException, SftpException {
        try (var input = IOStreamx.buffered(Files.newInputStream(file.toPath(), StandardOpenOption.READ))) {
            channel.put(input, file.getName());
        }
    }

    private void cd(ChannelSftp channel, String directory) throws SftpException {
        // 尝试直接进入目录
        try {
            channel.cd(directory);
        } catch (SftpException ex) {
            // 进入失败，可能目录不存在
            if (ex.id == 2) {
                // No such file
                channel.mkdir(directory);
                channel.cd(directory);
            } else {
                throw new ShellException("无法进入文件夹: " + directory, ex);
            }
        }
    }

    private void cd(ChannelSftp channel, Path remotePath) throws SftpException, IOException {
        if (remotePath.isAbsolute()) {
            // 如果是绝对路径，则需要先进入 /
            this.cd(channel, "/");
        }
        for (int i = 0, count = remotePath.getNameCount(); i < count; i++) {
            var name = remotePath.getName(i).toString();
            if ("~".equals(name)) {
                // 进入用户目录
                this.cd(channel, remoteHome);
            } else if (".".equals(name)) {
                // 进入当前目录，忽略
            } else {
                this.cd(channel, name);
            }
        }
    }

    /**
     * 将本地相对路径解析成绝对路径
     */
    private Path toLocalAbsolute(Path path) throws IOException {
        if (path.isAbsolute()) {
            return path.toAbsolutePath();
        } else {
            var localHome = Path.of(System.getProperty("user.home"));
            return switch (path.getName(0).toString()) {
                case ".", ".." -> path.toAbsolutePath();
                case "~" -> {
                    if (path.getNameCount() == 1) {
                        yield localHome;
                    } else {
                        yield localHome.resolve(path.subpath(1, path.getNameCount())).toAbsolutePath();
                    }
                }
                default -> throw new IOException("解析本地路径异常");
            };
        }
    }

    /**
     * 将远程相对路径解析成绝对路径
     *
     * <ul>
     *     <li>.：相对工作目录</li>
     *     <li>..：相对工作目录的上级目录</li>
     *     <li>~：相对用户目录</li>
     * </ul>
     */
    private Path toRemoteAbsolute(Path path) throws IOException {
        if (path.isAbsolute()) {
            return path;
        } else {
            var remotePath = switch (path.getName(0).toString()) {
                case "." -> {
                    if (path.getNameCount() == 1) {
                        yield this.workDir;
                    } else {
                        yield this.workDir.resolve(path.subpath(1, path.getNameCount()));
                    }
                }
                case ".." -> {
                    if (path.getNameCount() == 1) {
                        yield this.workDir.getParent();
                    } else {
                        yield this.workDir.getParent().resolve(path.subpath(1, path.getNameCount()));
                    }
                }
                case "~" -> {
                    if (path.getNameCount() == 1) {
                        yield this.remoteHome;
                    } else {
                        yield this.remoteHome.resolve(path.subpath(1, path.getNameCount()));
                    }
                }
                default -> throw new IOException("解析远程路径异常");
            };

            if (remotePath.startsWith(Path.of("~"))) {
                return this.remoteHome.resolve(remotePath.subpath(1, path.getNameCount()));
            } else {
                return remotePath;
            }
        }
    }

    private String process(String command) {
        return command.replace(" ", "\\ ").replace("(", "\\(").replace(")", "\\)");
    }

    @Override
    @SneakyThrows({IOException.class, InterruptedException.class})
    public int exec(String command, String... args) throws ShellException {
        var commandBuilder = new StringBuilder(this.process(command));
        for (var arg : args) {
            commandBuilder.append(" ").append(process(arg));
        }

        IOStreamx.writeLine(this.stdout, "$ " + commandBuilder, this.charset);
        this.notifyStdoutListener("$ " + commandBuilder);

        ChannelExec channel = null;
        try {
            channel = (ChannelExec) this.session.openChannel("exec");
            this.environments.forEach(channel::setEnv);

            var execPath = this.toRemoteAbsolute(this.workDir);
            // 设置命令
            channel.setCommand("cd " + execPath + " && " + commandBuilder);

            // 设置标准输出
            var stdout = new ByteArrayOutputStream();
            channel.setOutputStream(stdout);

            // 设置标准错误输出
            var stderr = new ByteArrayOutputStream();
            channel.setErrStream(stderr);

            try (var stdoutReader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
                 var stderrReader = new BufferedReader(new InputStreamReader(channel.getErrStream()))) {

                channel.connect();
                channel.start();

                while (true) {
                    // 读取标准输出
                    while (stdoutReader.ready()) {
                        var line = stdoutReader.readLine();
                        IOStreamx.writeLine(this.stdout, line, StandardCharsets.UTF_8);
                        this.notifyStdoutListener(line);
                    }

                    // 读取标准异输出
                    while (stderrReader.ready()) {
                        var line = stderrReader.readLine();
                        IOStreamx.writeLine(this.stderr, line, StandardCharsets.UTF_8);
                        this.notifyStderrListener(line);
                    }

                    if (channel.isClosed()) {
                        break;
                    }

                    // 等待一段时间继续执行
                    TimeUnit.MILLISECONDS.sleep(100);
                }
                return channel.getExitStatus();
            }
        } catch (JSchException ex) {
            throw new ShellException("执行命令出现异常: " + ex.getLocalizedMessage(), ex);
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
    }
}
