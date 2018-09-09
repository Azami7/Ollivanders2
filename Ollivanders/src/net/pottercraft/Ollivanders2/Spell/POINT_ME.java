package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Spell which points you in the direction of north.
 *
 * @version Ollivanders2
 * @author autumnwoz
 */
public class POINT_ME extends Charms
{
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     */
    public POINT_ME (O2SpellType type)
    {
        super(type);

        flavorText.add("\"\'Point Me!\' he whispered again to his wand, and it spun around and pointed him to the right-hand one.\"");
        flavorText.add("The Four-Point Spell");

        text = "Points the player north.";
    }

    /**
     * Constructor for casting the spell.
     *
     * @param plugin
     * @param player
     * @param type
     * @param rightWand
     */
    public POINT_ME (Ollivanders2 plugin, Player player, O2SpellType type, Double rightWand)
    {
        super(plugin, player, type, rightWand);
    }


    @Override
    public void checkEffect ()
    {
        Location l = player.getLocation().clone();
        l.setYaw(180);
        player.teleport(l);
        kill();
    }
}
