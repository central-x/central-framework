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

import central.sql.data.Entity;
import central.starter.graphql.GraphQLService;
import central.starter.graphql.annotation.GraphQLBatchLoader;
import central.starter.graphql.annotation.GraphQLQuery;
import central.starter.graphql.data.Db;
import central.starter.graphql.data.dto.DTO;
import central.starter.graphql.data.dto.DeptDTO;
import central.starter.graphql.data.entity.DeptEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dataloader.BatchLoaderEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Alan Yeh
 * @since 2022/09/09
 */
@Service
public class DeptService implements GraphQLService {
    @Override
    public String getNamespace() {
        return "Dept";
    }

    @Override
    public List<Class<? extends Entity>> getTypes() {
        return List.of(DeptDTO.class);
    }

    @Autowired
    private Db db;

    @GraphQLQuery
    public List<DeptDTO> findByIds(@RequestParam List<String> ids) {
        List<DeptEntity> entities = this.db.getDepts().stream().filter(it -> ids.contains(it.getId())).collect(Collectors.toList());

        return DTO.wrap(entities, DeptDTO.class);
    }

    /**
     * 批量获取
     */
    @GraphQLBatchLoader
    public Map<String, DeptDTO> batchLoader(@RequestParam Set<String> ids, BatchLoaderEnvironment environment, HttpServletRequest request, HttpServletResponse response) {
        return this.db.getDepts().stream().filter(it -> ids.contains(it.getId()))
                .map(it -> DTO.wrap(it, DeptDTO.class))
                .collect(Collectors.toMap(DeptDTO::getId, it -> it));
    }

    @GraphQLQuery
    public DeptDTO findById(@RequestParam String id) {
        Optional<DeptEntity> entity = this.db.getDepts().stream().filter(it -> it.getId().equals(id)).findFirst();

        return entity.map(it -> DTO.wrap(it, DeptDTO.class)).orElse(null);
    }
}
