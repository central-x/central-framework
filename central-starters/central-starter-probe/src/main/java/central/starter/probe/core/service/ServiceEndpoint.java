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

package central.starter.probe.core.service;

import central.lang.Stringx;
import central.lang.reflect.TypeRef;
import central.net.http.HttpClient;
import central.net.http.HttpRequest;
import central.net.http.HttpResponse;
import central.net.http.HttpUrl;
import central.net.http.body.extractor.StringExtractor;
import central.net.http.executor.java.JavaExecutor;
import central.starter.probe.core.Endpoint;
import central.starter.probe.core.ProbeException;
import central.util.Jsonx;
import central.util.Listx;
import central.util.Mapx;
import central.util.Range;
import central.validation.Label;
import central.validation.Validatex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * 服务探针
 *
 * @author Alan Yeh
 * @since 2023/12/29
 */
@Slf4j
public class ServiceEndpoint implements Endpoint, BeanNameAware, InitializingBean {

    @Setter
    private String beanName;

    @Setter
    @NotBlank
    @Size(max = 128)
    @Label("请求方法")
    private String method = "GET";

    @Setter
    @NotBlank
    @Size(max = 2048)
    @Label("访问地址")
    private String url;

    /**
     * 0 -> name, value
     * <p>
     * 1 -> name, value
     */
    @Setter
    @Label("请求头")
    private Map<String, Map<String, String>> headers;

    @Setter
    @Label("期望结果")
    private Map<String, Object> expects;

    @Setter
    @NotNull
    @Label("超时时间（毫秒）")
    private Long timeout = 5000L;

    private HttpClient client;

    /**
     * 期望响应状态码
     */
    private List<Integer> expectedStatus;

    /**
     * 期望内容
     */
    private String expectedContent;

    @Override
    public void afterPropertiesSet() throws Exception {
        Validatex.Default().validate(this);
        try {
            new URI(this.url);
        } catch (URISyntaxException error) {
            throw new IllegalArgumentException("无效的 URL 格式: " + this.url);
        }

        // 解析响应期望
        if (Mapx.isNotEmpty(this.expects)) {
            // 期望响应状态码
            var status = this.expects.get("status");
            if (status instanceof Map<?, ?> map) {
                // expects:
                //   status: [200, 403]
                this.expectedStatus = new ArrayList<>();
                map.values().stream().map(it -> {
                    try {
                        return Integer.parseInt(it.toString());
                    } catch (NumberFormatException ex) {
                        throw new IllegalArgumentException("不是有效的状态码: " + it);
                    }
                }).forEach(this.expectedStatus::add);
            } else if (status instanceof String str) {
                if ("ok".equalsIgnoreCase(str)) {
                    for (var it : Range.of(200, 299)) {
                        this.expectedStatus.add(it.intValue());
                    }
                } else {
                    throw new IllegalArgumentException("不支持状态码: " + str);
                }
            }
            // 期望响应内容
            var content = this.expects.get("content");
            if (content != null) {
                this.expectedContent = content.toString().trim();
            }
        }

        // 构建 Client
        this.client = new HttpClient(JavaExecutor.builder().connectTimeout(this.timeout).build());
    }

