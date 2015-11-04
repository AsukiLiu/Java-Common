package org.asuki.dp.gof23;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.asuki.dp.gof23.Chain.ValidationResult;
import org.asuki.dp.gof23.Chain.ValidationService;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ChainTest {

    @Test(dataProvider = "data")
    public void test(String toValidate, boolean isValid, String expectedResult) {
        ValidationResult result = ValidationService.validate(toValidate);

        assertThat(result.isValid(), is(isValid));
        assertThat(result.toString(), is(expectedResult));
    }

    @DataProvider
    private Object[][] data() {
        return new Object[][] {
                { "@123456", false, "[too long, invalid character]" },
                { "12345", true, "[]" } };
    }
}
