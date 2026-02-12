package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Cave Inimicum produces a boundary that keeps the caster hidden from view. Those who were on the other side of the
 * shield are not able to see, hear, or (if the spell was well cast) smell them.
 * <p>
 * {@link net.pottercraft.ollivanders2.stationaryspell.CAVE_INIMICUM}
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Cave_inimicum">https://harrypotter.fandom.com/wiki/Cave_inimicum</a>
 * @since 2.21
 */
public class CAVE_INIMICUM extends StationarySpell {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public CAVE_INIMICUM(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.CAVE_INIMICUM;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("\"That's as much as I can do. At the very least, we should know they're coming, I can't guarantee it will keep out Vol—\" -Hermione Granger");
            add("The Concealment Shield");
        }};

        text = "Cave Inimicum will hide players inside of it from players and hostile mobs outside the shield. It will also sound a proximity alarm if they get close to the shield area";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public CAVE_INIMICUM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.CAVE_INIMICUM;
        branch = O2MagicBranch.CHARMS;

        durationModifierInSeconds = 15;
        radiusModifier = 1;
        flairSize = 10;
        centerOnCaster = true;
        minRadius = net.pottercraft.ollivanders2.stationaryspell.CAVE_INIMICUM.minRadiusConfig;
        maxRadius = net.pottercraft.ollivanders2.stationaryspell.CAVE_INIMICUM.maxRadiusConfig;
        minDuration = net.pottercraft.ollivanders2.stationaryspell.CAVE_INIMICUM.minDurationConfig;
        maxDuration = net.pottercraft.ollivanders2.stationaryspell.CAVE_INIMICUM.maxDurationConfig;

        initSpell();
    }

    @Override
    protected O2StationarySpell createStationarySpell() {
        return new net.pottercraft.ollivanders2.stationaryspell.CAVE_INIMICUM(p, player.getUniqueId(), location, radius, duration);
    }
}


