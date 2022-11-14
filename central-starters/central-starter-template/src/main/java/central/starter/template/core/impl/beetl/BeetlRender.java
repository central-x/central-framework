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

package central.starter.template.core.impl.beetl;

import central.lang.Stringx;
import central.starter.template.core.TemplateRender;
import lombok.SneakyThrows;
import org.beetl.core.Configuration;
import org.beetl.core.ErrorHandler;
import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.StringTemplateResourceLoader;

import java.util.Map;
import java.util.Properties;

/**
 * Beetl 实现
 *
 * @author Alan Yeh
 * @since 2022/11/14
 */
public class BeetlRender implements TemplateRender {

    private final GroupTemplate groupTemplate;

    @SneakyThrows
    public BeetlRender(Properties properties) {
        if (Stringx.isNullOrBlank(properties.getProperty("ERROR_HANDLER"))) {
            properties.setProperty("ERROR_HANDLER", ErrorHandler.class.getName());
        }
        this.groupTemplate = new GroupTemplate(new StringTemplateResourceLoader(), new Configuration(properties));
    }

    @SneakyThrows
    public BeetlRender() {
        var properties = new Properties();
        properties.load(BeetlRender.class.getClassLoader().getResourceAsStream("central/template/beetl.properties"));
        var configuration = new Configuration(properties);
        this.groupTemplate = new GroupTemplate(new StringTemplateResourceLoader(), configuration);
    }

    @Override
    public String render(String template, Map<String, Object> params) {
        var render = this.groupTemplate.getTemplate(template);
        render.binding(params);
        return render.render();
    }
}
