package com.example.monsterhunter;
import com.example.monsterhunter.armorpieces.Armor;
import com.example.monsterhunter.armorpieces.Boni;
import com.example.monsterhunter.armorpieces.Skills;
import com.example.monsterhunter.armorpieces.Type;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class ArmorScraper {
    public static int counter = 0;
    public static void main(String[] args) throws IOException {
        boolean bonicheck = false;
        InputStream inputStream = ArmorScraper.class.getResourceAsStream("/com/example/monsterhunter/armorfile.txt");
        if (inputStream == null) {
            System.out.println("Datei nicht gefunden!");
        } else {

            // Pfad zur .txt-Datei anpassen
            //File file = new File("armors/armorfile_helmet.txt");

            List<Armor> armorList = new ArrayList<>();


            //try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                StringBuilder bonistring = new StringBuilder();
                Armor temp = new Armor("fuck off", "nope", "Helmet", 0, 0, new int[5], new ArrayList<>(), new ArrayList<>(),null, null, 0, new HashMap<>());
                String line;
                while ((line = reader.readLine()) != null) {
                    //if(line.contains("<td class=\"center\">")){
                    if(!line.contains("<") && !line.trim().isBlank() && !bonicheck){
                        bonistring.setLength(0);
                        bonicheck = true;
                    }

                    if(line.contains("</td>") && bonicheck){
                        line = bonistring.toString();
                        bonicheck = false;
                    }

                    bonistring.append(line).append(" ");

                    if(line.equals("</tr><tr>")){
                        //Armor final_armor = new Armor(temp.getName(), temp.getType_as_String(), temp.getDefense(), temp.getResistances(), temp.getSlots(), temp.getBonis());
                        armorList.add(new Armor(temp.getName(),
                                temp.getDescription(),
                                temp.getType_as_String(),
                                temp.getDefense_base(),
                                temp.getDefense_max(),
                                Arrays.copyOf(temp.getResistances(),
                                        temp.getResistances().length),
                                new ArrayList<>(temp.getSlots()),
                                new ArrayList<>(temp.getBonis()),
                                temp.getSetbonusskill(),
                                temp.getGroupSkill(),
                                temp.getRarity(),
                                temp.getCrafting_requirements()));
                    }
                    if(!bonicheck) processLine(line, temp);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONArray armorArray = new JSONArray();

            for(Armor armor1: armorList){
                JSONObject armor = new JSONObject();
                armor.put("name", armor1.getName());
                armor.put("type", armor1.getType_as_String());
                //armor.put("defense", armor1.getDefense());
                armor.put("resistances", new JSONArray(armor1.getResistances()));
                armor.put("slots", new JSONArray(armor1.getSlots()));
                armor.put("bonis", new JSONArray(armor1.getBonis_as_String()));
                armor.put("setbonusskill", armor1.getSetbonusskill().toString());
                armor.put("groupSkill", armor1.getGroupSkill().toString());
                armor.put("rarity", armor1.getRarity());
                armorArray.put(armor);
            }

            File dir = new File("armors");
            if (!dir.exists()) {
                dir.mkdir();  // Ordner erstellen
            }
            // Pfad zur JSON-Datei
            File file = new File(dir, "armor.json");
            //try (FileWriter writer = new FileWriter("armors/armor.json")) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(armorArray.toString(4)); // Pretty-Print mit Einrückung
                System.out.println("JSON-Datei gespeichert!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void processLine(String line, Armor temp) {
        // Name der Rüstung extrahieren (z.B. "Chatacabra Vambraces")
        String namePattern = "<img[^>]*alt='([^']+)'";
        Pattern pattern = Pattern.compile(namePattern);
        Matcher matcher = pattern.matcher(line);
        String name = "Unknown";
        if (matcher.find() && counter == 0) {
            name = matcher.group(1);
            temp.setName(name);
            counter++;
        }

        // Typ der Rüstung extrahieren (z.B. "Arms" oder "Legs")
        String typePattern = "<td class=\"center\">([^<]+)</td>";
        pattern = Pattern.compile(typePattern);
        matcher = pattern.matcher(line);
        String type = "Unknown";
        if (matcher.find()) {
            type = matcher.group(1).trim();
            temp.setType(Type.valueOf(type));
        }

        // Base Defense extrahieren (z.B. "Base: 6")
        String defensePattern = "<b class='a-bold'>Base</b>:\\s*(\\d+)";
        pattern = Pattern.compile(defensePattern);
        matcher = pattern.matcher(line);
        int defense = 0;
        if (matcher.find()) {
            defense = Integer.parseInt(matcher.group(1).trim());
            //temp.setDefense(defense);
        }

        // Boni extrahieren (z.B. "Stamina Surge Lv. 1")
        String bonusPattern = "\s*([A-Za-z\s]+)\s*Lv\\.\s*(\\d+)";
        pattern = Pattern.compile(bonusPattern);
        matcher = pattern.matcher(line);
        List<Boni> bonuses = new ArrayList<>();
        while (matcher.find()) {
            String bonus = matcher.group(1).trim() + " Lv. " + matcher.group(2).trim();
            String skillname = matcher.group(1).trim().replace(" ", "_");
            Skills skills = Skills.valueOf(skillname);
            int skillvalue = Integer.parseInt(matcher.group(2).trim());
            Boni bonus_final = new Boni(skills, skillvalue);
            bonuses.add(bonus_final);
            temp.setBonis(bonuses);
        }

        // Resistenzen extrahieren (z.B. "Fire Element: 1")
        String resistancePattern = "<div class='align'><img[^>]*alt='([^']+)'[^>]*>(\\s*-?\\d+)";
        pattern = Pattern.compile(resistancePattern);
        matcher = pattern.matcher(line);
        while (matcher.find()) {
           // resistances.put(matcher.group(1).trim(), Integer.parseInt(matcher.group(2).trim()));
            if(counter == 1){
                temp.getResistances()[0] = Integer.parseInt(matcher.group(2).trim());
                counter++;
            }else if(counter == 2){
                temp.getResistances()[1] = Integer.parseInt(matcher.group(2).trim());
                counter++;
            }
            else if(counter == 3){
                temp.getResistances()[2] = Integer.parseInt(matcher.group(2).trim());
                counter++;
            }
            else if(counter == 4){
                temp.getResistances()[3] = Integer.parseInt(matcher.group(2).trim());
                counter++;
            }
            else{
                temp.getResistances()[4] = Integer.parseInt(matcher.group(2).trim());
                counter = 0;
            }
        }

        /*
        // Ausgabe der extrahierten Daten
        System.out.println("Name: " + name);
        System.out.println("Type: " + type);
        System.out.println("Base Defense: " + defense);
        System.out.println("Bonuses: " + bonuses);
        System.out.println("Resistances: " + resistances);
        System.out.println("------------------------------------------------------");

         */
    }
}
