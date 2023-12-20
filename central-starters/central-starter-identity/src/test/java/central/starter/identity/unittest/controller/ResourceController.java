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

package central.starter.identity.unittest.controller;

import central.starter.identity.unittest.controller.data.Resource;
import central.starter.identity.unittest.controller.data.ResourceInput;
import central.starter.web.param.IdsParams;
import central.util.Guidx;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Resource Controller
 * 资源控制器
 *
 * @author Alan Yeh
 * @since 2023/02/13
 */
@RestController
@RequiresPermissions(ResourceController.Permissions.VIEW)
@RequestMapping("/api/resources")
public class ResourceController {
    public interface Permissions {
        String VIEW = "unittest:resources:view";
        String ADD = "unittest:resources:add";
        String EDIT = "unittest:resources:edit";
        String DELETE = "unittest:resources:delete";
    }

    @GetMapping
    public List<Resource> findBy() {
        var resource = new Resource();
        resource.setId(Guidx.nextID());
        resource.updateCreator("syssa");
        return List.of(resource);
    }

    @PostMapping
    @RequiresPermissions(Permissions.ADD)
    public Resource insert(@RequestBody @Validated ResourceInput input) {
        return null;
    }

    @PutMapping
    @RequiresPermissions(Permissions.EDIT)
    public Resource update(@RequestBody @Validated ResourceInput input) {
        return null;
    }

    @DeleteMapping
    @RequiresPermissions(Permissions.DELETE)
    public long delete(IdsParams params) {
        return 1;
    }
}
