package ro.deiutzblaxo.cloud.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class RequestSender {

    public void sendRequest(Request request) throws IOException {

        HttpURLConnection httpURLConnection = (HttpURLConnection) request.getURL().openConnection();
        httpURLConnection.setRequestMethod(request.getRequestMethod().name());
        httpURLConnection.setRequestProperty("User-Agent", "Cloud-Library");

        if (request.getHeader() != null && !request.getHeader().isEmpty()) {
            request.getHeader().forEach((s, s2) -> httpURLConnection.setRequestProperty(s, s2));
        }
        httpURLConnection.setConnectTimeout(5000);
        httpURLConnection.setReadTimeout(5000);
        if (request.hasAuthenticator())
            httpURLConnection.setAuthenticator(request.getAuthenticator());
        if (request.getBody() != null) {
            httpURLConnection.setDoOutput(true);
            OutputStream stream = httpURLConnection.getOutputStream();
            stream.write(request.getBody().getBytes());
            stream.flush();
            stream.close();
        }
        String inputLine;
        StringBuilder response = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        System.out.println(response);
        /*} else {

        }*/

    }

}
