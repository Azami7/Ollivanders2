package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.jetbrains.annotations.NotNull;

/**
 * Unlocking charm that removes COLLOPORTUS magical locks.
 *
 * <p>When the projectile hits a COLLOPORTUS stationary spell, the spell removes (kills) all COLLOPORTUS
 * spells at that location and displays a visual flair effect for player feedback. The projectile stops
 * when it hits a solid block via hasHitTarget(), but continues searching for COLLOPORTUS spells each tick
 * until it hits a block.</p>
 *
 * <p><strong>World Guard:</strong> Requires the INTERACT flag when WorldGuard is enabled.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Unlocking_Charm">Unlocking Charm</a>
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
     * Search for and remove COLLOPORTUS stationary spells at the projectile's location.
     *
     * <p>Each tick, checks if the projectile hit a solid block. If COLLOPORTUS spells are found at the
     * projectile's current location, kills them and displays a visual flair effect. The spell does not
     * stop the projectile on its own; it only stops when hasHitTarget() returns true (solid block hit).</p>
     */
    @Override
    protected void doCheckEffect() {
        if (hasHitTarget())
            kill();

        // check all the stationary spells in the location of the projectile for a Colloportus
        List<O2StationarySpell> colloportusSpellsAtLocation = Ollivanders2API.getStationarySpells().getActiveStationarySpellsAtLocationByType(location, O2StationarySpellType.COLLOPORTUS);

        if (colloportusSpellsAtLocation.isEmpty()) {
            common.printDebugMessage("No colloportus spells found at location", null, null, false);
            sendFailureMessage();
            return;
        }

        // kill the spells and flair for player feedback
        for (O2StationarySpell colloportus : colloportusSpellsAtLocation) {
            colloportus.kill();
            colloportus.flair(10);
        }
    }
}