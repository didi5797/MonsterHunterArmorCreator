package com.example.monsterhunter;

import com.example.monsterhunter.Skill;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

public class SkillParser {
    public static void main(String[] args) {
        SkillParser parser = new SkillParser();
        parser.printSkillNames();
    }

    public void printSkillNames() {
        OkHttpClient client = new OkHttpClient();

        // API-URL für die Skill-Daten
        String url = "https://wilds.mhdb.io/en/skills";  // Hier die tatsächliche URL einfügen

        // Request erstellen
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0")
                .build();

        try {
            Response response = client.newCall(request).execute(); // API-Aufruf
            if (response.isSuccessful()) {
                assert response.body() != null;
                String jsonData = response.body().string(); // JSON als String

                // Mit Jackson die JSON parsen
                ObjectMapper objectMapper = new ObjectMapper();
                List<Skill> skills = objectMapper.readValue(jsonData, objectMapper.getTypeFactory().constructCollectionType(List.class, Skill.class));

                // Nur die Namen der Skills ausgeben, Leerzeichen durch "_" ersetzen
                for (Skill skill : skills) {
                    System.out.println(skill.getName().replace(" ", "_").replace("'", "").replace("/", "_").replace("-", "_") + ",");
                }
            } else {
                System.out.println("Fehler: " + response.code()); // Falls der Request fehlschlägt
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

