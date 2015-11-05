package org.asuki.common.introspector;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.asuki.common.model.Person;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class IntrospectorTest {

    private static Person person;

    @BeforeClass
    public static void init() {
        person = new Person("Andy", 20);
    }

    @Test
    public void testPropertyDescriptor() throws Exception {
        PropertyDescriptor pd = new PropertyDescriptor("age", person.getClass());
        Method setMethod = pd.getWriteMethod();
        setMethod.invoke(person, 30);

        Method getMethod = pd.getReadMethod();
        Object age = getMethod.invoke(person);

        assertThat(age, is(30));
    }

    @Test
    public void testBeanInfo() throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(person.getClass());
        PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

        Object name = null;
        for (PropertyDescriptor pd : pds) {
            if ("name".equals(pd.getName())) {
                Method method = pd.getReadMethod();
                name = method.invoke(person);
                break;
            }
        }

        assertThat(name, is("Andy"));
    }

}
