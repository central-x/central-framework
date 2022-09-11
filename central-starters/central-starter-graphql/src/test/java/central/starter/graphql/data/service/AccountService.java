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

package central.starter.graphql.data.service;

import central.sql.Conditions;
import central.sql.Orders;
import central.sql.data.Entity;
import central.starter.graphql.GraphQLService;
import central.starter.graphql.annotation.GraphQLBatchLoader;
import central.starter.graphql.annotation.GraphQLQuery;
import central.starter.graphql.data.Db;
import central.starter.graphql.data.dto.AccountDTO;
import central.starter.graphql.data.dto.DTO;
import central.starter.graphql.data.entity.AccountEntity;
import graphql.schema.DataFetchingEnvironment;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Alan Yeh
 * @since 2022/09/09
 */
@Service
public class AccountService implements GraphQLService {
    @Override
    public String getNamespace() {
        return "Account";
    }

    @Override
    public List<Class<? extends Entity>> getTypes() {
        return List.of(AccountDTO.class);
    }

    @Autowired
    private Db db;

    /**
     * 批量获取
     */
    @GraphQLQuery
    public List<AccountDTO> findByIds(@RequestParam List<String> ids, DataFetchingEnvironment environment) {
        List<AccountEntity> entities = this.db.getAccounts().stream().filter(it -> ids.contains(it.getId())).collect(Collectors.toList());
        return DTO.wrap(entities, AccountDTO.class);
    }

    @GraphQLBatchLoader
    public Map<String, AccountDTO> batchLoader(@RequestParam List<String> ids) {
        return this.db.getAccounts().stream().filter(it -> ids.contains(it.getId()))
                .map(it -> DTO.wrap(it, AccountDTO.class))
                .collect(Collectors.toMap(AccountDTO::getId, it -> it));
    }

    @GraphQLQuery
    public AccountDTO findById(@RequestParam String id, DataFetchingEnvironment environment) {
        Optional<AccountEntity> entity = this.db.getAccounts().stream().filter(it -> it.getId().equals(id)).findFirst();

        return entity.map(it -> DTO.wrap(it, AccountDTO.class)).orElse(null);
    }

    public List<AccountDTO> findByDeptId(String deptId) {
        List<AccountEntity> entities = this.db.getAccounts().stream().filter(it -> it.getDeptId().equals(deptId)).collect(Collectors.toList());
        return DTO.wrap(entities, AccountDTO.class);
    }

    @GraphQLQuery
    public List<AccountDTO> findBy(@RequestParam(required = false) Long first,
                                   @RequestParam(required = false) Long offset,
                                   @RequestParam Conditions conditions,
                                   @RequestParam Orders orders,
                                   @Autowired DeptService service,
                                   @Autowired ApplicationContext context,
                                   @Autowired Environment environment,
                                   DataFetchingEnvironment loaderEnvironment,
                                   HttpServletRequest request,
                                   ServletRequest servletRequest,
                                   HttpServletResponse response,
                                   ServletResponse servletResponse) {
        // 懒得去过滤了
        return DTO.wrap(this.db.getAccounts(), AccountDTO.class);
    }
}
