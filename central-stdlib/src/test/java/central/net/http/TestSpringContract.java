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

package central.net.http;

import central.io.Filex;
import central.net.http.executor.apache.ApacheHttpClientExecutor;
import central.net.http.processor.impl.AddHeaderProcessor;
import central.net.http.proxy.HttpProxyFactory;
import central.net.http.proxy.contract.spring.SpringContract;
import central.net.http.server.TestHttpApplication;
import central.net.http.server.controller.data.Account;
import central.net.http.server.controller.data.Upload;
import central.net.http.server.controller.param.AccountParams;
import central.util.Guidx;
import central.util.Mapx;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


/**
 * SpringContract Test Cases
 *
 * @author Alan Yeh
 * @since 2022/07/19
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = TestHttpApplication.class)
public class TestSpringContract {

    private Server server;

    @Value("${server.port}")
    protected Integer serverPort;

    @BeforeEach
    public void before() {
        this.server = HttpProxyFactory.builder(ApacheHttpClientExecutor.Default())
                .baseUrl("http://127.0.0.1:" + serverPort)
                .tmp(Path.of("cache").toFile())
                .contact(new SpringContract())
                .processor(new AddHeaderProcessor("X-Forwarded-Proto", "https"))
                .processor(new AddHeaderProcessor("X-Forwarded-Port", "443"))
                .target(Server.class);
    }

    @AfterEach
    public void after() throws Exception {
        // 删除缓存目录
        Filex.delete(Path.of("cache").toFile());
    }

    /**
     * Test GET
     */
    @Test
    public void case1() {
        var accountId = Guidx.nextID();

        var account = this.server.findById(accountId);
        assertNotNull(account);
        assertEquals(accountId, account.getId());
        assertNotNull(account.getDept());
    }

    /**
     * Test POST
     */
    @Test
    public void case2() {
        var params = new AccountParams();
        params.setAge(18);
        params.setName("张三");

        var account = this.server.create(params);
        assertNotNull(account);
        assertNotNull(account.getId());
        assertEquals(Integer.valueOf(18), account.getAge());
        assertEquals("张三", account.getName());
    }

    /**
     * Test PUT
     */
    @Test
    public void case3() {
        var params = new AccountParams();
        params.setId(Guidx.nextID());
        params.setAge(18);
        params.setName("张三");

        var account = this.server.update(params);
        assertNotNull(account);
        assertEquals(params.getId(), account.getId());
        assertEquals(params.getAge(), account.getAge());
        assertEquals(params.getName(), account.getName());
    }

    /**
     * Test DELETE
     */
    @Test
    public void case4() {
        var count = this.server.delete(List.of(Guidx.nextID(), Guidx.nextID()));
        assertEquals(2L, count);
    }

    /**
     * Test Processor
     */
    @Test
    public void case5() {
        var info = this.server.info();
        assertNotNull(info.get("headers"));
        var headers = Mapx.caseInsensitive((Map<String, Object>) info.get("headers"));
        assertEquals("https", headers.get("X-Forwarded-Proto"));
        assertEquals("443", headers.get("X-Forwarded-Port"));
    }

    /**
     * Test Upload
     */
    @Test
    public void case6() throws Exception {
        var file = new File("test.txt");
        try {
            assertTrue(file.createNewFile());
            Filex.writeText(file, "Hello World");

            var upload = this.server.upload(file);
            assertEquals("file", upload.getName());
            assertEquals("test.txt", upload.getOriginalFilename());
            assertEquals("Hello World".getBytes(StandardCharsets.UTF_8).length, upload.getSize());
            assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, upload.getContentType());
        } finally {
            Filex.delete(file);
        }
    }

    /**
     * Test Download
     */
    @Test
    public void case7() throws Exception {
        var file = this.server.download(Guidx.nextID());
        assertTrue(file.exists());
        assertEquals("test.txt", file.getName());
        var content = Filex.readText(file, StandardCharsets.UTF_8);
        assertEquals("This is the file content", content);
    }

    /**
     * Test default method
     */
    @Test
    public void case8() throws Exception {
        var result = this.server.testDefault();
        assertEquals("success", result);
    }

    @Test
    public void case9() throws Exception {
        var accountId = Guidx.nextID();

        var request = HttpRequest.get(HttpUrl.of("/api/accounts").setQuery("id", accountId));
        var account = this.server.findByRequest( request);

        assertNotNull(account);
        assertEquals(accountId, account.getId());
        assertNotNull(account.getDept());
    }

    @RequestMapping("/api")
    public interface Server {

        @GetMapping("/accounts")
        Account findById(@RequestParam String id);

        @GetMapping(value = "/accounts")
        Account findByRequest(HttpRequest request);

        @PostMapping(value = "/accounts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        Account create(@RequestBody AccountParams params);

        @PutMapping(value = "/accounts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
        Account update(@RequestBody AccountParams params);

        @DeleteMapping(value = "/accounts")
        long delete(@RequestParam List<String> ids);

        @GetMapping("/info")
        Map<String, Object> info();

        @PostMapping(value = "/uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        Upload upload(@RequestPart File file);

        @GetMapping(value = "/download")
        File download(@RequestParam String fileId);

        default String testDefault() {
            return "success";
        }
    }
}
