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
 * A stationary shield spell that muffles sound and prevents eavesdropping on conversations.
 *
 * <p>The Muffliato charm creates a protective barrier where conversations are silenced from outside
 * listeners. Only players inside the spell area can hear chats from other inside players. Players
 * outside the spell cannot hear any conversations occurring within the protected area, effectively
 * preventing eavesdropping. The spell lasts for a configurable duration based on caster skill level.</p>
 *
 * <p>Spell characteristics:</p>
 * <ul>
 *   <li>Radius: 5-20 blocks (configurable)</li>
 *   <li>Duration: 30 seconds to 30 minutes (configurable)</li>
 *   <li>Effect: Prevents outside players from hearing inside conversations</li>
 *   <li>Inside players can hear outside conversations</li>
 * </ul>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Muffliato_Charm">https://harrypotter.fandom.com/wiki/Muffliato_Charm</a>
 */
public class MUFFLIATO extends ShieldSpell {
    /**
     * Minimum spell radius (5 blocks).
     */
    public static final int minRadiusConfig = 5;

    /**
     * Maximum spell radius (20 blocks).
     */
    public static final int maxRadiusConfig = 20;

    /**
     * Minimum spell duration (30 seconds).
     */
    public static final int minDurationConfig = Ollivanders2Common.ticksPerSecond * 30;

    /**
     * Maximum spell duration (30 minutes).
     */
    public static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 30;

    /**
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public MUFFLIATO(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.MUFFLIATO;
    }

    /**
     * Constructs a new MUFFLIATO spell cast by a player.
     *
     * <p>Creates a muffliato charm at the specified location with the given radius and duration.
     * The spell will prevent outside players from hearing conversations within the protected area.</p>
     *
     * @param plugin   a callback to the MC plugin (not null)
     * @param pid      the UUID of the player who cast the spell (not null)
     * @param location the center location of the spell (not null)
     * @param radius   the radius for this spell (will be clamped to min/max values)
     * @param duration the duration of the spell in ticks (will be clamped to min/max values)
     */
    public MUFFLIATO(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);
        spellType = O2StationarySpellType.MUFFLIATO;

        setRadius(radius);
        setDuration(duration);

        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
    }

    /**
     * Initializes the radius and duration constraints for this spell.
     *
     * <p>Sets the spell's radius boundaries (5-20 blocks) and duration boundaries (30 seconds to 30 minutes).</p>
     */
    @Override
    void initRadiusAndDurationMinMax() {
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
    }

    /**
     * Age the spell by 1 tick
     */
    @Override
    public void upkeep() {
        age();
    }

    /**
     * Handles player chat events and prevents outside players from hearing inside conversations.
     *
     * <p>When a player inside the muffliato spell area speaks, this method removes all outside
     * recipients from the chat event, effectively silencing the conversation from outside listeners.
     * If the speaker is outside the spell, the method returns early without modification, allowing
     * inside players to hear outside conversations.</p>
     *
     * @param event the async player chat event (not null)
     */
    @Override
    void doOnAsyncPlayerChatEvent(@NotNull AsyncPlayerChatEvent event) {
        Player speaker = event.getPlayer(); // will never be null

        if (!isLocationInside(speaker.getLocation()))
            return;

        Set<Player> recipients = new HashSet<>(event.getRecipients());

        for (Player player : recipients) {
            if (!isLocationInside(player.getLocation()))
                event.getRecipients().remove(player);
        }
    }

    /**
     * Serializes the muffliato spell data for persistence.
     *
     * <p>The muffliato spell has no extra data to serialize beyond the base spell properties,
     * so this method returns an empty map.</p>
     *
     * @return an empty map (the spell has no custom data to serialize)
     */
    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        return new HashMap<>();
    }

    /**
     * Deserializes muffliato spell data from saved state.
     *
     * <p>The muffliato spell has no extra data to deserialize, so this method does nothing.</p>
     *
     * @param spellData the serialized spell data map (not used)
     */
    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
    }

    /**
     * Cleans up when the muffliato spell ends.
     *
     * <p>The muffliato spell requires no special cleanup on termination.</p>
     */
    @Override
    void doCleanUp() {
    }
}