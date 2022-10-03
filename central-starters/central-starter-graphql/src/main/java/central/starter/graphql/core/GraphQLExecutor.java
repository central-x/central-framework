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

package central.starter.graphql.core;

import central.starter.graphql.GraphQLRequest;
import central.util.Context;
import central.util.Listx;
import graphql.*;
import graphql.execution.AbortExecutionException;
import graphql.execution.NonNullableFieldWasNullError;
import graphql.relay.InvalidCursorException;
import graphql.relay.InvalidPageSizeException;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * GraphQL 执行器
 *
 * @author Alan Yeh
 * @see GraphQLExecutorFactory
 * @since 2022/09/09
 */
public class GraphQLExecutor {

    @Setter
    private GraphQL graphQL;

    @Setter
    private LoaderRegistry registry;

    public Object execute(GraphQLRequest request, Context context) {
        // 构建输入
        var builder = new ExecutionInput.Builder()
                // 每次执行的时候，都需要重新构建 DataLoaderRegistry
                // 这是因为每次执行 Loader 的时候，上下文都是不一样的
                .dataLoaderRegistry(this.registry.buildRegistry(context))
                .localContext(context)
                .query(request.getQuery())
                .variables(request.getVariables());

        // 执行
        var result = this.graphQL.execute(builder);

        // 判断执行过程中是否有异常
        if (Listx.isNotEmpty(result.getErrors())) {
            var error = result.getErrors().get(0);

            if (ErrorType.InvalidSyntax.equals(error.getErrorType())) {
                // 语法错误
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "[GraphQL 语法错误] " + error.getMessage());
            } else if (ErrorType.ValidationError.equals(error.getErrorType())) {
                // 校验错误
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "[参数校验错误] " + error.getMessage());
            } else if (ErrorType.DataFetchingException.equals(error.getErrorType())) {
                if (error instanceof ExceptionWhileDataFetching ex) {
                    if (ex.getException() instanceof ResponseStatusException exception) {
                        throw exception;
                    } else {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage(), ex.getException());
                    }
                } else if (error instanceof InvalidCursorException ex) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage(), ex);
                } else if (error instanceof InvalidPageSizeException ex) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage(), ex);
                } else if (error instanceof NonNullableFieldWasNullError) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage());
                } else if (error instanceof SerializationError) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage());
                } else if (error instanceof TypeMismatchError) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage());
                } else if (error instanceof UnresolvedTypeError) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage());
                } else {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage());
                }
            } else if (ErrorType.NullValueInNonNullableField.equals(error.getErrorType())) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "[GraphQL 服务异常] " + error.getMessage());
            } else if (ErrorType.OperationNotSupported.equals(error.getErrorType())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "[请求异常] " + error.getMessage());
            } else if (ErrorType.ExecutionAborted.equals(error.getErrorType())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请求中止: " + error.getMessage(), (AbortExecutionException) error);
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "[未知异常] " + error.getMessage());
            }
        }

        // 输出结果
        return result.getData();
    }
}
