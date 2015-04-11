package com.free.walker.service.itinerary.util;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;

public class UriUtilTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testEnsureSecureUriAsString() throws InvalidTravelReqirementException, URISyntaxException {
        assertEquals("https://test.me/testcase", UriUtil.ensureSecureUriAsString(new URI("http://test.me/testcase")));
        assertEquals("https://test.me/testcase", UriUtil.ensureSecureUriAsString(new URI("https://test.me/testcase")));
        assertEquals("https://test.me:9001/testcase",
            UriUtil.ensureSecureUriAsString(new URI("http://test.me:9000/testcase")));
        assertEquals("https://test.me:9011/testcase",
            UriUtil.ensureSecureUriAsString(new URI("http://test.me:9010/testcase")));
        assertEquals("https://test.me:8000/testcase",
            UriUtil.ensureSecureUriAsString(new URI("http://test.me:8000/testcase")));
        assertEquals("https://test.me:9001/testcase",
            UriUtil.ensureSecureUriAsString(new URI("https://test.me:9000/testcase")));
        assertEquals("https://test.me:9011/testcase",
            UriUtil.ensureSecureUriAsString(new URI("https://test.me:9010/testcase")));
        assertEquals("https://test.me:8000/testcase",
            UriUtil.ensureSecureUriAsString(new URI("https://test.me:8000/testcase")));
    }
}
