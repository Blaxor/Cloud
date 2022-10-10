package ro.deiutzblaxo.cloud.http.request;

import java.net.Authenticator;
import java.net.URL;
import java.util.HashMap;

public interface Request {

    HashMap<String, String> getHeader();

    Request setHeader(HashMap<String, String> header);

    String getBody();

    Request setBody(String body);

    URL getURL();

    Request setURL(URL url);

    RequestMethod getRequestMethod();

    Request setRequestMethod(RequestMethod method);

    Request setAuthenticator(Authenticator authenticator);

    Authenticator getAuthenticator();

    boolean hasAuthenticator();

}
