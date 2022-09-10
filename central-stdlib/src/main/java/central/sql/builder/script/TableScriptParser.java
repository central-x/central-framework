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
import central.sql.builder.script.table.AddTableScript;
import central.sql.builder.script.table.DropTableScript;
import central.sql.builder.script.table.RenameTableScript;
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
 * 表相关脚本解析器
 *
 * @author Alan Yeh
 * @since 2022/08/09
 */
class TableScriptParser {

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
                    List<AddTableScript> scripts = parseAddTableScript(node);
                    for (AddTableScript script : scripts) {
                        sqls.addAll(dialect.forAddTable(script));
                    }
                } else if ("rename".equalsIgnoreCase(node.getNodeName())) {
                    List<RenameTableScript> scripts = parseRenameTableScript(node);
                    for (RenameTableScript script : scripts) {
                        sqls.addAll(dialect.forRenameTable(script));
                    }
                } else if ("drop".equalsIgnoreCase(node.getNodeName())) {
                    List<DropTableScript> scripts = parseDropTableScript(node);
                    for (DropTableScript script : scripts) {
                        sqls.addAll(dialect.forDropTable(script));
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
     * 解析添加表脚本
     */
    private static List<AddTableScript> parseAddTableScript(Node parent) {
        List<AddTableScript> scripts = new ArrayList<>();

        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (!"table".equalsIgnoreCase(node.getNodeName())) {
                    throw new IllegalArgumentException(Stringx.format("解析 SQL 脚本出现异常: {} 节点下，只允许出现 table 节点", parent.getNodeName()));
                }

                AddTableScript script = new AddTableScript();
                script.setName(getAttributeValue(node, "name", null));
                script.setRemarks(getAttributeValue(node, "comment", null));

                List<AddTableScript.Column> columns = getTableColumn(node);
                script.setColumns(columns);

                Validatorx.Default().validate(script);
                scripts.add(script);
            }
        }

        return scripts;
    }

    private static List<AddTableScript.Column> getTableColumn(Node parent) {
        List<AddTableScript.Column> scripts = new ArrayList<>();

        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (!"column".equalsIgnoreCase(node.getNodeName())) {
                    throw new IllegalArgumentException(Stringx.format("解析 SQL 脚本出现异常: {} 节点下，只允许出现 column 节点", parent.getNodeName()));
                }

                AddTableScript.Column script = new AddTableScript.Column();

                script.setName(getAttributeValue(node, "name", null));
                script.setType(SqlType.resolve(getAttributeValue(node, "type", null)));
                script.setSize(Integer.parseInt(getAttributeValue(node, "length", "0")));
                script.setPrimaryKey(Boolean.parseBoolean(getAttributeValue(node, "primaryKey", "false")));
                script.setRemarks(getAttributeValue(node, "comment", null));
                if (SqlType.STRING.isCompatibleWith(script.getType())) {
                    if (script.getSize() > 2000) {
                        throw new IllegalArgumentException(Stringx.format("解析 SQL 脚本出现异常: STRING 字段长度不能超过 2000"));
                    }
                }

                Validatorx.Default().validate(script);
                scripts.add(script);
            }
        }
        return scripts;
    }

    /**
     * 解析删除表脚本
     */
    private static List<DropTableScript> parseDropTableScript(Node parent) {
        List<DropTableScript> scripts = new ArrayList<>();

        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (!"table".equalsIgnoreCase(node.getNodeName())) {
                    throw new IllegalArgumentException(Stringx.format("解析 SQL 脚本出现异常: {} 节点下，只允许出现 table 节点", parent.getNodeName()));
                }

                DropTableScript script = new DropTableScript();
                script.setName(getAttributeValue(node, "name", null));

                Validatorx.Default().validate(script);
                scripts.add(script);
            }
        }

        return scripts;
    }

    /**
     * 解析重命名表脚本
     */
    private static List<RenameTableScript> parseRenameTableScript(Node parent) {
        List<RenameTableScript> scripts = new ArrayList<>();

        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (!"table".equalsIgnoreCase(node.getNodeName())) {
                    throw new IllegalArgumentException(Stringx.format("解析 SQL 脚本出现异常: {} 节点下，只允许出现 table 节点", parent.getNodeName()));
                }

                RenameTableScript script = new RenameTableScript();
                script.setName(getAttributeValue(node, "name", null));
                script.setNewName(getAttributeValue(node, "new-name", null));

                Validatorx.Default().validate(script);
                scripts.add(script);
            }
        }

        return scripts;
    }
}
