package central.net.http;

import central.io.Filex;
import central.net.http.executor.java.JavaExecutor;
import central.net.http.processor.impl.AddHeaderProcessor;
import central.net.http.proxy.HttpProxyFactory;
import central.net.http.proxy.contract.spring.SpringContract;
import central.net.http.server.TestHttpApplication;
import central.net.http.server.controller.data.Account;
import central.net.http.server.controller.data.Upload;
import central.net.http.server.controller.param.AccountParams;
import central.util.Guidx;
import central.util.Mapx;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


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
        this.server = HttpProxyFactory.builder(JavaExecutor.Default())
                .baseUrl("http://127.0.0.1:" + serverPort)
                .contact(new SpringContract())
                .processor(new AddHeaderProcessor("X-Forwarded-Proto", "https"))
                .processor(new AddHeaderProcessor("X-Forwarded-Port", "443"))
                .target(Server.class);
    }

    /**
     * Test GET
     */
    @Test
    public void case1() {
        var accountId = Guidx.nextID();

        var account = this.server.findById(accountId);
        Assertions.assertNotNull(account);
        Assertions.assertEquals(accountId, account.getId());
        Assertions.assertNotNull(account.getDept());
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
        Assertions.assertNotNull(account);
        Assertions.assertNotNull(account.getId());
        Assertions.assertEquals(Integer.valueOf(18), account.getAge());
        Assertions.assertEquals("张三", account.getName());
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
        Assertions.assertNotNull(account);
        Assertions.assertEquals(params.getId(), account.getId());
        Assertions.assertEquals(params.getAge(), account.getAge());
        Assertions.assertEquals(params.getName(), account.getName());
    }

    /**
     * Test DELETE
     */
    @Test
    public void case4() {
        var count = this.server.delete(List.of(Guidx.nextID(), Guidx.nextID()));
        Assertions.assertEquals(2L, count);
    }

    /**
     * Test Processor
     */
    @Test
    public void case5() {
        var info = this.server.info();
        Assertions.assertNotNull(info.get("headers"));
        var headers = Mapx.caseInsensitiveMap((Map<String, Object>) info.get("headers"));
        Assertions.assertEquals("https", headers.get("X-Forwarded-Proto"));
        Assertions.assertEquals("443", headers.get("X-Forwarded-Port"));
    }

    /**
     * Test Upload
     */
    @Test
    public void case6() throws Exception {
        var file = new File("test.txt");
        try {
            Assertions.assertTrue(file.createNewFile());
            Filex.writeText(file, "Hello World");

            var upload = this.server.upload(file);
            Assertions.assertEquals("file", upload.getName());
            Assertions.assertEquals("test.txt", upload.getOriginalFilename());
            Assertions.assertEquals("Hello World".getBytes(StandardCharsets.UTF_8).length, upload.getSize());
            Assertions.assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, upload.getContentType());
        } finally {
            Filex.delete(file);
        }
    }

    @RequestMapping("/api")
    public interface Server {

        @GetMapping("/accounts")
        Account findById(@RequestParam String id);

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
    }
}
