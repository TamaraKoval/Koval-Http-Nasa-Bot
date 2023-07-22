import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static final String URL = "https://api.nasa.gov/planetary/apod?api_key=bd95fwqqJsrnKwyqGBIrfUf9eYwkqerkQj07d54j";
    public static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) throws IOException {

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();

        HttpGet request = new HttpGet(URL);
        CloseableHttpResponse response = httpClient.execute(request);
//        Scanner scanner = new Scanner(response.getEntity().getContent());
//        System.out.println(scanner.nextLine());
        NasaPicture answer = MAPPER.readValue(response.getEntity().getContent(), NasaPicture.class);
        // System.out.println(answer.getUrl());

        HttpGet imageRequest = new HttpGet(answer.getUrl());
        CloseableHttpResponse image = httpClient.execute(imageRequest);

        String[] arr = answer.getUrl().split("/");
        String fileName = arr[arr.length - 1];

        HttpEntity entity = image.getEntity();

        try(FileOutputStream fos = new FileOutputStream(fileName)) {
            entity.writeTo(fos);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        httpClient.close();
    }
}
