package org.asuki.tool.icu;

import com.ibm.icu.text.Transliterator;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.Normalizer;
import java.text.Normalizer.Form;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BasicTextTest {

    private static final String FULL_WIDTH = "アイウガギグ１２３①￥％＄";
    private static final String HALF_WIDTH = "ｱｲｳｶﾞｷﾞｸﾞ123①¥%$";

    @Test
    public void testNormalizer() {
        final String expect = "アイウガギグ1231¥%$";

        assertThat(Normalizer.normalize(HALF_WIDTH, Form.NFKC), is(expect));
        assertThat(Normalizer.normalize(FULL_WIDTH, Form.NFKC), is(expect));
    }

    @Test(dataProvider = "data")
    public void testIcu(String transType, String fromString, String toString) {
        Transliterator transliterator = Transliterator.getInstance(transType);

        assertThat(transliterator.transliterate(fromString), is(toString));
    }

    @DataProvider
    private Object[][] data() {
        return new Object[][]{
                {"Fullwidth-Halfwidth", FULL_WIDTH, HALF_WIDTH},
                {"Halfwidth-Fullwidth", HALF_WIDTH, FULL_WIDTH},
                {"Katakana-Hiragana", "ｱｲｳｶﾞｷﾞｸﾞ アイウガギグ", "あいうがぎぐ あいうがぎぐ"},
                {"Hiragana-Katakana", "あいうがぎぐ", "アイウガギグ"},
                {"Hiragana-Latin", "あいうがぎぐ", "aiugagigu"},
        };
    }

}
