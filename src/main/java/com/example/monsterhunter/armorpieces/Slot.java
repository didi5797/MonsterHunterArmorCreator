package com.example.monsterhunter.armorpieces;

public class Slot {
    private int level; // Stufe des Slots (1 bis 4)

    public Slot(){
    }

    public Slot(int level) {
        if (level < 1 || level > 4) {
            throw new IllegalArgumentException("Slot level must be between 1 and 4");
        }
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        if (level < 1 || level > 4) {
            throw new IllegalArgumentException("Slot level must be between 1 and 4");
        }
        this.level = level;
    }

    @Override
    public String toString() {
        return "Slot(Lv " + level + ")";
    }
}
