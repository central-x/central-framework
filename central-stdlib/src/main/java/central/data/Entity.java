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

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 数据实体
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
@Data
public class Entity implements Identifiable, Serializable {
    @Serial
    private static final long serialVersionUID = 3246576373725082312L;

    /**
     * 唯一标识
     */
    private String id;

    /**
     * 创建人唯一标识
     */
    private String creatorId;

    /**
     * 实体创建时间
     */
    private Timestamp createDate;

    /**
     * 初始化实体的创建人唯一标识与创建时间
     *
     * @param creatorId 创建人唯一标识
     */
    public void updateCreator(String creatorId) {
        this.creatorId = creatorId;
        this.createDate = new Timestamp(System.currentTimeMillis());
    }
}
