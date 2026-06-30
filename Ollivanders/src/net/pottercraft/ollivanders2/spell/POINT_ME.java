package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Spell which points you in the direction of north.
 *
 * @author autumnwoz
 * @see <a href="https://harrypotter.fandom.com/wiki/Four-Point_Spell">Harry Potter Wiki - Four-Point Spell</a>
 */
public class POINT_ME extends O2Spell {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public POINT_ME(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.POINT_ME;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("\"'Point Me!' he whispered again to his wand, and it spun around and pointed him to the right-hand one.\"");
            add("The Four-Point Spell");
        }};

        text = "Points the player north.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public POINT_ME(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.POINT_ME;
        branch = O2MagicBranch.CHARMS;

        noProjectile = true;

        initSpell();
    }

    /**
     * Rotate the caster to face north, then end the spell.
     */
    @Override
    protected void doCheckEffect() {
        Location rotatedLocation = caster.getLocation();
        rotatedLocation.setYaw(180); // in Bukkit, yaw 180 faces north
        caster.teleport(rotatedLocation);

        kill();
    }
}
