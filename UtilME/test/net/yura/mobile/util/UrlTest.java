package net.yura.mobile.util;

import net.yura.mobile.util.Url;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UrlTest {

    @Test(timeout = 1000)
    public void testContructor() {
        testContructor("http://", "http", "", 0, "", "");
        testContructor("http://host", "http", "host", 0, "", "");

        testContructor("http://host?a=1", "http", "host", 0, "", "a=1");
        testContructor("http://host/file", "http", "host", 0, "file", "");
        testContructor("http://host/file?a=1", "http", "host", 0, "file", "a=1");

        testContructor("http://host:2021", "http", "host", 2021, "", "");
        testContructor("http://host:2021?a=1", "http", "host", 2021, "", "a=1");
        testContructor("http://host:2021/file", "http", "host", 2021, "file", "");
        testContructor("http://host:2021/file?a=1", "http", "host", 2021, "file", "a=1");

        testContructor("http://host:2021/file1/file2?a=1", "http", "host", 2021, "file1/file2", "a=1");
    }

    @Test(timeout = 1000)
    public void testQuery() {
        Url url = new Url("http://host:2021/file");
        assertEquals("", url.getQueryParameter("a"));
        url.addQueryParameter("c", "3");
        assertEquals("c=3", url.getQuery());
        assertEquals("3", url.getQueryParameter("c"));

        url = new Url("http://host:2021/file?a=1");
        assertEquals("1", url.getQueryParameter("a"));
        assertEquals("", url.getQueryParameter("b"));

        url = new Url("http://host:2021/file?a=1&b=2");
        assertEquals("1", url.getQueryParameter("a"));
        assertEquals("2", url.getQueryParameter("b"));

        url.addQueryParameter("c", "3");
        assertEquals("a=1&b=2&c=3", url.getQuery());
        assertEquals("1", url.getQueryParameter("a"));
        assertEquals("2", url.getQueryParameter("b"));
        assertEquals("3", url.getQueryParameter("c"));
    }

    @Test(timeout=1000)
    public void testPathSegment() {
        Url url = new Url("http://host:2021/");
        assertEquals("", url.getPathSegment(0));
        assertEquals("", url.getPathSegment(1));
        assertEquals("", url.getPathSegment(2));

        url = new Url("http://host:2021/file1");
        assertEquals("file1", url.getPathSegment(0));
        assertEquals("", url.getPathSegment(1));
        assertEquals("", url.getPathSegment(2));

        url = new Url("http://host:2021/file1/file2");
        assertEquals("file1", url.getPathSegment(0));
        assertEquals("file2", url.getPathSegment(1));
        assertEquals("", url.getPathSegment(2));
    }


    // ---- Helper methods ----


    private void testContructor(String spec, String protocol, String host, int port, String file, String query) {
        Url url = new Url(spec);
        assertEquals(protocol, url.getProtocol());
        assertEquals(host, url.getHost());
        assertEquals(port, url.getPort());
        assertEquals(file, url.getPath());
        assertEquals(query, url.getQuery());
    }
}
