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

package central.starter.security.unittest.test;

import central.net.http.executor.java.JavaExecutor;
import central.net.http.processor.impl.AddHeaderProcessor;
import central.net.http.proxy.HttpProxyFactory;
import central.net.http.proxy.contract.spring.SpringContract;
import central.starter.security.unittest.TestApplication;
import central.starter.security.unittest.controller.ResourceController;
import central.starter.security.unittest.controller.data.ResourceInput;
import central.util.Guidx;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Permissions Test Cases
 * 权限测试
 *
 * @author Alan Yeh
 * @since 2023/02/13
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = TestApplication.class)
public class TestController {

    @Value("${server.port}")
    private int port;

    @Test
    void case1() {
        var permissions = String.join(",", ResourceController.Permissions.VIEW, ResourceController.Permissions.EDIT);
        var jwt = JWT.create()
                .withClaim("permissions", permissions)
                .sign(Algorithm.HMAC256("test"));

        var client = HttpProxyFactory.builder(JavaExecutor.Default())
                .baseUrl("http://127.0.0.1:" + port)
                .contact(new SpringContract())
                .processor(new AddHeaderProcessor("Authorization", jwt))
                .log()
                .target(ResourceClient.class);

        var resources = client.findBy();

        Assertions.assertEquals(1, resources.size());
    }

    @Test
    void case2() {
        var permissions = String.join(",", ResourceController.Permissions.ADD, ResourceController.Permissions.EDIT);
        var jwt = JWT.create()
                .withClaim("permissions", permissions)
                .sign(Algorithm.HMAC256("test"));

        var client = HttpProxyFactory.builder(JavaExecutor.Default())
                .baseUrl("http://127.0.0.1:" + port)
                .contact(new SpringContract())
                .processor(new AddHeaderProcessor("Authorization", jwt))
                .log()
                .target(ResourceClient.class);

        Assertions.assertThrows(IllegalArgumentException.class,() -> client.findBy());
    }

    @Test
    void case3() {
        var permissions = String.join(",", ResourceController.Permissions.ADD, ResourceController.Permissions.EDIT);
        var jwt = JWT.create()
                .withClaim("permissions", permissions)
                .sign(Algorithm.HMAC256("test"));

        var client = HttpProxyFactory.builder(JavaExecutor.Default())
                .baseUrl("http://127.0.0.1:" + port)
                .contact(new SpringContract())
                .processor(new AddHeaderProcessor("Authorization", jwt))
                .log()
                .target(ResourceClient.class);

        Assertions.assertThrows(IllegalArgumentException.class,() -> client.insert(new ResourceInput(Guidx.nextID())));
    }
}
