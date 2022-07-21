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

package central.net.http.server.controller;

import central.net.http.server.controller.data.Account;
import central.net.http.server.controller.data.Dept;
import central.net.http.server.controller.data.Upload;
import central.net.http.server.controller.param.AccountParams;
import central.net.http.server.controller.param.FileParams;
import central.net.http.server.controller.param.IdsParams;
import central.net.http.server.controller.query.IdQuery;
import central.util.Guidx;
import central.util.Mapx;
import central.util.validate.group.Insert;
import central.util.validate.group.Update;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.groups.Default;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 测试用的接口
 *
 * @author Alan Yeh
 * @since 2022/07/15
 */
@RestController
@RequestMapping("/api")
public class IndexController {

    @GetMapping("/accounts")
    public Account getAccount(@Validated IdQuery query) {
        var dept = new Dept();
        dept.setId(Guidx.nextID());
        dept.setName("设计部");
        dept.updateCreator("super");

        var account = new Account();
        account.setId(query.getId());
        account.setName("张三");
        account.setAge(18);
        account.setDeptId(dept.getId());
        account.setDept(dept);
        account.updateCreator("super");

        return account;
    }

    @PostMapping("/accounts")
    public Account createAccount(@Validated({Default.class, Insert.class}) @RequestBody AccountParams params) {
        var account = new Account();
        account.setId(Guidx.nextID());
        account.setName(params.getName());
        account.setAge(params.getAge());
        account.updateCreator("super");
        return account;
    }

    @PutMapping("/accounts")
    public Account updateAccount(@Validated({Default.class, Update.class}) @RequestBody AccountParams params) {
        var account = new Account();
        account.setId(params.getId());
        account.setName(params.getName());
        account.setAge(params.getAge());
        account.updateCreator("super");
        account.updateModifier("super");
        return account;
    }

    @DeleteMapping("/accounts")
    public long deleteAccount(@Validated IdsParams params) {
        return params.getIds().size();
    }

    @GetMapping("/info")
    public Map<String, Object> info(HttpServletRequest request) {
        Map<String, Object> info = Mapx.newHashMap();
        // Headers
        var headers = Mapx.newHashMap();
        var headerIt = request.getHeaderNames();
        while (headerIt.hasMoreElements()) {
            var name = headerIt.nextElement();
            headers.put(name, request.getHeader(name));
        }
        info.put("headers", headers);

        // params
        var params = Mapx.newHashMap();
        var paramIt = request.getParameterNames();
        while (paramIt.hasMoreElements()) {
            var name = paramIt.nextElement();
            params.put(name, request.getParameter(name));
        }

        return info;
    }

    @PostMapping("/uploads")
    public Upload upload(@Validated FileParams params) {
        var upload = new Upload();
        upload.setName(params.getFile().getName());
        upload.setOriginalFilename(params.getFile().getOriginalFilename());
        upload.setSize(params.getFile().getSize());
        upload.setContentType(params.getFile().getContentType());
        return upload;
    }
}
