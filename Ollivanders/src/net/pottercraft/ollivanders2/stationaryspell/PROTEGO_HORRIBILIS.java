package net.pottercraft.ollivanders2.stationaryspell;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.events.OllivandersSpellProjectileMoveEvent;
import org.bukkit.Location;

import org.jetbrains.annotations.NotNull;

/**
 * Protego Horribilis: a stationary shield that blocks Dark Arts spells from crossing into its area. Avada Kedavra is
 * too powerful to stop and passes through.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Protego_horribilis">Harry Potter Wiki - Protego horribilis</a>
 */
public class PROTEGO_HORRIBILIS extends ShieldSpell {
    /**
     * Minimum spell radius, in blocks.
     */
    public static final int minRadiusConfig = 5;

    /**
     * Maximum spell radius, in blocks.
     */
    public static final int maxRadiusConfig = 30;

    /**
     * Minimum spell duration: 30 seconds.
     */
    public static final int minDurationConfig = Ollivanders2Common.ticksPerSecond * 30;

    /**
     * Maximum spell duration: 30 minutes.
     */
    public static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 30;

    /**
     * Constructor for loading a saved spell from disk; do not use to cast a new spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public PROTEGO_HORRIBILIS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.PROTEGO_HORRIBILIS;
    }

    /**
     * Constructor for casting a new Protego Horribilis spell.
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the UUID of the player who cast the spell
     * @param location the center location of the spell
     * @param radius   the radius for this spell, clamped to the min/max bounds
     * @param duration the duration in ticks, clamped to the min/max bounds
     */
    public PROTEGO_HORRIBILIS(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);
        spellType = O2StationarySpellType.PROTEGO_HORRIBILIS;

        setRadius(radius);
        setDuration(duration, false);

        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
    }

    @Override
    void initRadiusAndDurationMinMax() {
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
    }

    @Override
    public void upkeep() {
        age();
    }

    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        return new HashMap<>();
    }

    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
    }

    /**
     * Cancel a Dark Arts spell projectile as it crosses into the shielded area. Avada Kedavra and non-Dark-Arts spells
     * pass through.
     *
     * @param event the spell projectile move event
     */
    @Override
    void doOnSpellProjectileMoveEvent(@NotNull OllivandersSpellProjectileMoveEvent event) {
        O2SpellType type = event.getSpell().getSpellType();
        if (type == O2SpellType.AVADA_KEDAVRA)
            return;

        O2MagicBranch branch = event.getSpell().getMagicBranch();
        if (branch != O2MagicBranch.DARK_ARTS)
            return;

        Location toLocation = event.getTo();
        Location fromLocation = event.getFrom();

        if (isLocationInside(toLocation) && !isLocationInside(fromLocation)) {
            event.setCancelled(true);
        }
    }

    @Override
    void doCleanUp() {
    }
}