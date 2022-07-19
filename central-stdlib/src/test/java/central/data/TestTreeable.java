package central.data;

import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Treeable Test Cases
 *
 * @author Alan Yeh
 * @since 2022/07/10
 */
public class TestTreeable {

    @Test
    public void case1() {
        var root1 = new TreeNode(null, "1", "Node1");
        var root1Child1 = new TreeNode("1", "3", "Node3");
        var root1Child2 = new TreeNode("1", "4", "Node4");
        var root1Child3 = new TreeNode("1", "5", "Node5");
        var root1Child1Child1 = new TreeNode("3", "6", "Node6");
        var root1Child1Child2 = new TreeNode("3", "7", "Node7");
        var root1Child3Child1 = new TreeNode("5", "8", "Node8");
        var root1Child3Child1Child1 = new TreeNode("8", "9", "Node9");
        var root2 = new TreeNode(null, "2", "Node2");
        var root2Child1 = new TreeNode("2", "10", "Node10");
        var root2Child2 = new TreeNode("2", "11", "Node11");
        var root2Child1Child1 = new TreeNode("10", "12", "Node12");

        var roots = Treeable.build(
                Arrays.asList(root1,
                        root1Child1,
                        root1Child2,
                        root1Child3,
                        root1Child1Child1,
                        root1Child1Child2,
                        root1Child3Child1,
                        root1Child3Child1Child1,
                        root2,
                        root2Child1,
                        root2Child2,
                        root2Child1Child1)
        );

        Assertions.assertEquals(2, roots.size());
        Assertions.assertEquals(3, roots.get(0).getChildren().size());
        Assertions.assertEquals(2, roots.get(0).getChild("3").getChildren().size());
        Assertions.assertEquals(0, roots.get(0).getChild("4").getChildren().size());
        Assertions.assertEquals(1, roots.get(0).getChild("5").getChildren().size());
        Assertions.assertEquals(1, roots.get(0).getChild("5").getChild("8").getChildren().size());
        Assertions.assertEquals(2, roots.get(1).getChildren().size());
        Assertions.assertEquals(1, roots.get(1).getChild("10").getChildren().size());
        Assertions.assertEquals(0, roots.get(1).getChild("11").getChildren().size());
    }

    @Data
    private static class TreeNode implements Treeable<TreeNode> {
        private String parentId;

        private String id;

        private String name;

        private List<TreeNode> children = new ArrayList<>();

        public TreeNode(String parentId, String id, String name) {
            this.parentId = parentId;
            this.id = id;
            this.name = name;
        }

        public TreeNode getChild(String childId) {
            return this.children.stream().filter(it -> Objects.equals(childId, it.getId())).findFirst().orElse(null);
        }
    }
}
