package com.example.monsterhunter;

import com.example.monsterhunter.armorpieces.Armor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MonsterHunterApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //checkAndInitializeFiles();

        FXMLLoader fxmlLoader = new FXMLLoader(MonsterHunterApp.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("MH Wilds Armorbuilder");
        stage.setScene(scene);
        stage.show();
    }

    private void checkAndInitializeFiles() throws IOException {
        File folder = new File("armors");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File("armors/aromor.json");
        if (!file.exists()) {
            initializeDefaultFile();
        }
    }

    private void initializeDefaultFile() throws IOException {
        ArmorScraper.main(new String[]{});
    }


    public static void main(String[] args) {
        /*
        List<Armor> armorList = loadArmorFromJson("armor.json");
        assert armorList != null;
        List<Armor> Head_list = armorList.stream()
                .filter(armor -> armor.getType_as_String().equalsIgnoreCase("Head"))
                .toList();
        List<Armor> Torso_list = armorList.stream()
                .filter(armor -> armor.getType_as_String().equalsIgnoreCase("Torso"))
                .toList();
        List<Armor> Arms_list = armorList.stream()
                .filter(armor -> armor.getType_as_String().equalsIgnoreCase("Arms"))
                .toList();
        List<Armor> Legs_list = armorList.stream()
                .filter(armor -> armor.getType_as_String().equalsIgnoreCase("Legs"))
                .toList();
        List<Armor> Waist_list = armorList.stream()
                .filter(armor -> armor.getType_as_String().equalsIgnoreCase("Waist"))
                .toList();

         */

        /*
        // Testausgabe:
        for (Armor armor : Boots_list) {
            System.out.println(armor);
        }

         */
        launch();


    }

    public static List<Armor> loadArmorFromJson(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(new File(filePath), new TypeReference<List<Armor>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}