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

package central.data;

import java.sql.Timestamp;

/**
 * Modifiable Entity
 * 可修改的实体
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
public interface Modifiable {
    /**
     * 获取修改人唯一标识
     */
    String getModifierId();

    /**
     * 设置修改人唯一标识
     *
     * @param modifierId 修改人唯一标识
     */
    void setModifierId(String modifierId);

    /**
     * 获取修改日期
     */
    Timestamp getModifyDate();

    /**
     * 设置修改日期
     *
     * @param modifyDate 修改日期
     */
    void setModifyDate(Timestamp modifyDate);

    /**
     * 更新实体修改人唯一标识和修改时间
     *
     * @param modifierId 修改人唯一标识
     */
    default void updateModifier(String modifierId) {
        this.setModifierId(modifierId);
        this.setModifyDate(new Timestamp(System.currentTimeMillis()));
    }
}
