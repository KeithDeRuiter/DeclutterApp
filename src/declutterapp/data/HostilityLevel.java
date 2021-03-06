/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package declutterapp.data;

import java.awt.Color;

/**
 *
 * @author Keith
 */
public enum HostilityLevel {
    ALLY("Ally", new Color(100, 255, 100)),
    //NEUTRAL("Neutral", Color.BLUE),
    CIVILIAN("Civilian", new Color(100, 100, 255)),
    ENEMY("Enemy", new Color(255, 100, 100));
    
    
    
    /** The name of this hostility level. */
    private final String name;
    
    /** The color associated with this hostility level. */
    private final Color color;
    
    /** Construct an new instance of {@code HostilityLevel}. */
    private HostilityLevel(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    /**
     * Gets the name of this hostility level.
     * @return The name of this hostility level.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the color of this hostility level.
     * @return The color associated with this hostility level.
     */
    public Color getColor() {
        return color;
    }
    
    
    
}
