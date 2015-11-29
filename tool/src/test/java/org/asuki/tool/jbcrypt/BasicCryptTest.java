package org.asuki.tool.jbcrypt;

import org.mindrot.jbcrypt.BCrypt;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BasicCryptTest {

    private static final String PASSWORD = "1234abcd";
    private static String passwordInDb = hashPasswordWithSalt(PASSWORD);

    @Test(dataProvider = "data")
    public void test(String input, boolean expect) {
        assertThat(BCrypt.checkpw(input, passwordInDb), is(expect));
    }

    private static String hashPasswordWithSalt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    @DataProvider
    private Object[][] data() {
        return new Object[][]{
                {PASSWORD, true},
                {PASSWORD + "x", false},
        };
    }
}
