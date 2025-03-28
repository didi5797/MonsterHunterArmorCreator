package com.example.monsterhunter.armorpieces;

import com.fasterxml.jackson.annotation.JsonSetter;

public class Boni {
    private Skills skill;
    private int skillevel;

    public Boni() {}

    public Boni(Skills skillName, int level) {
        this.skill = skillName;
        this.skillevel = level;
    }

    public Skills getSkill() { return skill; }
    public void setSkill(Skills skill) { this.skill = skill; }

    public int getSkillevel() { return skillevel; }
    public void setSkillevel(int skillevel) { this.skillevel = skillevel; }

    @JsonSetter("skill")
    public void setSkillFromJson(String skillName) {
        // Prüfen, ob der Skill in der Enum existiert, falls nicht setze "none"
        try {
            this.skill = Skills.valueOf(skillName.replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            this.skill = Skills.none; // Falls der Skill nicht gefunden wird, setze "none"
        }
    }


    @Override
    public String toString() {
        return "{ \"skill\": \"" + skill.toString() + "\", \"skillevel\": " + skillevel + " }";
    }
}

