package org.asuki.dp.gof23;

import static java.lang.System.out;

import org.asuki.dp.gof23.Facade.MainFacade;
import org.testng.annotations.Test;

public class FacadeTest {

    @Test
    public void test() {
        MainFacade facade = new MainFacade();

        facade.operation1();
        out.println();
        facade.operation2();
    }
}
