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

package central.starter.graphql;

import central.sql.data.Entity;

import java.util.List;

/**
 * GraphQL 服务
 *
 * @author Alan Yeh
 * @since 2022/09/09
 */
public interface GraphQLService {

    /**
     * GraphQL 命令空间
     */
    String getNamespace();

    /**
     * 注册类型
     * 将 DTO 和 GraphQL 里面的类型关联起来
     * 注意，这些 DTO 类型需要添加 @GraphQLType 注解
     */
    List<Class<? extends Entity>> getTypes();

    /**
     * GraphQL 分组
     * 分组后，会读取 resources/central/graphql/{group}/{service}.graphql 文件
     */
    default String getGroup() {
        return null;
    }
}
