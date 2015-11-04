package org.asuki.dp.gof23;

import org.asuki.dp.gof23.Command.Document;
import org.asuki.dp.gof23.Command.Invoker;
import org.asuki.dp.gof23.Command.Bold;
import org.asuki.dp.gof23.Command.Italic;
import org.testng.annotations.Test;

public class CommandTest {

    @Test
    public void test() {

        Document document = new Document();
        document.printStatus();

        Invoker invoker = new Invoker();

        invoker.execute(new Bold(document));
        document.printStatus();

        invoker.execute(new Italic(document));
        document.printStatus();

        invoker.undoLast();
        document.printStatus();

        invoker.undoLast();
        document.printStatus();

        invoker.redoLast();
        document.printStatus();

        invoker.redoLast();
        document.printStatus();
    }

}
