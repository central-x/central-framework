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
import central.lang.Stringx;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.keyverifier.AcceptAllServerKeyVerifier;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.util.security.SecurityUtils;
import org.apache.sshd.common.util.security.eddsa.EdDSASecurityProviderRegistrar;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpClientFactory;
import org.apache.sshd.sftp.client.fs.SftpFileSystem;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.KeyPair;
import java.time.Duration;
import java.util.*;

/**
 * 远程 Shell
 * <p>
 * 这是一个非交互式的 Shell，每次命令都是独立再次连到服务器去执行的
 * <p>
 * 例：
 * <pre>
 * {@code try(var shell = RemoteShell.of("10.10.20.20", "root", "x.123456")) {
 *     shell.connect(Duration.ofSeconds(5);
 *     // 执行命令
 *     shell.exec("java", "--version");
 *     // 将本地文件传输到服务器
 *     shell.transferTo(localFile, remotePath);
 *     // 将服务器文件传输到本地
 *     shell.transferFrom(remoteFile, localPath);
 * }}
 * </pre>
 *
 * @author Alan Yeh
 * @since 2022/11/25
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RemoteShell extends Shell {
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
    public static RemoteShell of(String host, String username, String password) {
        return new RemoteShell(host, 22, username, password, null);
    }

    /**
     * 创建远程 Shell
     *
     * @param host     主机名
     * @param port     端口
     * @param username 用户名
     * @param password 密码
     */
    public static RemoteShell of(String host, int port, String username, String password) {
        return new RemoteShell(host, port, username, password, null);
    }

    /**
     * 使用默认端口创建远程 Shell
     *
     * @param host      主机名
     * @param username  用户名
     * @param publicKey 认证密钥（ssh public key）
     */
    public static RemoteShell of(String host, int port, String username, KeyPair publicKey) {
        return new RemoteShell(host, port, username, null, publicKey);
    }

    /**
     * 创建远程 Shell
     *
     * @param host      主机名
     * @param username  用户名
     * @param publicKey 认证密钥（ssh public key）
     */
    public static RemoteShell of(String host, String username, KeyPair publicKey) {
        return new RemoteShell(host, 22, username, null, publicKey);
    }

    private SshClient client;

    private ClientSession session;

    private SftpClient sftp;

    private SftpFileSystem fs;

    @Override
    public void connect(Duration timeout) throws ShellException {
        SecurityUtils.registerSecurityProvider(new EdDSASecurityProviderRegistrar());
        this.client = SshClient.setUpDefaultClient();
        this.client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);
        this.client.start();

        try {
            // 连接到服务器，创建会话
            this.session = this.client.connect(this.username, this.host, this.port).verify(timeout).getSession();
        } catch (IOException ex) {
            throw new ShellException(Stringx.format("无法连接到服务器（{}@{}:{}）: {}", this.username, this.host, this.port, ex.getLocalizedMessage()), ex);
        }
        if (Stringx.isNotBlank(this.password)) {
            // 使用密码验证
            this.session.addPasswordIdentity(this.password);
        }
        if (this.publicKey != null) {
            // 通过密钥验证
            this.session.addPublicKeyIdentity(this.publicKey);
        }
        // 判断是否连接成功
        try {
            // 验证会话
            Assertx.mustTrue(this.session.auth().verify(timeout).isSuccess(), ShellException::new, Stringx.format("无法连接到服务器（{}@{}:{}）", this.username, this.host, this.port));
        } catch (IOException ex) {
            throw new ShellException(Stringx.format("无法连接到服务器（{}@{}:{}）: {}", this.username, this.host, this.port, ex.getLocalizedMessage()), ex);
        }

        // 创建文件访问客户端
        try {
            this.sftp = SftpClientFactory.instance().createSftpClient(this.session).singleSessionInstance();
            this.fs = SftpClientFactory.instance().createSftpFileSystem(this.session);

            // 处理 workDir
            this.workDir = this.toRemoteAbsolute(this.workDir);
        } catch (IOException ex) {
            throw new ShellException(Stringx.format("服务器（{}@{}:{}）创建 sftp 失败: {}", this.username, this.host, this.port, ex.getLocalizedMessage()), ex);
        }
    }

    private Path toLocalAbsolute(Path path) {
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

    private Path toRemoteAbsolute(Path path) {
        if (path.isAbsolute()) {
            return this.fs.getPath(path.toString());
        } else {
            switch (path.getName(0).toString()) {
                case "~", "." -> {
                    if (path.getNameCount() == 1) {
                        return this.fs.getDefaultDir();
                    } else {
                        return this.fs.getDefaultDir().resolve(path.subpath(1, path.getNameCount()).toString());
                    }
                }
                case ".." -> {
                    if (path.getNameCount() == 1) {
                        return this.fs.getDefaultDir().getParent();
                    } else {
                        return this.fs.getDefaultDir().getParent().resolve(path.subpath(1, path.getNameCount()).toString());
                    }
                }
                default -> {
                    return this.fs.getPath(path.toString());
                }
            }
        }
    }

    @Override
    public boolean isConnected() {
        return this.client != null && this.session != null && this.session.isOpen();
    }

    @Override
    public void disconnect() throws ShellException {
        try {
            if (this.fs != null) {
                this.fs.close();
                this.fs = null;
            }
            if (this.sftp != null) {
                this.sftp.close();
                this.sftp = null;
            }
            if (this.session != null) {
                this.session.disconnect(0, "Disconnect");
                this.session = null;
            }
            if (this.client != null) {
                this.client.stop();
                this.client = null;
            }
            this.stdout.reset();
            this.stderr.reset();
        } catch (IOException ex) {
            throw new ShellException(ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    @SneakyThrows(IOException.class)
    public boolean rm(Path remoteFile) throws ShellException {
        remoteFile = toRemoteAbsolute(remoteFile);
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
            Files.createDirectories(toRemoteAbsolute(remotePath));
            return true;
        } catch (IOException ex) {
            throw new ShellException("创建文件夹失败: " + ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    public boolean transferTo(Path localFile) throws ShellException {
        return transferTo(localFile, this.workDir);
    }

    @Override
    public boolean transferTo(Path localFile, Path remotePath) throws ShellException {
        localFile = this.toLocalAbsolute(localFile);
        remotePath = this.toRemoteAbsolute(remotePath);
        return this.transfer(localFile, remotePath);
    }

    @Override
    public boolean transferFrom(Path remoteFile, Path localPath) throws ShellException {
        remoteFile = this.toRemoteAbsolute(remoteFile);
        localPath = this.toLocalAbsolute(localPath);
        return this.transfer(remoteFile, localPath);
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
    @SneakyThrows(IOException.class)
    public int exec(String command, String... args) throws ShellException {
        var commandBuilder = new StringBuilder(this.process(command));
        for (var arg : args) {
            commandBuilder.append(" ").append(process(arg));
        }

        IOStreamx.writeLine(this.stdout, "$ " + commandBuilder, this.charset);
        this.notifyStdoutListener("$ " + commandBuilder);

        try (var channel = session.createExecChannel(commandBuilder.toString())) {
            this.environments.forEach(channel::setEnv);
            channel.setOut(this.stdout);
            channel.setErr(this.stderr);
            channel.open().verify(Duration.ofSeconds(10));
            channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), 0L);
            return 0;
        } catch (IOException ex) {
            throw new ShellException(Stringx.format("在服务器（{}@{}:{}）执行命令[{}]出错: " + ex.getLocalizedMessage(), this.username, this.host, this.port), ex);
        }
    }
}
