package com.example.monsterhunter;

import com.example.monsterhunter.armorpieces.Armor;

public class ArmorSet {
    private String name; // Name des Sets
    private Armor head;
    private Armor torso;
    private Armor arms;
    private Armor legs;
    private Armor waist;

    // Konstruktor
    public ArmorSet(String name, Armor head, Armor torso, Armor arms, Armor legs, Armor waist) {
        this.name = name;
        this.head = head;
        this.torso = torso;
        this.arms = arms;
        this.legs = legs;
        this.waist = waist;
    }

    // Getter & Setter
    public String getName() { return name; }
    public Armor getHead() { return head; }
    public Armor getTorso() { return torso; }
    public Armor getArms() { return arms; }
    public Armor getLegs() { return legs; }
    public Armor getWaist() { return waist; }

    @Override
    public String toString() {
        return name + " || " + head.getName() + ", " + torso.getName() + ", " + arms.getName() + ", " + legs.getName() + ", " + waist.getName();
    }
}

