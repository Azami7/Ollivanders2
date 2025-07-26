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
 * Protego horribilis stops dark arts spells entering the protected area, except the Killing Curse (which is too strong).
 * <p>
 * {@link net.pottercraft.ollivanders2.spell.PROTEGO_HORRIBILIS}
 *
 * @author Azami7
 * @version Ollivanders2
 * @see <a href = "https://harrypotter.fandom.com/wiki/Protego_horribilis">https://harrypotter.fandom.com/wiki/Protego_horribilis</a>
 */
public class PROTEGO_HORRIBILIS extends ShieldSpell {
    /**
     * min radius for this spell
     */
    public static final int minRadiusConfig = 5;
    /**
     * max radius for this spell
     */
    public static final int maxRadiusConfig = 30;
    /**
     * min duration for this spell
     */
    public static final int minDurationConfig = Ollivanders2Common.ticksPerSecond * 30;
    /**
     * max duration for this spell
     */
    public static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 30;

    /**
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public PROTEGO_HORRIBILIS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.PROTEGO_HORRIBILIS;
    }

    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the player who cast the spell
     * @param location the center location of the spell
     * @param radius   the radius for this spell
     * @param duration the duration of the spell
     */
    public PROTEGO_HORRIBILIS(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);
        spellType = O2StationarySpellType.PROTEGO_HORRIBILIS;
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;

        setRadius(radius);
        setDuration(duration);

        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
    }

    /**
     * Age the spell by a tick and kill projectiles crossing the boundaries
     */
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
     * Stop any dark arts spells from crossing the shield boundary, except avada kadavra
     */
    @Override
    void doOnSpellProjectileMoveEvent(@NotNull OllivandersSpellProjectileMoveEvent event) {
        O2SpellType type = event.getSpell().spellType;
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