package org.asuki.dp.gof23;

import org.asuki.dp.gof23.Adapter.Adapterable;
import org.asuki.dp.gof23.Adapter.AdapterImplA;
import org.asuki.dp.gof23.Adapter.AdapterImplB;
import org.testng.annotations.Test;

public class AdapterTest {

    @Test
    public void testObjectWay() {
        Adapterable t = new AdapterImplA();
        t.request();
    }

    @Test
    public void testClassWay() {
        Adapterable t = new AdapterImplB();
        t.request();
    }
}
