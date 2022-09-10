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
import central.sql.builder.script.index.AddIndexScript;
import central.sql.builder.script.index.DropIndexScript;
import central.util.Objectx;
import central.util.Stringx;
import central.validation.Validatorx;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.List;

/**
 * 索引相关脚本解析器
 *
 * @author Alan Yeh
 * @since 2022/08/09
 */
class IndexScriptParser {


    /**
     * 解析 XML
     */
    public static List<SqlScript> parse(SqlBuilder dialect, Node parent) throws SQLSyntaxErrorException {
        List<SqlScript> sqls = new ArrayList<>();

        NodeList children = parent.getChildNodes();

        for (int j = 0; j < children.getLength(); j++) {
            Node node = children.item(j);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if ("add".equalsIgnoreCase(node.getNodeName())) {
                    List<AddIndexScript> scripts = parseAddIndexScript(node);
                    for (AddIndexScript script : scripts) {
                        sqls.addAll(dialect.forAddIndex(script));
                    }
                } else if ("drop".equalsIgnoreCase(node.getNodeName())) {
                    List<DropIndexScript> scripts = parseDropIndexScript(node);
                    for (DropIndexScript script : scripts) {
                        sqls.addAll(dialect.forDropIndex(script));
                    }
                }
            }
        }

        return sqls;
    }

    private static String getAttributeValue(Node node, String attribute, String defaultValue) {
        NamedNodeMap attributes = node.getAttributes();
        Node item = attributes.getNamedItem(attribute);
        if (item == null) {
            return defaultValue;
        }
        return Objectx.get(item.getNodeValue(), defaultValue);
    }


    /**
     * 解析添加索引脚本
     */
    private static List<AddIndexScript> parseAddIndexScript(Node parent) {
        List<AddIndexScript> scripts = new ArrayList<>();

        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (!"index".equalsIgnoreCase(node.getNodeName())) {
                    throw new IllegalArgumentException(Stringx.format("解析 SQL 脚本出现异常: {} 节点下，只允许出现 index 节点", parent.getNodeName()));
                }

                AddIndexScript script = new AddIndexScript();
                script.setName(getAttributeValue(node, "name", null));
                script.setTable(getAttributeValue(node, "table", null));
                script.setColumn(getAttributeValue(node, "column", null));

                Validatorx.Default().validate(script);
                scripts.add(script);
            }
        }
        return scripts;
    }

    /**
     * 解析删除索引脚本
     */
    private static List<DropIndexScript> parseDropIndexScript(Node parent) {

        List<DropIndexScript> scripts = new ArrayList<>();

        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (!"index".equalsIgnoreCase(node.getNodeName())) {
                    throw new IllegalArgumentException(Stringx.format("解析 SQL 脚本出现异常: {} 节点下，只允许出现 index 节点", parent.getNodeName()));
                }

                DropIndexScript script = new DropIndexScript();
                script.setTable(getAttributeValue(node, "table", null));
                script.setName(getAttributeValue(node, "name", null));

                Validatorx.Default().validate(script);
                scripts.add(script);
            }
        }
        return scripts;
    }
}
