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

package central.sql.datasource.factory.druid;

import lombok.Data;

/**
 * Druid 数据源属性
 *
 * @author Alan Yeh
 * @since 2022/08/05
 */
@Data
public class DruidProperties {
    /**
     * 初始化时建立物理连接的个数, 默认[3]
     */
    private Integer initialSize = 3;
    /**
     * 最大连接池数量, 默认[50]
     */
    private Integer maxActive = 50;
    /**
     * 最小连接池数量, 默认[3]
     */
    private Integer minIdle = 3;
    /**
     * 获取连接时最大等待时间, 单位毫秒, 默认[5000]
     */
    private Long maxWait = 5000L;
    /**
     * ${testWhileIdle}的判断依据, 单位毫秒, 默认[90000]
     */
    private Long timeBetweenEvictionRunsMillis = 90000L;
    /**
     * 默认[1800000]
     */
    private Long minEvictableIdleTimeMillis = 1800000L;
    /**
     * 申请连接时执行${validationQuery}检测连接是否有效, 做了这个配置会降低性能, 默认[false]
     */
    private Boolean testOnBorrow = false;
    /**
     * 归还连接时执行${validationQuery}检测连接是否有效，做了这个配置会降低性能, 默认[false]
     */
    private Boolean testOnReturn = false;
    /**
     * 申请连接的时候检测, 如果空闲时间大于${timeBetweenEvictionRunsMillis}, 执行${validationQuery}检测连接是否有效
     */
    private Boolean testWhileIdle = false;
    /**
     * 是否缓存preparedStatement, 也就是PSCache. PSCache对支持游标的数据库性能提升巨大，比如说oracle。在mysql下建议关闭
     */
    private Boolean poolPreparedStatements = false;
    /**
     * 每个数据库连接最大缓存preparedStatement的数量, 默认[20]
     */
    private Integer maxPoolPreparedStatementPerConnectionSize = 20;
    /**
     * 最大打开preparedStatement的数量，默认[30]
     */
    private Integer maxOpenPreparedStatements = 30;
    /**
     * 用来检测连接是否有效的SQL。sqlite、mysql默认为[SELECT 1], oracle默认为[SELECT 1 FROM DUAL]
     */
    private String validationQuery;
}
