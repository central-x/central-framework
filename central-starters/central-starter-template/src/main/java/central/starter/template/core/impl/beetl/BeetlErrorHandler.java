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

package central.starter.template.core.impl.beetl;

import central.starter.template.core.RenderException;
import org.beetl.core.ErrorHandler;
import org.beetl.core.GroupTemplate;
import org.beetl.core.exception.BeetlException;
import org.beetl.core.exception.ErrorInfo;

import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Beetl 错误处理
 *
 * @author Alan Yeh
 * @since 2022/11/14
 */
public class BeetlErrorHandler implements ErrorHandler {

    @Override
    public void processException(BeetlException ex, GroupTemplate groupTemplate, Writer writer) {
        throw new RenderException(ex.getLocalizedMessage(), ex);
//        ErrorInfo error = new ErrorInfo(ex);
//
//        if (error.getErrorCode().equals(BeetlException.CLIENT_IO_ERROR_ERROR)) {
//            //不输出详细提示信息
//            if (!groupTemplate.getConf().isIgnoreClientIOError()) {
//                println(writer, "客户端IO异常:" + getResourceName(ex.resource.getId()) + ":" + error.msg);
//                if (ex.getCause() != null) {
//                    this.printThrowable(writer, ex.getCause());
//                }
//                return;
//
//            }
//
//        }
//
//        int line = error.errorTokenLine;
//
//        StringBuilder sb = new StringBuilder(">>").append(this.getDateTime()).append(":").append(error.type)
//                .append(":").append(error.errorTokenText).append(" 位于").append(line != 0 ? line + "行" : "").append(" 资源:")
//                .append(getResourceName(ex.resource.getId()));
//
//        if (error.errorCode.equals(BeetlException.TEMPLATE_LOAD_ERROR)) {
//            if (error.msg != null)
//                sb.append(error.msg);
//            println(writer, sb.toString());
//            println(writer, groupTemplate.getResourceLoader().getInfo());
//            return;
//        }
//
//        println(writer, sb.toString());
//        if (ex.getMessage() != null) {
//            println(writer, ex.getMessage());
//        }
//
//        ResourceLoader resLoader = groupTemplate.getResourceLoader();
//        //潜在问题，此时可能得到是一个新的模板（开发模式下），不过可能性很小，忽略！
//
//        String content = null;
//        try {
//
//            Resource res = ex.resource;
//            //显示前后三行的内容
//            int[] range = this.getRange(line);
//            content = res.getContent(range[0], range[1]);
//            if (content != null) {
//                String[] strs = content.split(ex.cr);
//                int lineNumber = range[0];
//                for (String str : strs) {
//                    print(writer, "" + lineNumber);
//                    print(writer, "|");
//                    println(writer, str);
//                    lineNumber++;
//                }
//
//            }
//        } catch (IOException e) {
//
//            //ingore
//
//        }
//
//        if (error.hasCallStack()) {
//            println(writer, "  ========================");
//            println(writer, "  调用栈:");
//            for (int i = 0; i < error.resourceCallStack.size(); i++) {
//                println(writer, "  " + error.resourceCallStack.get(i) + " 行："
//                        + error.tokenCallStack.get(i).line);
//            }
//        }
//
//        printCause(error, writer);
//        try {
//            writer.flush();
//        } catch (IOException ignored) {
//        }

    }

    protected void printCause(ErrorInfo error, Writer writer) {
        Throwable t = error.cause;
        if (t != null) {
            printThrowable(writer, t);
        }

    }

    protected Object getResourceName(Object resourceId) {
        return resourceId;
    }

    protected void println(Writer w, String msg) {
        System.out.println(msg);
    }

    protected void print(Writer w, String msg) {
        System.out.print(msg);
    }

    protected void printThrowable(Writer w, Throwable t) {
        t.printStackTrace();
    }

    protected int[] getRange(int line) {
        int startLine = 0;
        int endLine = 0;
        if (line > 3) {
            startLine = line - 3;
        } else {
            startLine = 1;
        }

        endLine = startLine + 6;
        return new int[]
                {startLine, endLine};
    }

    protected String getDateTime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        return sdf.format(date);
    }
}