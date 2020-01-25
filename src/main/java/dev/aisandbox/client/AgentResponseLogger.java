package dev.aisandbox.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class AgentResponseLogger implements ClientHttpRequestInterceptor {

  int lastHTTPCode = -1;
  String lastResponse = "";

  @Override
  public ClientHttpResponse intercept(
      HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    // perform the request
    ClientHttpResponse response = execution.execute(request, body);
    // take a copy of the output
    lastHTTPCode = response.getRawStatusCode();
    BufferedReader in = new BufferedReader(new InputStreamReader(response.getBody()));
    String line;
    StringBuilder sb = new StringBuilder();
    while ((line = in.readLine()) != null) {
      sb.append(line);
      sb.append("\n");
    }
    lastResponse = sb.toString();
    in.close();

    return response;
  }
}
