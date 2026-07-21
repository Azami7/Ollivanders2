package net.pottercraft.ollivanders2.house;

import net.pottercraft.ollivanders2.common.O2Color;
import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

/**
 * The four Hogwarts houses, each with a display name and chat color (both customizable via config) and a house-cup
 * score.
 *
 * @author Azami7
 * @see O2Color
 * @see O2Houses
 */
public enum O2HouseType {
    /**
     * Gryffindor, the house of the brave.
     */
    GRYFFINDOR("Gryffindor", O2Color.DARK_RED),
    /**
     * Hufflepuff, the house of the loyal and hardworking.
     */
    HUFFLEPUFF("Hufflepuff", O2Color.GOLD),
    /**
     * Ravenclaw, the house of the wise.
     */
    RAVENCLAW("Ravenclaw", O2Color.BLUE),
    /**
     * Slytherin, the house of the ambitious and cunning.
     */
    SLYTHERIN("Slytherin", O2Color.DARK_GREEN);

    private String name;
    private O2Color color;
    private int score;

    /**
     * Constructor
     *
     * @param name  the display name for this house
     * @param color the chat color for this house
     */
    O2HouseType(@NotNull String name, @NotNull O2Color color) {
        this.name = name;
        this.color = color;
        score = 0;
    }

    /**
     * Get the display name for this house.
     *
     * @return the display name of this house
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Set the display name for this house. Should only be done from within the House package.
     *
     * @param n the display name for this house
     */
    void setName(@NotNull String n) {
        if (!n.isEmpty())
            name = n;
    }

    /**
     * Get the chat color for this house
     *
     * @return the ChatColor for this house
     */
    @NotNull
    public ChatColor getChatColorCode() {
        return color.getChatColor();
    }

    /**
     * Get the string prefix for this house color
     *
     * @return the string prefix that sets this color
     */
    @NotNull
    public String getColorPrefix() {
        return color.getChatColorCode();
    }

    /**
     * Set the color for this house. Should only be done from within the House package. No change is
     * made if the string is not a valid ChatColor value.
     *
     * @param name the color as a string
     */
    void setColor(@NotNull String name) {
        O2Color c = null;

        try {
            c = O2Color.valueOf(name);
        }
        catch (Exception e) {
            Ollivanders2API.common.printDebugMessage(name + " is not a valid color", null, null, false);
        }

        if (c != null)
            color = c;
    }

    /**
     * Get the current score for this house.
     *
     * @return the score for this house
     */
    public int getScore() {
        return score;
    }

    /**
     * Get a house's house-cup ranking as display text, e.g. "in 1st place" or "tied for 2nd place".
     *
     * @param houseType the house to rank
     * @return the ranking text, formatted "in Nth place" or "tied for Nth place"
     */
    public static String getHousePlaceText(@NotNull O2HouseType houseType) {
        int score = houseType.getScore();
        boolean tied = false;
        int place = 1;

        for (O2HouseType type : O2HouseType.values()) {
            if (type == houseType)
                continue;

            if (type.getScore() > score)
                place = place + 1;
            else if (type.getScore() == score)
                tied = true;
        }

        StringBuilder placeString = new StringBuilder();
        if (tied)
            placeString.append("tied for ");
        else
            placeString.append("in ");

        if (place == 1)
            placeString.append("1st ");
        else if (place == 2)
            placeString.append("2nd ");
        else if (place == 3)
            placeString.append("3rd ");
        else
            placeString.append("4th ");

        placeString.append("place");

        return placeString.toString();
    }

    /**
     * Set the score for this house. Should only be done from within the House package.
     *
     * @param s the score to set
     */
    void setScore(int s) {
        if (s < 0)
            s = 0;

        score = s;
    }
}