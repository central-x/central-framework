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

import central.lang.reflect.TypeReference;
import central.net.http.body.extractor.JsonExtractor;
import central.net.http.body.request.JsonBody;
import central.net.http.body.request.MultipartFormBody;
import central.net.http.body.request.MultipartFormPart;
import central.net.http.processor.impl.AddHeaderProcessor;
import central.net.http.server.TestHttpApplication;
import central.net.http.server.controller.data.Account;
import central.net.http.server.controller.data.Upload;
import central.util.Guidx;
import central.util.Mapx;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Http Test Cases
 *
 * @author Alan Yeh
 * @since 2022/07/18
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = TestHttpApplication.class)
public abstract class TestHttp {
    protected HttpClient client;

    @Value("${server.port}")
    protected Integer serverPort;

    @BeforeEach
    public void before() {
        this.client = new HttpClient(this.getExecutor());
        this.client.setBaseUrl("http://127.0.0.1:" + this.serverPort);
    }

    protected abstract HttpExecutor getExecutor();

    /**
     * Test GET
     */
    @Test
    public void case1() throws Throwable {
        String accountId = Guidx.nextID();
        try (var request = HttpRequest.get(HttpUrl.create("/api/accounts").setQuery("id", accountId)).build()) {
            var response = this.client.execute(request);
            Assertions.assertEquals(HttpStatus.OK, response.getStatus());

            var account = response.getBody().extract(new JsonExtractor<>(TypeReference.of(Account.class)));
            Assertions.assertNotNull(account);
            Assertions.assertEquals(accountId, account.getId());
            Assertions.assertNotNull(account.getDept());
        }
    }

    /**
     * Test POST
     */
    @Test
    public void case2() throws Throwable {
        try (var request = HttpRequest.post(HttpUrl.create("/api/accounts")).build()) {
            Map<String, Object> body = Mapx.newHashMap();
            body.put("age", 18);
            body.put("name", "张三");
            request.setBody(new JsonBody(body));

            var response = this.client.execute(request);
            Assertions.assertEquals(HttpStatus.OK, response.getStatus());

            var account = response.getBody().extract(new JsonExtractor<>(TypeReference.of(Account.class)));
            Assertions.assertNotNull(account);
            Assertions.assertNotNull(account.getId());
            Assertions.assertEquals(Integer.valueOf(18), account.getAge());
            Assertions.assertEquals("张三", account.getName());
        }
    }

    /**
     * Test PUT
     */
    @Test
    public void case3() throws Throwable {
        try (var request = HttpRequest.put(HttpUrl.create("/api/accounts")).build()) {
            Map<String, Object> body = Mapx.newHashMap();
            body.put("id", Guidx.nextID());
            body.put("age", 18);
            body.put("name", "张三");
            request.setBody(new JsonBody(body));

            var response = this.client.execute(request);
            Assertions.assertEquals(HttpStatus.OK, response.getStatus());

            var account = response.getBody().extract(new JsonExtractor<>(TypeReference.of(Account.class)));
            Assertions.assertNotNull(account);
            Assertions.assertEquals(body.get("id"), account.getId());
            Assertions.assertEquals(Integer.valueOf(18), account.getAge());
            Assertions.assertEquals("张三", account.getName());
        }
    }

    /**
     * Test DELETE
     */
    @Test
    public void case4() throws Throwable {
        try (var request = HttpRequest.delete(HttpUrl.create("/api/accounts").addQuery("ids", Guidx.nextID()).addQuery("ids", Guidx.nextID())).build()) {
            var response = this.client.execute(request);

            Assertions.assertEquals(HttpStatus.OK, response.getStatus());

            var count = response.getBody().extract(new JsonExtractor<>(TypeReference.of(Integer.class)));
            Assertions.assertNotNull(count);
            Assertions.assertEquals(Integer.valueOf(2), count);
        }
    }

    /**
     * Test Processor
     */
    @Test
    public void case5() throws Throwable {
        try (var request = HttpRequest.get(HttpUrl.create("/api/info")).build()) {
            this.client.addProcessor(new AddHeaderProcessor("X-Forwarded-Proto", "https"));
            this.client.addProcessor(new AddHeaderProcessor("X-Forwarded-Port", "443"));

            var response = this.client.execute(request);

            Assertions.assertEquals(HttpStatus.OK, response.getStatus());

            var info = response.getBody().extract(new JsonExtractor<>(TypeReference.ofMap(String.class, Object.class)));
            Assertions.assertNotNull(info.get("headers"));
            var headers = Mapx.caseInsensitive((Map<String, Object>) info.get("headers"));
            Assertions.assertEquals("https", headers.get("X-Forwarded-Proto"));
            Assertions.assertEquals("443", headers.get("X-Forwarded-Port"));
        }
    }

    /**
     * Test Upload
     */
    @Test
    public void case6() throws Throwable {
        try (var request = HttpRequest.post(HttpUrl.create("/api/uploads")).build()) {
            var body = new MultipartFormBody();
            body.add(MultipartFormPart.create("file", "test.txt", "Hello World".getBytes(StandardCharsets.UTF_8), MediaType.TEXT_PLAIN));
            request.setBody(body);

            var response = this.client.execute(request);

            Assertions.assertEquals(HttpStatus.OK, response.getStatus());
            var upload = response.getBody().extract(new JsonExtractor<>(TypeReference.of(Upload.class)));
            Assertions.assertEquals("file", upload.getName());
            Assertions.assertEquals("test.txt", upload.getOriginalFilename());
            Assertions.assertEquals("Hello World".getBytes(StandardCharsets.UTF_8).length, upload.getSize());
            Assertions.assertEquals(MediaType.TEXT_PLAIN.toString(), upload.getContentType());
        }
    }
}
