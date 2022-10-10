package ro.deiutzblaxo.cloud.http.request;

import java.net.Authenticator;
import java.net.URL;
import java.util.HashMap;


public class RequestPrefab implements Request {
    public RequestPrefab(HashMap<String, String> header, String body, URL url, RequestMethod requestMethod, Authenticator authenticator) {
        this.header = header;
        Body = body;
        this.url = url;
        this.requestMethod = requestMethod;
        this.authenticator = authenticator;
    }

    public RequestPrefab(HashMap<String, String> header, String body, URL url, RequestMethod requestMethod) {
        this.header = header;
        Body = body;
        this.url = url;
        this.requestMethod = requestMethod;
    }

    public RequestPrefab(String body, URL url, RequestMethod requestMethod) {
        Body = body;
        this.url = url;
        this.requestMethod = requestMethod;
    }

    public RequestPrefab(){}

    private HashMap<String, String> header;
    private String Body;
    private URL url;
    private RequestMethod requestMethod;
    private Authenticator authenticator;


    @Override
    public HashMap<String, String> getHeader() {
        return header;
    }

    @Override
    public Request setHeader(HashMap<String, String> header) {
        this.header = header;
        return this;
    }

    @Override
    public String getBody() {
        return Body;
    }

    @Override
    public Request setBody(String body) {
        Body = body;
        return this;
    }

    public URL getURL() {
        return url;
    }

    public Request setURL(URL url) {
        this.url = url;
        return this;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    @Override
    public Request setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    @Override
    public Request setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
        return this;
    }

    @Override
    public Authenticator getAuthenticator() {
        return authenticator;
    }

    @Override
    public boolean hasAuthenticator() {
        return authenticator != null;
    }
}
