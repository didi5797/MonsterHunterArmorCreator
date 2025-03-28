package com.example.monsterhunter.armorpieces;

import com.example.monsterhunter.ArmorParser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.apache.commons.lang3.EnumUtils;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Armor {
    private String name;
    private String description;
    private Type type; // Helmet, Chest, Arms, Legs, Boots
    private int defense_base;
    private int defense_max;
    private int[] resistances; // Fire, Water, Thunder, Ice, Dragon
    private List<Slot> slots; // Liste von Slots
    private List<Boni> bonis;
    private Skills setbonusskill;
    private Skills groupSkill;
    private int rarity;
    private Map<String, Integer> crafting_requirements = new HashMap<String, Integer>();

    // Leerer Konstruktor für Jackson
    public Armor() {}
    /*
    public Armor(String name, String type, int defense, int[] resistances, List<Slot> slots, List<Boni> bonis) {
        this.name = name;
        this.type = Type.valueOf(type);
        this.defense = defense;
        this.resistances = resistances;
        this.slots = slots;
        this.bonis = bonis;
    }

     */
    public Armor(String name, String description, String type, int defense_base, int defense_max, int[] resistances, List<Slot> slots, List<Boni> bonis, Skills setbonusskill, Skills groupSkill, int rarity, Map<String, Integer> crafting_requirements) {
        this.name = name;
        this.description = description;
        this.type = Type.valueOf(type);
        this.defense_base = defense_base;
        this.defense_max = defense_max;
        this.resistances = resistances;
        this.slots = slots;
        this.bonis = bonis;
        this.setbonusskill = setbonusskill;
        this.groupSkill = groupSkill;
        this.rarity = rarity;
        this.crafting_requirements = crafting_requirements;
    }

    @JsonSetter("kind")
    public void setType(String kind) {
        switch (kind.toLowerCase()) {
            case "head" -> this.type = Type.Head;
            case "chest" -> this.type = Type.Torso;
            case "arms" -> this.type = Type.Arms;
            case "legs" -> this.type = Type.Legs;
            case "waist" -> this.type = Type.Waist;
            default -> throw new IllegalArgumentException("Unknown armor type: " + kind);
        }
    }

    @JsonSetter("defense")
    public void setDefense(Map<String, Integer> defense) {
        this.defense_base = defense.get("base");
        this.defense_max = defense.get("max");
    }

    @JsonSetter("resistances")
    public void setResistances(Map<String, Integer> resistancesMap) {
        this.resistances = new int[]{
                resistancesMap.getOrDefault("fire", 0),
                resistancesMap.getOrDefault("water", 0),
                resistancesMap.getOrDefault("thunder", 0),
                resistancesMap.getOrDefault("ice", 0),
                resistancesMap.getOrDefault("dragon", 0)
        };
    }

    @JsonSetter("crafting")
    public void setCrafting(Map<String, Object> crafting) {
        if (crafting.containsKey("materials")) {
            this.crafting_requirements = ArmorParser.parseCraftingMaterials(crafting);
        }
    }

    @JsonSetter("skills")
    public void setSkills(List<Map<String, Object>> skills) {
        // Initialisiere die Liste, falls sie noch nicht gesetzt ist
        if (this.bonis == null) {
            this.bonis = new ArrayList<>();
        }

        // Variable für das Default (Standard) Skill
        Skills defaultSkill = Skills.none;

        // Iteration durch alle Skills
        for (Map<String, Object> skillEntry : skills) {
            Map<String, Object> skillData = (Map<String, Object>) skillEntry.get("skill");
            String kind = (String) skillData.get("kind");
            String skillName = skillData.get("name").toString().replace(" ", "_").replace("'", "").replace("/", "_").replace("-", "_");
            int level = (int) skillEntry.get("level");

            // Prüfen, ob der Skill in der Enum existiert
            Skills skillEnum = defaultSkill;  // Standardmäßig "none"
            try {
                if (EnumUtils.isValidEnum(Skills.class, skillName)) {
                    skillEnum = Skills.valueOf(skillName);
                }
            } catch (IllegalArgumentException e) {
                skillEnum = defaultSkill;  // Falls der Skill nicht gefunden wird, setze "none"
            }

            // Abhängig von der Art des Skills ("armor", "set", "group")
            switch (kind) {
                case "armor" -> this.bonis.add(new Boni(skillEnum, level));  // Für "armor" Skills werden Boni hinzugefügt
                case "set" -> this.setbonusskill = skillEnum;  // Für "set" Skill wird setbonusskill gesetzt
                case "group" -> this.groupSkill = skillEnum;  // Für "group" Skill wird groupSkill gesetzt
            }
        }

        // Falls keine setbonusskill und groupSkill gesetzt wurden, ensure default values
        if (this.setbonusskill == null) {
            this.setbonusskill = defaultSkill;
        }
        if (this.groupSkill == null) {
            this.groupSkill = defaultSkill;
        }
    }






    @JsonSetter("setbonusskill")
    public void setSetBonusSkill(String setbonusskill) {
        this.setbonusskill = Skills.valueOf((setbonusskill == null || setbonusskill.isEmpty()) ? "none" : setbonusskill);
    }

    @JsonSetter("groupSkill")
    public void setSetgroupSkill(String setgroupSkill) {
        this.groupSkill = Skills.valueOf((setgroupSkill == null || setgroupSkill.isEmpty()) ? "none" : setgroupSkill);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public String getType_as_String() {
        return type.toString();
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getDefense_base() {
        return defense_base;
    }

    public void setDefense_base(int defense_base) {
        this.defense_base = defense_base;
    }

    public int getDefense_max() {
        return defense_max;
    }

    public void setDefense_max(int defense_max) {
        this.defense_max = defense_max;
    }

    public int[] getResistances() {
        return resistances;
    }

    public void setResistances(int[] resistances) {
        this.resistances = resistances;
    }

    public List<Slot> getSlots() {
        return slots;
    }

    public void setSlots(List<Slot> slots) {
        this.slots = slots;
    }

    public List<Boni> getBonis() {
        return bonis;
    }

    public String getBonis_as_String() {
        return bonis.toString();
    }


    public void setBonis(List<Boni> bonis) {
        this.bonis = bonis;
    }

    public Skills getSetbonusskill() {
        return setbonusskill;
    }

    public void setSetbonusskill(Skills setbonusskill) {
        this.setbonusskill = setbonusskill;
    }

    public Skills getGroupSkill() {
        return groupSkill;
    }

    public void setGroupSkill(Skills groupSkill) {
        this.groupSkill = groupSkill;
    }

    public int getRarity() {
        return rarity;
    }

    public void setRarity(int rarity) {
        this.rarity = rarity;
    }

    public Map<String, Integer> getCrafting_requirements() {
        return crafting_requirements;
    }

    public void setCrafting_requirements(Map<String, Integer> crafting_requriements) {
        this.crafting_requirements = crafting_requriements;
    }

    @Override
    public String toString() {
        return "Armor{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", defense_base=" + defense_base +
                ", defense_max=" + defense_max +
                ", resistances=" + Arrays.toString(resistances) +
                ", slots=" + slots +
                ", bonis=" + bonis +
                ", setbonusskill=" + setbonusskill +
                ", groupSkill=" + groupSkill +
                ", rarity=" + rarity +
                ", crafting_requriements=" + crafting_requirements +
                '}';
    }
}
