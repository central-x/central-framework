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

package central.starter.graphql.stub;

import central.sql.data.ModifiableEntity;
import central.validation.group.Update;
import central.web.XForwardedHeaders;
import jakarta.validation.groups.Default;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;

import java.io.Serializable;
import java.util.List;

/**
 * 可修改的代理
 *
 * @author Alan Yeh
 * @since 2022/10/25
 */
public interface ModifiableProvider<Data extends ModifiableEntity, Input extends Serializable> extends Provider<Data, Input> {
    /**
     * 更新数据
     *
     * @param input    数据输入
     * @param operator 操作人
     * @return 更新后的实体数据
     */
    Data update(@Validated({Update.class, Default.class}) Input input, String operator);

    Data update(@Validated({Update.class, Default.class}) Input input, String operator, @RequestHeader(XForwardedHeaders.TENANT) String tenant);

    /**
     * 批量更新数据
     *
     * @param inputs   数据输入
     * @param operator 操作人
     * @return 更新后的实体数据
     */
    List<Data> updateBatch(@Validated({Update.class, Default.class}) List<Input> inputs, String operator);

    List<Data> updateBatch(@Validated({Update.class, Default.class}) List<Input> inputs, String operator, @RequestHeader(XForwardedHeaders.TENANT) String tenant);

}
