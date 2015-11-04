package org.asuki.dp.other;

import org.asuki.dp.other.NullObject.Node;
import org.asuki.dp.other.NullObject.NodeImpl;
import org.asuki.dp.other.NullObject.NullNode;
import org.testng.annotations.Test;

public class NullObjectTest {

    @Test
    public void test() {
        // @formatter:off
        Node root = 
                new NodeImpl("1", 
                        new NodeImpl("11", 
                                new NodeImpl("111", 
                                        NullNode.getInstance(), 
                                        NullNode.getInstance()),
                                NullNode.getInstance()), 
                        new NodeImpl("12", 
                                NullNode.getInstance(), 
                                new NodeImpl("122",
                                        NullNode.getInstance(), 
                                        NullNode.getInstance()))
                );
        // @formatter:on

        root.walk();
    }
}
