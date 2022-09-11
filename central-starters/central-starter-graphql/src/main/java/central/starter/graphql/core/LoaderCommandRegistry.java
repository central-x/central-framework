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

import central.starter.graphql.core.command.LoaderCommand;
import lombok.Getter;
import org.dataloader.DataLoaderFactory;
import org.dataloader.DataLoaderOptions;
import org.dataloader.DataLoaderRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BatchLoader 注册中心
 *
 * @author Alan Yeh
 * @since 2022/09/09
 */
public class LoaderCommandRegistry {

    @Getter
    private final Map<String, LoaderCommand> commands = new ConcurrentHashMap<>();

    /**
     * 注册 DataLoader 命令
     *
     * @param command loader 命令
     */
    public LoaderCommandRegistry register(LoaderCommand command) {
        this.commands.put(command.getName(), command);
        return this;
    }

    /**
     * 取消注册 DataLoader 命令
     *
     * @param name loader 命令名
     */
    public LoaderCommandRegistry unregister(String name) {
        this.commands.remove(name);
        return this;
    }

    /**
     * 判断是否已注册该 Loader
     *
     * @param name loader 命令名
     */
    public boolean isRegistered(String name) {
        return this.commands.containsKey(name);
    }

    /**
     * 构建 DataLoaderRegister
     *
     * @param context 执行上下文
     */
    public DataLoaderRegistry buildRegistry(ExecuteContext context) {
        var registry = new DataLoaderRegistry();
        var options = DataLoaderOptions.newOptions().setCachingEnabled(false).setBatchingEnabled(true).setMaxBatchSize(1000).setBatchLoaderContextProvider(() -> context);
        for (var loader : this.commands.entrySet()) {
            registry.register(loader.getKey(), DataLoaderFactory.newDataLoader(loader.getValue(), options));
        }
        return registry;
    }
}
