package com.example.monsterhunter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArmorParser {
    public static Map<String, Integer> parseCraftingMaterials(Map<String, Object> crafting) {
        Map<String, Integer> materialsMap = new HashMap<>();
        if (crafting.containsKey("materials")) {
            List<Map<String, Object>> materials = (List<Map<String, Object>>) crafting.get("materials");
            for (Map<String, Object> material : materials) {
                Map<String, Object> item = (Map<String, Object>) material.get("item");
                String itemName = (String) item.get("name");
                Integer quantity = (Integer) material.get("quantity");
                materialsMap.put(itemName, quantity);
            }
        }
        return materialsMap;
    }
}

