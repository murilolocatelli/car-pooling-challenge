package com.cabify.carpooling.test.util;

import com.cabify.carpooling.test.BaseUnitTest;
import com.cabify.carpooling.util.NumericUtils;

import org.junit.Assert;
import org.junit.Test;

public class NumericUtilsTest extends BaseUnitTest {

    @Test
    public void testNull() {
        final Long result = NumericUtils.toLong(null);

        Assert.assertNull(result);
    }

    @Test
    public void testInvalidValue() {
        final Long result = NumericUtils.toLong("abc");

        Assert.assertNull(result);
    }

    @Test
    public void testSuccess() {
        final Long result = NumericUtils.toLong("1");

        Assert.assertEquals(result.longValue(), 1L);
    }

}
