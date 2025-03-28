package com.example.monsterhunter;

import com.example.monsterhunter.armorpieces.Armor;
import com.example.monsterhunter.armorpieces.Boni;
import com.example.monsterhunter.armorpieces.Skills;
import com.google.gson.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

public class MonsterHunterController {

    private static final String SAVE_FILE_PATH = "armors/saved_armor_sets.json";
    //private List<Armor> armorList = ArmorLoader.loadArmorFromJson("armors/armor.json");
    private List<Armor> armorList = HtmlParser.getArmorList();
    private ComboBox<Armor> lastUsedComboBox = null;
    private Armor headgear_temp = null;
    private Armor torso_temp = null;
    private Armor arms_temp = null;
    private Armor legs_temp = null;
    private Armor waist_temp = null;

    @FXML private ComboBox<Armor> headCombo;
    @FXML private ComboBox<Armor> torsoCombo;
    @FXML private ComboBox<Armor> armsCombo;
    @FXML private ComboBox<Armor> legsCombo;
    @FXML private ComboBox<Armor> waistCombo;
    @FXML private Label totalDefenseLabel;
    @FXML private Label fireResistanceLabel;
    @FXML private Label waterResistanceLabel;
    @FXML private Label thunderResistanceLabel;
    @FXML private Label iceResistanceLabel;
    @FXML private Label dragonResistanceLabel;
    @FXML private Label skillsLabel;
    @FXML private Label selectedArmorLabel;
    @FXML private Label defenseLabel;
    @FXML private Label resistanceLabel;
    @FXML private Label skillsDetailLabel;
    @FXML private Label aktuellausgewahlt;
    @FXML private Label setbonusskillLabel;
    @FXML private Label groupskillLabel;
    @FXML private Label setbonusskillLabel_aktuell;
    @FXML private Label groupskillLabel_aktuell;
    @FXML private TextField searchField;
    @FXML private ListView<Armor> searchResultsList;
    @FXML
    private ListView<ArmorSet> armorSetListView;


    @FXML
    private Label welcomeText;
    @FXML private BorderPane root;

