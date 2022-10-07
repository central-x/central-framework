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
import central.sql.SqlType;
import central.sql.builder.script.column.AddColumnScript;
import central.sql.builder.script.column.DropColumnScript;
import central.sql.builder.script.column.RenameColumnScript;
import central.util.Objectx;
import central.lang.Stringx;
import central.validation.Validatex;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.List;

/**
 * 字段相关表本解析器
 *
 * @author Alan Yeh
 * @since 2022/08/09
 */
class ColumnScriptParser {

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
                    List<AddColumnScript> scripts = parseAddColumnScript(node);
                    for (AddColumnScript script : scripts) {
                        sqls.addAll(dialect.forAddColumn(script));
                    }
                } else if ("rename".equalsIgnoreCase(node.getNodeName())) {
                    List<RenameColumnScript> scripts = parseRenameColumnScript(node);
                    for (RenameColumnScript script : scripts) {
                        sqls.addAll(dialect.forRenameColumn(script));
                    }
                } else if ("drop".equalsIgnoreCase(node.getNodeName())) {
                    List<DropColumnScript> scripts = parseDropColumnScript(node);
                    for (DropColumnScript script : scripts) {
                        sqls.addAll(dialect.forDropColumn(script));
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
        return Objectx.getOrDefault(item.getNodeValue(), defaultValue);
    }

    /**
     * 解析添加字段脚本
     */
    private static List<AddColumnScript> parseAddColumnScript(Node parent) {
        List<AddColumnScript> scripts = new ArrayList<>();

        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (!"column".equalsIgnoreCase(node.getNodeName())) {
                    throw new IllegalArgumentException(Stringx.format("解析 SQL 脚本出现异常: {} 节点下，只允许出现 column 节点", parent.getNodeName()));
                }

                AddColumnScript script = new AddColumnScript();

                script.setName(getAttributeValue(node, "name", null));
                if ("table".equalsIgnoreCase(parent.getNodeName())) {
                    script.setTable(getAttributeValue(parent, "name", null));
                } else {
                    script.setTable(getAttributeValue(node, "table", null));
                }
                script.setType(SqlType.resolve(getAttributeValue(node, "type", null)));
                script.setLength(Integer.parseInt(getAttributeValue(node, "length", "0")));
                script.setComment(getAttributeValue(node, "comment", null));
                script.setAfter(getAttributeValue(node, "after", null));

                if (SqlType.STRING.isCompatibleWith(script.getType())) {
                    if (script.getLength() > 2000) {
                        throw new IllegalArgumentException(Stringx.format("解析 SQL 脚本出现异常: STRING 字段长度不能超过 2000"));
                    }
                }

                Validatex.Default().validate(script);
                scripts.add(script);
            }
        }
        return scripts;
    }

    /**
     * 解析删除表字段脚本
     */
    private static List<DropColumnScript> parseDropColumnScript(Node parent) {
        List<DropColumnScript> scripts = new ArrayList<>();

        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (!"column".equalsIgnoreCase(node.getNodeName())) {
                    throw new IllegalArgumentException(Stringx.format("解析 SQL 脚本出现异常: {} 节点下，只允许出现 column 节点", parent.getNodeName()));
                }

                DropColumnScript script = new DropColumnScript();

                script.setTable(getAttributeValue(node, "table", null));
                script.setName(getAttributeValue(node, "name", null));

                Validatex.Default().validate(script);
                scripts.add(script);
            }
        }
        return scripts;
    }

    /**
     * 解析重命名表字段脚本
     */
    private static List<RenameColumnScript> parseRenameColumnScript(Node parent) {
        List<RenameColumnScript> scripts = new ArrayList<>();

        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (!"column".equalsIgnoreCase(node.getNodeName())) {
                    throw new IllegalArgumentException(Stringx.format("解析 SQL 脚本出现异常: {} 节点下，只允许出现 column 节点", parent.getNodeName()));
                }

                RenameColumnScript script = new RenameColumnScript();
                script.setTable(getAttributeValue(node, "table", null));
                script.setName(getAttributeValue(node, "name", null));
                script.setNewName(getAttributeValue(node, "new-name", null));
                script.setType(SqlType.resolve(getAttributeValue(node, "type", null)));
                script.setLength(Integer.parseInt(getAttributeValue(node, "length", "0")));
                script.setNullable(Boolean.parseBoolean(getAttributeValue(node, "nullable", "true")));
                script.setRemarks(getAttributeValue(node, "comment", null));

                Validatex.Default().validate(script);
                scripts.add(script);
            }
        }
        return scripts;
    }
}
