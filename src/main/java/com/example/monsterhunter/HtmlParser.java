package com.example.monsterhunter;

import com.example.monsterhunter.armorpieces.Armor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.List;

public class HtmlParser {

    private static final String API_URL = "https://wilds.mhdb.io/en/armor";

    public static List<Armor> getArmorList() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(API_URL)
                .header("User-Agent", "Mozilla/5.0") // Vermeidung von 403-Fehlern
                .build();

        try {
            Response response = client.newCall(request).execute(); // API-Aufruf
            if (response.isSuccessful() && response.body() != null) {
                String jsonData = response.body().string(); // JSON-Daten als String
                System.out.println(jsonData);

                // JSON in List<Armor> konvertieren
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(jsonData, new TypeReference<List<Armor>>() {});
            } else {
                System.out.println("Fehler: HTTP-Code " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return List.of(); // Gibt eine leere Liste zurück, falls ein Fehler auftritt
    }

    public static void main(String[] args) {
        //System.out.println(getArmorList());
        getArmorList();
    }
}
