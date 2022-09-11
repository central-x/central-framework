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

package central.starter.graphql.test;

import central.net.http.executor.java.JavaExecutor;
import central.net.http.proxy.HttpProxyFactory;
import central.net.http.proxy.contract.spring.SpringContract;
import central.sql.Conditions;
import central.starter.graphql.TestApplication;
import central.starter.graphql.test.data.Account;
import central.util.Mapx;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GraphQL Test Cases
 *
 * @author Alan Yeh
 * @since 2022/09/09
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = TestApplication.class)
public class TestGraphQL {

    @Value("${server.port}")
    private int port;

    private GraphQLClient client;

    @BeforeEach
    public void before() {
        this.client = HttpProxyFactory.builder(JavaExecutor.Default())
                .baseUrl("http://127.0.0.1:" + port)
                .contact(new SpringContract())
                .target(GraphQLClient.class);
    }


    /**
     * 测试 DataLoader、GraphQLBean 注入等
     */
    @Test
    public void test1() {
        String graphql = """
                query AccountProvider($ids: [String]) {
                    result: Account_findByIds(ids: $ids) {
                        id
                        name
                        deptId
                        dept {
                            id
                            name
                            accounts {
                                id
                                name
                                deptId
                            }
                        }
                    }
                }""";

        List<String> ids = Arrays.asList("1", "2", "3");

        GraphqlResult<List<Account>> result = this.client.query(graphql, Mapx.newHashMap("ids", ids), "test");

        System.out.println(result.getResult());
    }

    /**
     * 测试请求头、默认值、参数转换、SpringBean 注入
     */
    @Test
    public void test2() {
        String graphql = """
                query AccountProvider($first: Int, $offset: Int, $conditions: [ConditionInput], $orders: [OrderInput]) {
                    result: Account_findBy(first: $first, offset: $offset, conditions: $conditions, orders: $orders) {
                        id
                        name
                        deptId
                        dept {
                            id
                            name
                            accounts {
                                id
                                name
                                deptId
                            }
                        }
                    }
                }""";

        Map<String, Object> variables = new HashMap<>();
        variables.put("first", 1);
        variables.put("conditions", Conditions.where().eq("name", "张三"));

        GraphqlResult<List<Account>> result = this.client.query(graphql, variables, "test");

        System.out.println(result.getResult());
    }
}
