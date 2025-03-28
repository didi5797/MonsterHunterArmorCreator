package com.example.monsterhunter;

import com.example.monsterhunter.armorpieces.Armor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ArmorLoader {
    public static List<Armor> loadArmorFromJson(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(new File(filePath), new TypeReference<List<Armor>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return List.of(); // Falls Fehler, gibt eine leere Liste zurück
        }
    }
}
