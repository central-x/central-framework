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

package central.sql.datasource.migration;

import java.util.List;

/**
 * 迁移程序
 *
 * @author Alan Yeh
 * @since 2022/08/29
 */
public interface Database {
    /**
     * 数据库 URL
     */
    String getUrl();

    /**
     * 数据库产品名称
     */
    String getName();

    /**
     * 数据库版本
     */
    String getVersion();

    /**
     * 数据库驱动名
     */
    String getDriverName();

    /**
     * 数据库驱动版本
     */
    String getDriverVersion();

    /**
     * 获取表信息
     */
    List<Table> getTables();

    /**
     * 添加迁移动作
     * 不建议直接添加迁移动作，通过 #getTable 方法获取到表结构之后，通过该表进行迁移会更准确一些
     *
     * @param action 迁移动作
     */
    void addAction(MigrateAction action);

    /**
     * 添加表信息
     *
     * @param table 表信息
     */
    void addTable(Table table);

    /**
     * 获取表信息
     */
    Table getTable(String name);

    /**
     * 销毁数据库
     * 慎用
     */
    void drop();
}
