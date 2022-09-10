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

package central.sql.builder.script;

import central.sql.SqlBuilder;
import central.sql.SqlScript;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.List;

/**
 * 脚本解析器
 *
 * @author Alan Yeh
 * @since 2022/08/09
 */
public class ScriptParser {

    public static List<SqlScript> parse(SqlBuilder builder, String content) throws SQLSyntaxErrorException {
        Document document;
        try {
            var factory = DocumentBuilderFactory.newInstance();
            document = factory.newDocumentBuilder().parse(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new SQLSyntaxErrorException("解析 SQL 脚本异常: " + ex.getLocalizedMessage(), ex);
        }

        var sqls = new ArrayList<SqlScript>();

        var rootNode = document.getFirstChild();
        var children = rootNode.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            var node = children.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                if ("tables".equalsIgnoreCase(node.getNodeName())) {
                    // 解析表相关脚本
                    sqls.addAll(TableScriptParser.parse(builder, node));
                } else if ("columns".equalsIgnoreCase(node.getNodeName())) {
                    // 解析字段相关脚本
                    sqls.addAll(ColumnScriptParser.parse(builder, node));
                } else if ("indexes".equalsIgnoreCase(node.getNodeName())) {
                    // 解析索引相关脚本
                    sqls.addAll(IndexScriptParser.parse(builder, node));
                }
            }
        }

        return sqls;
    }
}
