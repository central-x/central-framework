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

package central.starter.probe.core;

import central.starter.probe.core.endpoint.Endpoint;
import central.starter.probe.core.secure.Authorizer;
import central.util.Mapx;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * 探针控制器
 *
 * @author Alan Yeh
 * @since 2023/12/27
 */
@Controller
@RequestMapping("/__probe")
public class ProbeController {
    @Setter(onMethod_ = @Autowired)
    private Authorizer secure;

    @Setter(onMethod_ = @Autowired(required = false))
    private Map<String, Endpoint> endpoints;

    /**
     * 探针入口地址
     */
    @GetMapping
    public ResponseEntity<Map<String, String>> index(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        // 验证保护器
        try {
            secure.authorize(authorization);
        } catch (ProbeException cause) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", cause.getLocalizedMessage()));
        }

        // 读取探针内容
        var result = new HashMap<String, String>();
        var status = HttpStatus.OK;
        if (Mapx.isNotEmpty(this.endpoints)) {
            for (var endpointEntry : this.endpoints.entrySet()) {
                try {
                    endpointEntry.getValue().perform();
                    result.put(endpointEntry.getKey(), "OK");
                } catch (Exception error) {
                    status = HttpStatus.INTERNAL_SERVER_ERROR;
                    result.put(endpointEntry.getKey(), error.getLocalizedMessage());
                }
            }
        }
        return ResponseEntity.status(status).body(result);
    }
}
