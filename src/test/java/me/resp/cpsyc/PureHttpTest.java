package me.resp.cpsyc;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PureHttpTest {

  private static String CLASS_GRAPHQL_ENDPOINT = "https://graphql.fauna.com/graphql";

  @Test
  void tGet() throws IOException, InterruptedException {
    HttpClient httpClient =
        HttpClient.newBuilder()
            .version(Version.HTTP_2) // this is the default
            .build();
    //     HttpRequest mainRequest =
    //         HttpRequest.newBuilder()
    //             .uri(URI.create("https://http2.github.io/"))
    //             .POST(BodyPublishers.ofString(json))
    //             .build();

    String body =
        "{\"query\" :"
            + "\"mutation CreateCpMessage {"
            + "   createCpMessage(data: {"
            + "   	content: \\\"content\\\""
            + "   }) {"
            + "       _id"
            + "       content"
            + "   }"
            + "}"
            + "\","
            + "\"variables\":"
            + "{}"
            + "}";
    String key = Tutil.basicHeader();
    HttpRequest request =
        HttpRequest.newBuilder()
            .header("Authorization", key)
            .uri(URI.create(CLASS_GRAPHQL_ENDPOINT))
            .POST(BodyPublishers.ofString(body))
            .build();

    HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
    log.info("Response status code: " + response.statusCode());
    log.info("Response headers: " + response.headers());
    log.info("Response body: " + response.body());
  }
}
