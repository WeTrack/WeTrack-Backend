package com.wetrack.util;

import org.junit.Test;

import static com.wetrack.util.CryptoUtils.md5Digest;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CryptoUtilsTest {

    @Test
    public void testMd5Digest() {
        assertThat(md5Digest("12345"), is("827ccb0eea8a706c4c34a16891f84e7b"));
        assertThat(md5Digest("67890"), is("1e01ba3e07ac48cbdab2d3284d1dd0fa"));
    }

}
