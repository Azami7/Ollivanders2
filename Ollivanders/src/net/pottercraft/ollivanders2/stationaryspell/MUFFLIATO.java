package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Muffliato: a stationary shield spell that keeps players outside its radius from hearing conversations spoken inside
 * it. Players inside can still hear players outside.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Muffliato_Charm">Harry Potter Wiki - Muffliato Charm</a>
 */
public class MUFFLIATO extends ShieldSpell {
    /**
     * Minimum spell radius, in blocks.
     */
    public static final int minRadiusConfig = 5;

    /**
     * Maximum spell radius, in blocks.
     */
    public static final int maxRadiusConfig = 20;

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
    public MUFFLIATO(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.MUFFLIATO;
    }

    /**
     * Constructor for casting a new Muffliato spell.
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the UUID of the player who cast the spell
     * @param location the center location of the spell
     * @param radius   the radius for this spell, clamped to the min/max bounds
     * @param duration the duration in ticks, clamped to the min/max bounds
     */
    public MUFFLIATO(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);
        spellType = O2StationarySpellType.MUFFLIATO;

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

    /**
     * When a player inside the area speaks, drop every recipient outside the area so the conversation stays private.
     *
     * @param event the async player chat event
     */
    @Override
    void doOnAsyncPlayerChatEvent(@NotNull AsyncPlayerChatEvent event) {
        Player speaker = event.getPlayer();

        if (!isLocationInside(speaker.getLocation()))
            return;

        Set<Player> recipients = new HashSet<>(event.getRecipients());

        for (Player player : recipients) {
            if (!isLocationInside(player.getLocation()))
                event.getRecipients().remove(player);
        }
    }

    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        return new HashMap<>();
    }

    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
    }

    @Override
    void doCleanUp() {
    }
}