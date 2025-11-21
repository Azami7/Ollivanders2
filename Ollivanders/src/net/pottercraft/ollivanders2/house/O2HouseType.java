package net.pottercraft.ollivanders2.house;

import net.pottercraft.ollivanders2.common.O2Color;
import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

/**
 * Enumeration of Hogwarts houses with customizable names, colors, and points tracking.
 *
 * <p>O2HouseType defines the four houses of Hogwarts: Gryffindor, Hufflepuff, Ravenclaw, and Slytherin.
 * Each house has a customizable display name, an associated chat color for player identification, and a
 * points score that tracks performance in the house cup. Houses are customizable via configuration, allowing
 * servers to use alternative names and colors while maintaining the house system structure.</p>
 *
 * <p>House Properties:</p>
 * <ul>
 * <li>Display name: customizable via configuration (default: traditional house names)</li>
 * <li>Chat color: customizable via O2Color enum, used for team display names</li>
 * <li>Score: points accumulated for house cup standings, clamped to non-negative values</li>
 * </ul>
 *
 * @author Azami7
 * @see O2Color for available color options
 * @see O2Houses for house management and score tracking
 */
public enum O2HouseType {
    /**
     * Gryffindor - The house of the brave, associated with red and gold.
     * Default color: Dark Red
     */
    GRYFFINDOR("Gryffindor", O2Color.DARK_RED),
    /**
     * Hufflepuff - The house of the loyal and hardworking, associated with yellow and black.
     * Default color: Gold
     */
    HUFFLEPUFF("Hufflepuff", O2Color.GOLD),
    /**
     * Ravenclaw - The house of the wise and intelligent, associated with blue and bronze.
     * Default color: Blue
     */
    RAVENCLAW("Ravenclaw", O2Color.BLUE),
    /**
     * Slytherin - The house of the ambitious and cunning, associated with green and silver.
     * Default color: Dark Green
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
     * <p>Returns the name of this house. This name is customizable via server configuration and may differ
     * from the default house name (e.g., a server might rename "Gryffindor" to a custom name).</p>
     *
     * @return the customizable display name of this house
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
     * Get text for displaying the ranking of a specific house in the house cup.
     *
     * <p>Calculates the current ranking of this house compared to all other houses based on their scores.
     * Returns text such as "in 1st place", "tied for 2nd place", or "in 4th place". Ties are detected by
     * comparing scores with other houses; the ranking is determined by counting how many other houses have
     * a higher score than this house. Ordinal suffixes (1st, 2nd, 3rd, 4th) are applied appropriately.</p>
     *
     * @param houseType the house to calculate ranking for
     * @return a human-readable string describing this house's ranking in the house cup,
     *         formatted as either "in Nth place" or "tied for Nth place"
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