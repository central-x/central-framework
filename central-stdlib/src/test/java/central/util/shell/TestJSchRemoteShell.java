///*
// * MIT License
// *
// * Copyright (c) 2022-present Alan Yeh <alan@yeh.cn>
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all
// * copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.
// */
//
//package central.util.shell;
//
//import org.junit.jupiter.api.Test;
//
//import java.nio.file.Path;
//import java.time.Duration;
//
///**
// * JSchRemoteShell Test Cases
// *
// * @author Alan Yeh
// * @since 2022/12/05
// */
//public class TestJSchRemoteShell {
//
//    @Test
//    public void case1() throws Exception {
//        try (var shell = JSchRemoteShell.of("10.10.20.20", 22, "root", "x.123456")) {
//            shell.connect(Duration.ofSeconds(5));
//            shell.setWorkDir(Path.of("~/centralx"));
//            shell.exec("ls", "--all");
////            shell.exec("pod", "--version");
//
//            System.out.println(shell.getStdout());
//            System.err.println(shell.getStderr());
//        }
//    }
//
//    @Test
//    public void case2() throws Exception {
//        try (var shell = JSchRemoteShell.of("10.10.20.20", 22, "root", "x.123456")) {
//            shell.connect(Duration.ofSeconds(5));
//            shell.transferTo(Path.of("~/Documents/centralx"));
//
//            System.out.println(shell.getStdout());
//            System.err.println(shell.getStderr());
//        }
//    }
//
//    @Test
//    public void case3() throws Exception {
//        try (var shell = JSchRemoteShell.of("10.10.20.20", 22, "root", "x.123456")) {
//            shell.connect(Duration.ofSeconds(5));
//            shell.transferFrom(Path.of("~/centralx"), Path.of("~/Documents"));
//
//            System.out.println(shell.getStdout());
//            System.err.println(shell.getStderr());
//        }
//    }
//
//    @Test
//    public void case4() throws Exception {
//        try (var shell = JSchRemoteShell.of("10.10.20.20", 22, "root", "x.123456")) {
//            shell.connect(Duration.ofSeconds(5));
//            shell.rm(Path.of("~/centralx"));
//
//            System.out.println(shell.getStdout());
//            System.err.println(shell.getStderr());
//        }
//    }
//}
