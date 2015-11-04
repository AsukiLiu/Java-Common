package org.asuki.dp.other;

import static java.lang.System.out;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class NullObject {

    interface Node {

        String getName();

        Node getLeft();

        Node getRight();

        int getTreeSize();

        void walk();
    }

    @AllArgsConstructor
    @Getter
    static class NodeImpl implements Node {

        private final String name;
        private final Node left;
        private final Node right;

        @Override
        public int getTreeSize() {
            return 1 + left.getTreeSize() + right.getTreeSize();
        }

        @Override
        public void walk() {
            out.println(name);
            if (left.getTreeSize() > 0) {
                left.walk();
            }
            if (right.getTreeSize() > 0) {
                right.walk();
            }
        }
    }

    static class NullNode implements Node {

        private static NullNode instance = new NullNode();

        private NullNode() {
        }

        public static NullNode getInstance() {
            return instance;
        }

        @Override
        public int getTreeSize() {
            return 0;
        }

        @Override
        public Node getLeft() {
            return null;
        }

        @Override
        public Node getRight() {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public void walk() {
        }
    }
}
