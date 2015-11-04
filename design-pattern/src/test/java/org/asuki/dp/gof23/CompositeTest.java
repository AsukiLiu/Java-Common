package org.asuki.dp.gof23;

import static java.util.Arrays.asList;

import org.asuki.dp.gof23.Composite.Branch;
import org.asuki.dp.gof23.Composite.Element;
import org.asuki.dp.gof23.Composite.Leaf;
import org.asuki.dp.gof23.Composite.Letter;
import org.asuki.dp.gof23.Composite.Sentence;
import org.asuki.dp.gof23.Composite.Word;
import org.testng.annotations.Test;

public class CompositeTest {

    @Test
    public void testMethodVersion() {
        Branch branch = new Branch("branch");
        branch.add(new Leaf("leaf-3"));
        branch.add(new Leaf("leaf-4"));
        branch.add(new Leaf("leaf-5"));

        Branch root = new Branch("root");
        root.add(new Leaf("leaf-1"));
        root.add(new Leaf("leaf-2"));
        root.add(branch);

        root.display();
    }

    @Test
    public void testConstructorVersion() {
        Element sentence = new Sentence(asList(
                new Word(asList(new Letter('V'), new Letter('e'), new Letter(
                        'r'), new Letter('y'))),
                new Word(asList(new Letter('W'), new Letter('e'), new Letter(
                        'l'), new Letter('l')))));

        sentence.print();
    }

}
