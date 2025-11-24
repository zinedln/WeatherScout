package src;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {
    public static void main(String[] args) throws Exception {


        String url = "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&current=temperature_2m,relative_humidity_2m,is_day,wind_speed_10m";

        //HTTP Client
        HttpClient client = HttpClient.newHttpClient();

        //Anfrage erstellen
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        // Anfrage senden & Antwort erhalten
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Ausgabe
        System.out.println("API Antwort:");
        System.out.println(response.body());
    }
}
