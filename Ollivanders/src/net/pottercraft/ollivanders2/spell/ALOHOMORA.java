package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.COLLOPORTUS;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.jetbrains.annotations.NotNull;

/**
 * The unlocking spell - https://harrypotter.fandom.com/wiki/Unlocking_Charm
 */
public final class ALOHOMORA extends O2Spell {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public ALOHOMORA(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.ALOHOMORA;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("There are many ways to pass through locked doors in the magical world. When you wish to enter or depart discreetly, however, the Unlocking Charm is your best friend.");
            add("The Unlocking Charm");
        }};

        text = "Unlocks doors locked by Colloportus.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public ALOHOMORA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.ALOHOMORA;
        branch = O2MagicBranch.CHARMS;

        // world guard
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.INTERACT);

        initSpell();
    }

    /**
     * Checks for colloportus stationary spells and ages them, if found
     */
    @Override
    protected void doCheckEffect() {
        if (hasHitTarget())
            kill();

        // check all the stationary spells in the location of the projectile for a Colloportus
        List<O2StationarySpell> inside = new ArrayList<>();
        List<O2StationarySpell> stationarySpellsAtLocation = Ollivanders2API.getStationarySpells().getStationarySpellsAtLocation(location);

        if (stationarySpellsAtLocation.isEmpty()) {
            common.printDebugMessage("No stationary spells found at location", null, null, false);
            return;
        }

        for (O2StationarySpell spell : stationarySpellsAtLocation) {
            if (spell instanceof COLLOPORTUS) {
                common.printDebugMessage("Found a COLLOPORTUS spell", null, null, false);
                inside.add(spell);
            }
        }

        // remove the colloportus spells found
        if (!inside.isEmpty()) {
            for (O2StationarySpell spell : inside) {
                spell.kill();
                spell.flair(10);
            }

            kill();
        }
    }
}