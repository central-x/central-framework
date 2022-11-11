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

package central.util;

import jakarta.validation.constraints.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * 可被观查的
 *
 * @author Alan Yeh
 * @see ObserveEvent
 * @see Observer
 * @since 2022/11/11
 */
public class Observable<O extends Observable<O>> {
    /**
     * 观查者列表
     */
    private final List<Observer<O>> observers = new ArrayList<>();

    /**
     * 添加观查者
     *
     * @param observer 观查者
     */
    public synchronized void addObserver(@Nonnull Observer<O> observer) {
        if (!this.observers.contains(observer)) {
            this.observers.add(observer);
        }
    }

    /**
     * 移除观查者
     *
     * @param observer 观查者
     */
    public synchronized void removeObserver(Observer<O> observer) {
        this.observers.remove(observer);
    }

    /**
     * 通知观查者当前已变更
     */
    protected void notifyObservers() {
        this.notifyObservers(() -> (O) this);
    }

    /**
     * 通知观查者当前已变更
     *
     * @param event 变更事件
     */
    protected void notifyObservers(@NotNull ObserveEvent<O> event) {
        Observer<O>[] observers;
        synchronized (this) {
            observers = this.observers.toArray(new Observer[0]);
        }
        for (var observer : observers) {
            observer.update(event);
        }
    }
}
