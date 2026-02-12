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
 * Cave Inimicum produces a boundary that keeps the caster hidden from view. Those who were on the other side of the
 * shield are not able to see, hear, or (if the spell was well cast) smell them.
 * <p>
 * {@link net.pottercraft.ollivanders2.spell.CAVE_INIMICUM}
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Cave_inimicum">https://harrypotter.fandom.com/wiki/Cave_inimicum</a>
 * @since 2.21
 */
public class CAVE_INIMICUM extends ConcealmentShieldSpell {
    /**
     * min radius for this spell
     */
    public static final int minRadiusConfig = 5;
    /**
     * max radius for this spell
     */
    public static final int maxRadiusConfig = 20;
    /**
     * min duration for this spell
     */
    public static final int minDurationConfig = Ollivanders2Common.ticksPerSecond * 30;
    /**
     * max duration for this spell
     */
    public static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 30;

    private final String proximityAlarmMessage = "A hostile entity approaches.";

    /**
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public CAVE_INIMICUM(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.CAVE_INIMICUM;
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
    public CAVE_INIMICUM(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location, radius, duration);

        alarmOnProximity = true;
        proximityRadiusModifier = 5; // alarm if a player or hostile entity gets within 5 blocks of the spell area

        spellType = O2StationarySpellType.CAVE_INIMICUM;
    }

    /**
     * Set the min/max values for radius and duration.
     */
    void initRadiusAndDurationMinMax() {
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
    }

    /**
     * Can this entity see players inside the spell area? Assumes the entity being checked is outside the spell area.
     *
     * @param entity the entity looking inside the area
     * @return false, this spell conceals players in the area from everyone
     */
    protected boolean canSee(@NotNull LivingEntity entity) {
        return isLocationInside(entity.getLocation());
    }

    /**
     * Can this entity target players inside the spell area? Assumes the entity being checked is outside the spell area.
     *
     * @param entity the entity targeting inside the area
     * @return true, this spell does not affect targeting
     */
    @Override
    public boolean canTarget(@NotNull LivingEntity entity) {
        return true;
    }

    /**
     * Can this entity enter the spell area? Assumes the entity being checked is outside the spell area.
     *
     * @param entity the entity entering the area
     * @return true, this spell does not block entry in to the spell area
     */
    public boolean canEnter(@NotNull LivingEntity entity) {
        return true;
    }

    /**
     * Can this entity "hear" sounds from inside the spell area? Assumes the entity being checked is outside the spell area.
     *
     * @param entity the entity entering the area
     * @return false, this spell conceals players in the area from everyone
     */
    protected boolean canHear(@NotNull LivingEntity entity) {
        return false;
    }

    /**
     * Activate the proximity alarm if there is a player at the location. Assumes that a check to determine
     * that a proximity alarm should go off for this location has happened and called this.
     *
     * @param player the player that triggered the alarm
     */
    protected boolean checkAlarm(@NotNull Player player) {
        return true;
    }

    /**
     * Activate the proximity alarm if there is a player or hostile mob at the location. Assumes that a check to determine
     * that a proximity alarm should go off for this location has happened and called this.
     *
     * @param entity the entity that triggered the alarm
     */
    protected boolean checkAlarm(@NotNull LivingEntity entity) {
        return EntityCommon.isHostile(entity);
    }

    /**
     * Do the proximity alarm action for this spell.
     */
    protected void proximityAlarm() {
        if (proximityCooldownTimer > 0)
            return;

        for (Player player : getPlayersInsideSpellRadius()) {
            player.sendMessage(Ollivanders2.chatColor + proximityAlarmMessage);
        }

        proximityCooldownTimer = proximityCooldownLimit;
    }

    public String getProximityAlarmMessage() {
        return proximityAlarmMessage;
    }
}
