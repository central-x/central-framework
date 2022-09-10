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

package central.sql.datasource.factory.hikari;

import lombok.Data;

import java.lang.reflect.Modifier;

/**
 * Hikari 数据源配置
 *
 * @author Alan Yeh
 * @since 2022/08/05
 */
@Data
public class HikariProperties {
    /**
     * 控制从池返回的连接的默认自动提交行为, 默认[true]
     */
    private Boolean autoCommit = true;

    /**
     * 此属性控制客户端等待池中连接的最大毫秒数,默认[30000]
     */
    private Long connectionTimeout = 30000L;

    /**
     * 控制允许连接在池中处于空闲状态的最长时间 （此设置仅在大于minimumIdle（最小空闲数）小于maximumPoolSize（池大小）时才适用，单位毫秒。默认[600000]
     */
    private Long idleTimeout = 600000L;

    /**
     * 控制池中连接的最长生命周期。强烈建议您设置此值，它应比任何数据库或基础结构强加的连接时间限制短几秒。单位毫秒，默认[1800000]
     */
    private Long maxLifetime = 1800000L;

    /**
     * 用来检测连接是否有效的SQL。
     */
    private String connectionTestQuery;

    /**
     * 最小空闲连接数, 默认与maximumPoolSize相同
     */
    private Integer minimumIdle;

    /**
     * 最大连接池数量，默认[10]
     */
    private Integer maximumPoolSize = 10;

    /**
     * 连接池的用户定义名称，主要显示在日志记录和JMX管理控制台中，以标识池和池配置。默认自动生成
     */
    private String poolName;

    /**
     * 是否在其自己的事务中隔离内部池查询，例如连接活动测试。默认[false]
     */
    private Boolean isolateInternalQueries = false;

    /**
     * 如果池无法成功初始化连接，则此属性控制池是否“快速失败”。
     * 任何正数都被认为是尝试获取初始连接的毫秒数; 在此期间，应用程序线程将被阻止。
     * 如果在此超时发生之前无法获取连接，则将引发异常。此超时被应用后的connectionTimeout 期。如果值为零（0），HikariCP将尝试获取并验证连接。
     * 如果获得连接但验证失败，则将引发异常并且池未启动。但是，如果无法获得连接，则池将启动，但稍后获取连接的努力可能会失败。
     * 小于零的值将绕过任何初始连接尝试，并且池将在尝试在后台获取连接时立即启动。因此稍后获得连接的努力可能失败。
     * 默认[1]
     */
    private Long initializationFailTimeout = 1L;

    /**
     * 该属性设置一个SQL语句，在将每个新连接创建后，将其添加到池中之前执行该语句。
     */
    private String connectionInitSql;

    /**
     * 控制连接测试活动的最长时间。该值必须小于connectionTimeout。最低可接受的验证超时为250毫秒。默认[5000]
     */
    private Long validationTimeout = 5000L;

//    public void cloneTo(HikariProperties target){
//        for (var field : HikariProperties.class.getDeclaredFields()){
//            if (!Modifier.isFinal(field.getModifiers())){
//                field.setAccessible(true);
//                try {
//                    field.set(target, field.get(this));
//                } catch (Exception ignored){
//                    throw new co
//                }
//            }
//        }
//    }
}
