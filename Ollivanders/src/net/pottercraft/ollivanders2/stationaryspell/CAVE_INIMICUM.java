package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Cave Inimicum: a concealment shield that hides the players inside its radius from everyone outside, who cannot see
 * or hear them. Cast by {@link net.pottercraft.ollivanders2.spell.CAVE_INIMICUM}.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Cave_inimicum">Harry Potter Wiki - Cave inimicum</a>
 */
public class CAVE_INIMICUM extends ConcealmentShieldSpell {
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
     * The message sent to players inside the area when the proximity alarm fires.
     */
    private final String proximityAlarmMessage = "A hostile entity approaches.";

    /**
     * Constructor for loading a saved spell from disk; do not use to cast a new spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public CAVE_INIMICUM(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.CAVE_INIMICUM;
    }

    /**
     * Constructor for casting a new Cave Inimicum spell.
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the player who cast the spell
     * @param location the center location of the spell
     * @param radius   the radius for this spell
     * @param duration the duration of the spell
     */
    public CAVE_INIMICUM(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location, radius, duration);

        alarmOnProximity = true;
        proximityRadiusModifier = 5; // alarm if a player or hostile entity gets within 5 blocks of the spell area

        spellType = O2StationarySpellType.CAVE_INIMICUM;
    }

    void initRadiusAndDurationMinMax() {
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
    }

    /**
     * @param entity the entity to check
     * @return true only if the entity is itself inside the area; outsiders cannot see players concealed here
     */
    protected boolean canSee(@NotNull LivingEntity entity) {
        return isLocationInside(entity.getLocation());
    }

    /**
     * @param entity the entity to check
     * @return true; this spell does not affect targeting
     */
    @Override
    public boolean canTarget(@NotNull LivingEntity entity) {
        return true;
    }

    /**
     * @param entity the entity to check
     * @return true; this spell does not block entry to the area
     */
    public boolean canEnter(@NotNull LivingEntity entity) {
        return true;
    }

    /**
     * @param entity the entity to check
     * @return false; this spell conceals sound from everyone outside the area
     */
    protected boolean canHear(@NotNull LivingEntity entity) {
        return false;
    }

    /**
     * @param player the player near the boundary
     * @return true; any player triggers this spell's alarm
     */
    protected boolean checkAlarm(@NotNull Player player) {
        return true;
    }

    /**
     * @param entity the entity near the boundary
     * @return true if the entity is hostile
     */
    protected boolean checkAlarm(@NotNull LivingEntity entity) {
        return EntityCommon.isHostile(entity);
    }

    /**
     * Message the players inside the area that a hostile entity is near, then start the alarm cooldown.
     */
    protected void proximityAlarm() {
        if (proximityCooldownTimer > 0)
            return;

        for (Player player : getPlayersInsideSpellRadius()) {
            player.sendMessage(Ollivanders2.chatColor + proximityAlarmMessage);
        }

        proximityCooldownTimer = proximityCooldownLimit;
    }

    /**
     * Get the message sent to the caster when proximity is triggered.
     *
     * @return the proximity alarm message
     */
    public String getProximityAlarmMessage() {
        return proximityAlarmMessage;
    }
}