    public void initialize() {

        // Beispiel: ArmorSets aus JSON laden (muss ggf. angepasst werden)
        List<ArmorSet> loadedArmorSets = loadArmorSetsFromJson(); // Lade die Armor-Sets aus der JSON-Datei

        armorSetListView.setCellFactory(listView -> new CheckBoxListCell<>());
        // Fülle die ListView mit den geladenen Armor-Sets
        armorSetListView.getItems().addAll(loadedArmorSets);

    loadComboBox(headCombo, "Head");
        loadComboBox(torsoCombo, "Torso");
        loadComboBox(armsCombo, "Arms");
        loadComboBox(legsCombo, "Legs");
        loadComboBox(waistCombo, "Waist");

        armorSetListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Event-Handler für jedes ComboBox-Feld hinzufügen
        headCombo.setOnAction(e -> updateArmorDetails());
        torsoCombo.setOnAction(e -> updateArmorDetails());
        armsCombo.setOnAction(e -> updateArmorDetails());
        legsCombo.setOnAction(e -> updateArmorDetails());
        waistCombo.setOnAction(e -> updateArmorDetails());


        totalDefenseLabel.setText("Total Defense: 0");
        fireResistanceLabel.setText("Fire: 0");
        waterResistanceLabel.setText("Water: 0");
        thunderResistanceLabel.setText("Thunder: 0");
        iceResistanceLabel.setText("Ice: 0");
        dragonResistanceLabel.setText("Dragon: 0");
        skillsLabel.setText("No skills selected");

        searchField.textProperty().addListener((observable, oldValue, newValue) -> onSearch()); // Suchfeld überwachen

        // Setzt den Namen als Anzeige für ListView (ähnlich wie ComboBox)
        searchResultsList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Armor armor, boolean empty) {
                super.updateItem(armor, empty);
                if (empty || armor == null) {
                    setText("");
                } else {
                    // Skills als String zusammenfügen
                    String skillsText = armor.getBonis().stream()
                            .map(bonus -> bonus.getSkill().toString().replace("_", " ") + " " + bonus.getSkillevel())
                            .collect(Collectors.joining(", "));

                    // Finaler Text für die Anzeige
                    setText(armor.getName() + " || " + skillsText);
                }
            }
        });


        searchResultsList.setOnMouseClicked(event -> {
            Armor selected = searchResultsList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                /** für mittlere Anzeige */
                selectedArmorLabel.setText(selected.getName());
                defenseLabel.setText("Defense: " + selected.getDefense_base());
                resistanceLabel.setText(String.format("Fire: %d\nWater: %d\nThunder: %d\nIce: %d\nDragon: %d",
                        selected.getResistances()[0], selected.getResistances()[1], selected.getResistances()[2], selected.getResistances()[3], selected.getResistances()[4]));


                Map<Skills, Integer> Skills_onepart = new HashMap<>();
                for (Boni bonus : selected.getBonis()) {
                    Skills skill = bonus.getSkill();
                    int skillLevel = bonus.getSkillevel();
                    Skills_onepart.put(skill, Skills_onepart.getOrDefault(skill, 0) + skillLevel);
                }
                StringBuilder skillsText2 = new StringBuilder();
                Skills_onepart.forEach((skill, level) -> {
                    skillsText2.append(skill.toString().replace("_", " "))
                            .append(": ")
                            .append(level)
                            .append("\n");
                });
                skillsDetailLabel.setText(skillsText2.toString());

                /** für auswahl in combobox */
                switch (selected.getType_as_String().toLowerCase()) {
                    case "head" -> headCombo.setValue(selected);
                    case "torso" -> torsoCombo.setValue(selected);
                    case "arms" -> armsCombo.setValue(selected);
                    case "legs" -> legsCombo.setValue(selected);
                    case "waist" -> waistCombo.setValue(selected);
                }

                updateArmorDetails();
            }
        });

        /*
        armorSetListView.setCellFactory(param -> new ListCell<ArmorSet>() {
            @Override
            protected void updateItem(ArmorSet item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString()); // Hier kannst du anpassen, wie die Sets dargestellt werden
                }
            }
        });

         */

    }

    private void updateArmorDetails() {
        int totalDefense = 0;
        int defense_onepart = 0;
        int[] totalResistances = new int[5]; // Fire, Water, Thunder, Ice, Dragon
        int[] resistances_onepart = new int[5]; // Fire, Water, Thunder, Ice, Dragon
        Map<Skills, Integer> totalSkills = new HashMap<>();
        Map<Skills, Integer> setboniskills = new HashMap<>();
        Map<Skills, Integer> groupboniskills = new HashMap<>();
        Map<Skills, Integer> Skills_onepart = new HashMap<>();
        Armor lastSelectedArmor = null;

        // ArrayList, in der nur die nicht-`null` Rüstungsteile gespeichert werden
        List<Armor> selectedArmor = new ArrayList<>();

        // Hinzufügen der ausgewählten Rüstungsteile nur, wenn sie nicht `null` sind
        if (headCombo.getValue() != null) selectedArmor.add(headCombo.getValue());
        if (torsoCombo.getValue() != null) selectedArmor.add(torsoCombo.getValue());
        if (armsCombo.getValue() != null) selectedArmor.add(armsCombo.getValue());
        if (legsCombo.getValue() != null) selectedArmor.add(legsCombo.getValue());
        if (waistCombo.getValue() != null) selectedArmor.add(waistCombo.getValue());

        StringBuilder selectedArmorText = new StringBuilder();
        for (Armor armor : selectedArmor) {
            if (armor != null) {
                // Anzeige des Namens des ausgewählten Armor-Teils
                //selectedArmorText.append(armor.getName()).append("\n");

                // Verteidigung und Resistances berechnen
                totalDefense += armor.getDefense_base();
                for (int i = 0; i < armor.getResistances().length; i++) {
                    totalResistances[i] += armor.getResistances()[i];
                }

                // Skills zusammenzählen
                for (Boni bonus : armor.getBonis()) {
                    Skills skill = bonus.getSkill();
                    int skillLevel = bonus.getSkillevel();
                    totalSkills.put(skill, totalSkills.getOrDefault(skill, 0) + skillLevel);
                }

                Skills setbonusskill = armor.getSetbonusskill();
                Skills groupskill = armor.getGroupSkill();
                if(setbonusskill != Skills.none) setboniskills.put(setbonusskill, totalSkills.getOrDefault(setbonusskill, 0) + 1);
                if(groupskill != Skills.none) groupboniskills.put(groupskill, totalSkills.getOrDefault(groupskill, 0) + 1);

            }
        }

        // Falls lastUsedComboBox nicht null ist, verwenden wir den Wert von dieser ComboBox
        if (headCombo.getValue() != headgear_temp || torsoCombo.getValue() != torso_temp || armsCombo.getValue() != arms_temp ||
                legsCombo.getValue() != legs_temp || waistCombo.getValue() != waist_temp) {
            if(headCombo.getValue() != headgear_temp){
                headgear_temp = headCombo.getValue();
                lastSelectedArmor = headCombo.getValue();
            }else if(torsoCombo.getValue() != torso_temp){
                torso_temp = torsoCombo.getValue();
                lastSelectedArmor = torsoCombo.getValue();
            }else if(armsCombo.getValue() != arms_temp){
                arms_temp = armsCombo.getValue();
                lastSelectedArmor = armsCombo.getValue();
            }else if(legsCombo.getValue() != legs_temp){
                legs_temp = legsCombo.getValue();
                lastSelectedArmor = legsCombo.getValue();
            }else{
                waist_temp = waistCombo.getValue();
                lastSelectedArmor = waistCombo.getValue();
            }

            for (int i = 0; i < resistances_onepart.length; i++) {
                resistances_onepart[i] = 0;
            }

            defense_onepart = lastSelectedArmor.getDefense_base();
            for (int i = 0; i < lastSelectedArmor.getResistances().length; i++) {
                resistances_onepart[i] = lastSelectedArmor.getResistances()[i];
            }


            for (Boni bonus : lastSelectedArmor.getBonis()) {
                Skills skill = bonus.getSkill();
                int skillLevel = bonus.getSkillevel();
                Skills_onepart.put(skill, Skills_onepart.getOrDefault(skill, 0) + skillLevel);
            }
            /*
            Skills setbonusskill = lastSelectedArmor.getSetbonusskill();
            Skills groupskill = lastSelectedArmor.getGroupSkill();
            if(setbonusskill != Skills.none) Skills_onepart.put(setbonusskill, Skills_onepart.getOrDefault(setbonusskill, 0) + 1);
            if(groupskill != Skills.none) Skills_onepart.put(groupskill, Skills_onepart.getOrDefault(groupskill, 0) + 1);

             */

            aktuellausgewahlt.setText("Momentan ausgewähltes \n Rüstungsteil:");
            selectedArmorLabel.setText(lastSelectedArmor.getName());
            defenseLabel.setText("Defense: " + defense_onepart);
            resistanceLabel.setText(String.format("Fire: %d\nWater: %d\nThunder: %d\nIce: %d\nDragon: %d",
                    resistances_onepart[0], resistances_onepart[1], resistances_onepart[2], resistances_onepart[3], resistances_onepart[4]));


            StringBuilder skillsText2 = new StringBuilder();
            Skills_onepart.forEach((skill, level) -> {
                skillsText2.append(skill.toString().replace("_", " "))
                        .append(": ")
                        .append(level)
                        .append("\n");
            });
            skillsDetailLabel.setText(skillsText2.toString());
            Skills setbonusskill = lastSelectedArmor.getSetbonusskill();
            Skills groupskill = lastSelectedArmor.getGroupSkill();
            if(setbonusskill != Skills.none) setbonusskillLabel_aktuell.setText("Setboni: \n" + setbonusskill.toString().replace("_", " "));
            if(groupskill != Skills.none) groupskillLabel_aktuell.setText("Gruppenskills: \n" + groupskill.toString().replace("_", " "));

        }

        // Anzeige der Details in den Labels
        totalDefenseLabel.setText("Total Defense: " + totalDefense);
        fireResistanceLabel.setText("Fire: " + totalResistances[0]);
        waterResistanceLabel.setText("Water: " + totalResistances[1]);
        thunderResistanceLabel.setText("Thunder: " + totalResistances[2]);
        iceResistanceLabel.setText("Ice: " + totalResistances[3]);
        dragonResistanceLabel.setText("Dragon: " + totalResistances[4]);

        // Anzeige der Skills
        StringBuilder skillsText = new StringBuilder();
        totalSkills.forEach((skill, level) -> {
            skillsText.append(skill.toString().replace("_", " "))
                    .append(": ")
                    .append(level)
                    .append("\n");
        });
        StringBuilder setskillstext = new StringBuilder();
        setboniskills.forEach((skill, level) -> {
            setskillstext.append(skill.toString().replace("_", " "))
                    .append(": ")
                    .append(level)
                    .append("\n");
        });
        StringBuilder groupskillstext = new StringBuilder();
        groupboniskills.forEach((skill, level) -> {
            groupskillstext.append(skill.toString().replace("_", " "))
                    .append(": ")
                    .append(level)
                    .append("\n");
        });
        skillsLabel.setText(skillsText.toString());
        setbonusskillLabel.setText("Setboni: \n" + setskillstext.toString());
        groupskillLabel.setText("Gruppenskills: \n" + groupskillstext.toString());
        //selectedArmorLabel.setText(selectedArmorText.toString());
    }

    private void loadComboBox(ComboBox<Armor> comboBox, String type) {
        List<Armor> filteredList = armorList.stream()
                .filter(armor -> armor.getType_as_String().equalsIgnoreCase(type))
                .toList();

        comboBox.getItems().addAll(filteredList);
        // Keine Standardauswahl setzen, sondern null als ausgewählten Wert.
        comboBox.setValue(null); // Keine Auswahl beim Start

        // Setzt den Namen als Anzeige
        setupComboBoxDisplay(comboBox);
    }


    private void setupComboBoxDisplay(ComboBox<Armor> comboBox) {
        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Armor armor) {
                return (armor != null) ? armor.getName() : "";
            }

            @Override
            public Armor fromString(String string) {
                return null; // Nicht nötig, da Auswahl aus Liste erfolgt
            }
        });
    }



    // Event-Handler für ComboBoxen, der die zuletzt genutzte ComboBox speichert
    private void setupComboBoxListeners() {
        headCombo.setOnAction(e -> {
            lastUsedComboBox = headCombo; // Merke die zuletzt genutzte ComboBox
            updateArmorDetails();
        });
        torsoCombo.setOnAction(e -> {
            lastUsedComboBox = torsoCombo; // Merke die zuletzt genutzte ComboBox
            updateArmorDetails();
        });
        armsCombo.setOnAction(e -> {
            lastUsedComboBox = armsCombo; // Merke die zuletzt genutzte ComboBox
            updateArmorDetails();
        });
        legsCombo.setOnAction(e -> {
            lastUsedComboBox = legsCombo; // Merke die zuletzt genutzte ComboBox
            updateArmorDetails();
        });
        waistCombo.setOnAction(e -> {
            lastUsedComboBox = waistCombo; // Merke die zuletzt genutzte ComboBox
            updateArmorDetails();
        });
    }

    @FXML
    private void onSearch() {
        String query = searchField.getText().toLowerCase().trim(); // Eingabe holen und formatieren

        if (query.isEmpty()) {
            searchResultsList.getItems().clear(); // Falls das Feld leer ist, Liste leeren
            return;
        }

        // Liste filtern
        List<Armor> filteredArmor = armorList.stream()
                .filter(armor ->
                        armor.getName().toLowerCase().contains(query) ||  // Suche im Namen
                                armor.getBonis().stream().anyMatch(bonus ->
                                        bonus.getSkill().toString().replace("_", " ").toLowerCase().contains(query) || // Suche nach Skill-Namen
                                                String.valueOf(bonus.getSkillevel()).equals(query) // Suche nach Skill-Leveln
                                )
                )
                .toList();

        // Gefilterte Ergebnisse in die ListView setzen
        searchResultsList.getItems().setAll(filteredArmor);
    }

    @FXML
    private void saveArmorSet() {
        if (headCombo.getValue() == null || torsoCombo.getValue() == null || armsCombo.getValue() == null ||
                legsCombo.getValue() == null || waistCombo.getValue() == null) {
            System.out.println("Bitte wähle für jeden Slot ein Rüstungsteil!");
            return;
        }

        // Set-Namen eingeben lassen
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Set speichern");
        dialog.setHeaderText("Gib einen Namen für das Rüstungsset ein:");
        Optional<String> result = dialog.showAndWait();

        if (result.isEmpty() || result.get().trim().isEmpty()) {
            System.out.println("Ungültiger Name!");
            return;
        }

        String setName = result.get().trim();

        // Neues Set erstellen
        JsonObject newSet = new JsonObject();
        newSet.addProperty("name", setName);
        newSet.addProperty("head", headCombo.getValue().getName());
        newSet.addProperty("torso", torsoCombo.getValue().getName());
        newSet.addProperty("arms", armsCombo.getValue().getName());
        newSet.addProperty("legs", legsCombo.getValue().getName());
        newSet.addProperty("waist", waistCombo.getValue().getName());

        // Vorhandene JSON-Daten laden
        Gson gson = new Gson();
        List<JsonObject> armorSets = new ArrayList<>();
        try (Reader reader = new FileReader(SAVE_FILE_PATH)) {
            JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
            jsonArray.forEach(el -> armorSets.add(el.getAsJsonObject()));
        } catch (IOException e) {
            System.out.println("Erstelle neue Datei...");
        }

        // Neues Set hinzufügen und speichern
        armorSets.add(newSet);
        try (Writer writer = new FileWriter(SAVE_FILE_PATH)) {
            gson.toJson(armorSets, writer);
            System.out.println("Set gespeichert!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<ArmorSet> loadedArmorSets = loadArmorSetsFromJson();
        armorSetListView.getItems().clear();
        armorSetListView.getItems().addAll(loadedArmorSets);
    }


    @FXML
    private void loadArmorSets() {
        List<String> setNames = new ArrayList<>();
        List<JsonObject> armorSets = new ArrayList<>();

        try (Reader reader = new FileReader(SAVE_FILE_PATH)) {
            JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
            jsonArray.forEach(el -> {
                JsonObject obj = el.getAsJsonObject();
                armorSets.add(obj);
                setNames.add(obj.get("name").getAsString());
            });
        } catch (IOException e) {
            System.out.println("Keine gespeicherten Sets gefunden.");
            return;
        }

        // Auswahlmenü anzeigen
        ChoiceDialog<String> dialog = new ChoiceDialog<>(null, setNames);
        dialog.setTitle("Set laden");
        dialog.setHeaderText("Wähle ein gespeichertes Rüstungsset:");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(setName -> {
            // Gewähltes Set suchen
            for (JsonObject obj : armorSets) {
                if (obj.get("name").getAsString().equals(setName)) {
                    loadSetIntoComboBoxes(obj);
                    break;
                }
            }
        });
    }

    private void loadSetIntoComboBoxes(JsonObject set) {
        headCombo.setValue(findArmorByName(set.get("head").getAsString()));
        torsoCombo.setValue(findArmorByName(set.get("torso").getAsString()));
        armsCombo.setValue(findArmorByName(set.get("arms").getAsString()));
        legsCombo.setValue(findArmorByName(set.get("legs").getAsString()));
        waistCombo.setValue(findArmorByName(set.get("waist").getAsString()));

        updateArmorDetails();
    }

    // Hilfsfunktion: Armor-Objekt anhand des Namens finden
    private Armor findArmorByName(String name) {
        return armorList.stream()
                .filter(armor -> armor.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @FXML
    private void exportArmorSets() {
        // Zeige den Datei-Explorer an, um eine Datei zum Speichern auszuwählen
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportiere Armor Sets");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Dateien", "*.json"));

        // Dialog zum Auswählen der Speicherdatei
        File file = fileChooser.showSaveDialog(null);
        if (file == null) return; // Falls der Nutzer den Dialog schließt

        // Sammle die ausgewählten Armor Sets
        List<ArmorSet> selectedSets = armorSetListView.getSelectionModel().getSelectedItems();
        if (selectedSets.isEmpty()) {
            System.out.println("Kein Armor Set ausgewählt!");
            return; // Wenn nichts ausgewählt wurde
        }

        // Falls die Datei existiert, laden wir die alten Sets
        try (Reader reader = new FileReader(SAVE_FILE_PATH)) {
            //JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
            JsonArray jsonArray = new JsonArray();

            // Füge die ausgewählten Armor Sets zur JSON hinzu
            for (ArmorSet set : selectedSets) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", set.getName());
                jsonObject.addProperty("head", set.getHead().getName());
                jsonObject.addProperty("torso", set.getTorso().getName());
                jsonObject.addProperty("arms", set.getArms().getName());
                jsonObject.addProperty("legs", set.getLegs().getName());
                jsonObject.addProperty("waist", set.getWaist().getName());

                jsonArray.add(jsonObject); // Armor Set zur JSON hinzufügen
            }

            // Schreibe die aktualisierte JSON in die ausgewählte Datei
            try (Writer writer = new FileWriter(file)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(jsonArray, writer);
                System.out.println("Armor Sets exportiert nach: " + file.getAbsolutePath());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    @FXML
    private void importArmorSets() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importiere Armor Sets");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Dateien", "*.json"));

        File file = fileChooser.showOpenDialog(null);
        if (file == null) return; //Für den Fall das die 0,5% eintreten

        List<JsonObject> importedSets = new ArrayList<>();
        try (Reader reader = new FileReader(file)) {
            JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
            jsonArray.forEach(el -> importedSets.add(el.getAsJsonObject()));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Vorhandene Sets laden
        List<JsonObject> existingSets = new ArrayList<>();
        try (Reader reader = new FileReader(SAVE_FILE_PATH)) {
            JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
            jsonArray.forEach(el -> existingSets.add(el.getAsJsonObject()));
        } catch (IOException e) {
            System.out.println("Erstelle neue Datei...");
        }

        // Doppelte Sets vermeiden
        for (JsonObject importedSet : importedSets) {
            boolean exists = existingSets.stream()
                    .anyMatch(set -> set.get("name").getAsString().equals(importedSet.get("name").getAsString()));

            if (!exists) {
                existingSets.add(importedSet);
            }
        }

        // Speichern
        try (Writer writer = new FileWriter(SAVE_FILE_PATH)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(existingSets, writer);
            System.out.println("Armor Sets importiert!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (file != null) {
            // Wenn eine Datei ausgewählt wurde, lade die Sets aus der JSON
            List<ArmorSet> importedArmorSets = loadArmorSetsFromJson();

            // Die ListView mit den importierten Sets aktualisieren
            armorSetListView.getItems().setAll(importedArmorSets);
            System.out.println("Armor Sets erfolgreich importiert.");
        }

        List<ArmorSet> loadedArmorSets = loadArmorSetsFromJson();
        armorSetListView.getItems().addAll(loadedArmorSets);
    }

    private List<ArmorSet> loadArmorSetsFromJson() {
        List<ArmorSet> armorSets = new ArrayList<>();

        try (Reader reader = new FileReader(SAVE_FILE_PATH)) {
            JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();

            for (JsonElement element : jsonArray) {
                JsonObject obj = element.getAsJsonObject();
                String name = obj.get("name").getAsString();
                String head_name = obj.get("head").getAsString();
                String torso_name = obj.get("torso").getAsString();
                String arms_name = obj.get("arms").getAsString();
                String legs_name = obj.get("legs").getAsString();
                String waist_name = obj.get("waist").getAsString();

                // Suche die entsprechenden Armor-Objekte basierend auf den Namen
                Armor head = findArmorByName(head_name);
                Armor torso = findArmorByName(torso_name);
                Armor arms = findArmorByName(arms_name);
                Armor legs = findArmorByName(legs_name);
                Armor waist = findArmorByName(waist_name);

                ArmorSet set = new ArmorSet(name, head, torso, arms, legs, waist);
                armorSets.add(set);
            }
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("ja noch nicht da. Controller Methode loadArmorSetsFromJson");
        }

        return armorSets;
    }


}