    @Override
    @SneakyThrows
    public void perform() throws ProbeException {
        var request = HttpRequest.of(HttpMethod.valueOf(this.method.toUpperCase()), HttpUrl.of(this.url));
        if (Mapx.isNotEmpty(this.headers)) {
            for (var index : this.headers.entrySet()) {
                request.addHeader(index.getValue().get("name"), index.getValue().get("value"));
            }
        }

        HttpStatus status = null;
        String error = null;
        HttpResponse response = null;
        String content = null;

        do {
            try {
                response = this.client.execute(request);
            } catch (TimeoutException ex) {
                status = HttpStatus.REQUEST_TIMEOUT;
                error = ex.getLocalizedMessage();
                break;
            } catch (Exception ex) {
                error = ex.getLocalizedMessage();
                break;
            }

            // 如果有期望状态码，则以用户期望为准
            if (Listx.isNotEmpty(this.expectedStatus)) {
                if (!this.expectedStatus.contains(response.getStatus().value())) {
                    error = Stringx.format("执行请求 [GET {}] 失败: 返回非期望状态码[{} {}]", this.url, response.getStatus().value(), response.getStatus().getReasonPhrase());
                    break;
                }
            } else {
                // 否则以状态码是否在 200 ～ 299 的区间来判断是否成功
                if (!response.isSuccess()) {
                    error = Stringx.format("执行请求 [GET {}] 失败: 返回非成功状态码[{} {}]", this.url, response.getStatus().value(), response.getStatus().getReasonPhrase());
                    break;
                }
            }

            // 如果有期望响应体，则以需要解析响应体，并匹配内容
            if (Stringx.isNotBlank(this.expectedContent)) {
                if (MediaType.APPLICATION_JSON.isCompatibleWith(response.getHeaders().getContentType())) {
                    content = response.getBody().extract(StringExtractor.of());
                    // 如果响应是 application/json 的话，则解析后匹配
                    if (this.expectedContent.startsWith("[") && content.startsWith("[")) {
                        // JSON 数组
                        var expectedList = Jsonx.Default().deserialize(this.expectedContent, TypeRef.ofList(Object.class));
                        var responseList = Jsonx.Default().deserialize(content, TypeRef.ofList(Object.class));
                        content = Jsonx.Default().serialize(responseList, true);

                        if (!this.isDeepEquals(expectedList, responseList)) {
                            error = Stringx.format("执行请求 [GET {}] 失败: 返回非期望响应体", this.url);
                            break;
                        }

                    }
                    if (this.expectedContent.startsWith("{") && content.startsWith("{")) {
                        // JSON 对象
                        var expectedMap = Jsonx.Default().deserialize(this.expectedContent, TypeRef.ofMap(String.class, Object.class));
                        var responseMap = Jsonx.Default().deserialize(content, TypeRef.ofMap(String.class, Object.class));
                        content = Jsonx.Default().serialize(responseMap, true);

                        if (!this.isDeepEquals(expectedMap, responseMap)) {
                            error = Stringx.format("执行请求 [GET {}] 失败: 返回非期望响应体", this.url);
                            break;
                        }
                    }
                }
            }
        } while (false);


        var builder = new StringBuilder("┏━━━━━━━━━━━━━━━━━━ Probe ━━━━━━━━━━━━━━━━━━━\n");
        builder.append("┣ Endpoint: ").append(this.beanName).append("\n");
        builder.append("┣ Type: ").append("Service\n");
        builder.append("┣ Params:\n");
        builder.append("┣ - url: ").append(this.url).append("\n");
        builder.append("┣ - timeout: ").append(this.timeout).append("\n");
        if (Mapx.isNotEmpty(this.headers)) {
            builder.append("┣ - headers:\n");
            for (var index : this.headers.entrySet()) {
                builder.append("┣    - ").append(index.getValue().get("name")).append(": ").append(index.getValue().get("value")).append("\n");
            }
        }
        if (Mapx.isNotEmpty(this.expects)) {
            builder.append("┣ - expected:\n");
            if (Listx.isNotEmpty(this.expectedStatus)) {
                builder.append("┣    - status: [").append(this.expectedStatus.stream().map(Object::toString).collect(Collectors.joining(", "))).append("]\n");
            }
            if (Stringx.isNotBlank(this.expectedContent)) {
                builder.append("┣    - content: ").append(this.expectedContent.replace("\n", "").replace("\r\n", "")).append("\n");
            }
        }
        builder.append("┣ Result: \n");
        builder.append("┣ - status: ").append(response.getStatus().value()).append("(").append(response.getStatus().getReasonPhrase()).append(")").append("\n");
        if (Mapx.isNotEmpty(response.getHeaders())) {
            builder.append("┣ - headers: \n");
            for (var entry : response.getHeaders().entrySet()) {
                for (var value : entry.getValue()) {
                    builder.append("┣    - ").append(entry.getKey()).append(": ").append(value).append("\n");
                }
            }
        }
        if (Stringx.isNotBlank(content)) {
            builder.append("┣ - content: \n").append("┣ ").append(content.replace("\n", "\n┣ ")).append("\n");
        }
        builder.append("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        if (Stringx.isNotBlank(error)) {
            log.error(builder.toString());
            throw new ProbeException(error);
        } else {
            log.info(builder.toString());
        }
    }

    private boolean isDeepEquals(List<?> source, List<?> target) {
        if (source.size() != target.size()) {
            return false;
        }

        var sourceSet = new HashSet<>(source);
        var targetSet = new HashSet<>(target);

        return sourceSet.equals(targetSet);
    }

    private boolean isDeepEquals(Map<?, ?> source, Map<?, ?> target) {
        if (source.size() != target.size()) {
            return false;
        }

        for (var sourceEntry : source.entrySet()) {
            var sourceValue = sourceEntry.getValue();
            var targetValue = target.get(sourceEntry.getKey());
            if (sourceValue != null) {
                if (targetValue == null) {
                    return false;
                }
                if (sourceValue instanceof Map<?, ?> sourceValueMap && targetValue instanceof Map<?, ?> targetValueMap) {
                    if (!isDeepEquals(sourceValueMap, targetValueMap)) {
                        return false;
                    }
                }
                if (sourceValue instanceof List<?> sourceValueList && targetValue instanceof List<?> targetValueList) {
                    if (!isDeepEquals(sourceValueList, targetValueList)) {
                        return false;
                    }
                }
            } else {
                if (targetValue != null) {
                    return false;
                }
            }
        }
        return true;
    }
}
