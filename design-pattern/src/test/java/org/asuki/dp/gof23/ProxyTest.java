package org.asuki.dp.gof23;

import org.asuki.dp.gof23.Proxy.DynamicProxySubject;
import org.asuki.dp.gof23.Proxy.ProxySubject;
import org.asuki.dp.gof23.Proxy.StaticProxySubject;
import org.asuki.dp.gof23.Proxy.Subject;
import org.testng.annotations.Test;

public class ProxyTest {

    @Test
    public void testProxy() {
        ProxySubject s = new ProxySubject();
        s.request();
        s.request();
        s.request();
        s.request();
        s.request();
    }

    @Test
    public void testStaticProxy() {
        Subject s = new StaticProxySubject();
        s.request();
    }

    @Test
    public void testDynamicProxy() {
        Subject s = DynamicProxySubject.createProxy();
        s.request();
    }
}
