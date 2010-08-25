package net.yura.mobile.util;

public class Url {
    private String protocol;
    private String host;
    private int port;
    private String file;
    private String query;

    public Url(String spec) {
        protocol = searchPart(spec, 0, ":");

        int startIdx = protocol.length() + 3;
        String hostPort = searchPart(spec, startIdx, "/?");

        host = searchPart(hostPort, 0, ":");
        String portStr = searchPart(hostPort, host.length() + 1, "");
        if (portStr.length() > 0) {
            port = Integer.parseInt(portStr);
        }

        startIdx += hostPort.length() + 1;
        if (startIdx < spec.length() && spec.charAt(startIdx - 1) == '?') {
            file = "";
            query = spec.substring(startIdx);
        }
        else {
            file = searchPart(spec, startIdx, "?");

            startIdx += file.length() + 1;
            query = searchPart(spec, startIdx, "");
        }
    }

    public Url(String protocol, String host, int port, String file, String query) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.file = file;
        this.query = query;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void addQueryParameter(String key, String value) {
        if (query.length() > 0) {
            query += '&';
        }
        query += key + '=' + value;
    }

    public String getQueryParameter(String key) {

        int startIdx = 0;
        while (startIdx < query.length()) {
            String k = searchPart(query, startIdx, "=");
            startIdx += k.length() + 1;
            String v = searchPart(query, startIdx, "&");
            startIdx += v.length() + 1;

            if (key.equals(k)) {
                return v;
            }
        }

        return "";
    }

    public String getPath() {
        return (query.length() == 0) ? file : file + '&' + query;
    }

    public String getPathSegment(int idx) {
        String res = "";
        int startIdx = 0;
        for (int i = 0; i <= idx; i++) {
            res = searchPart(file, startIdx, "/");
            startIdx += res.length() + 1;
        }

        return res;
    }

    // --- Internal helper methods ---

    private String searchPart(String spec, int startIdx, String delim) {

        if (startIdx >= spec.length()) {
            return "";
        }

        for (int i = 0; i < delim.length(); i++) {
            int idx = spec.indexOf(delim.charAt(i), startIdx);
            if (idx >= 0) {
                return spec.substring(startIdx, idx);
            }
        }

        return spec.substring(startIdx);
    }
}
