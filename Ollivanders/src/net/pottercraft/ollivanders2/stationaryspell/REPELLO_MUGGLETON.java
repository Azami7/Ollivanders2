package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * The Muggle-Repelling -Repello Muggletum - is a charm that prevents Muggles from seeing or entering an area. Any
 * non-magic person gets close to the vicinity of the enchantment remembers something urgent to do and leave.
 * <p>
 * {@link net.pottercraft.ollivanders2.spell.REPELLO_MUGGLETON}
 *
 * @author Azami7
 * @version Ollivanders2
 * @See <a href = "https://harrypotter.fandom.com/wiki/Muggle-Repelling_Charm">https://harrypotter.fandom.com/wiki/Muggle-Repelling_Charm</a>
 * @since 2.21
 */
public class REPELLO_MUGGLETON extends ConcealmentShieldSpell {
    /**
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public REPELLO_MUGGLETON(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.REPELLO_MUGGLETON;

        initMessages();

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
    public REPELLO_MUGGLETON(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location, radius, duration);

        spellType = O2StationarySpellType.REPELLO_MUGGLETON;

        initMessages();
    }

    private void initMessages() {
        messages.add("You just remembered you need to do something someplace else.");
        messages.add("You just recalled an important appointment you need to get to somewhere else.");
        messages.add("Why were you going that way? You want to go a different way.");
        messages.add("You realize you don't actually want to go that way.");
        messages.add("You hear someone behind you calling your name.");
    }

    /**
     * Is this entity a player and are they a Muggle?
     *
     * @param entity the entity to check
     * @return true if they are a muggle, false otherwise
     */
    private boolean isMuggle(Entity entity) {
        if (entity instanceof Player) {
            return p.getO2Player((Player) entity).isMuggle();
        }

        return false;
    }

    /**
     * Can this entity see players inside the spell area? Assumes the entity being checked is outside the spell area.
     *
     * @param entity the entity looking inside the area
     * @return true if the entity is not a Muggle, false otherwise
     */
    protected boolean canSee(@NotNull Entity entity) {
        boolean isMuggle = isMuggle(entity);

        if (isMuggle)
            common.printDebugMessage(entity.getName() + " cannot see players in this area", null, null, false);
        else
            common.printDebugMessage(entity.getName() + " can see players in this area", null, null, false);

        return !isMuggle;
    }

    /**
     * Can this entity target players inside the spell area? Assumes the entity being checked is outside the spell area.
     *
     * @param entity the entity targeting inside the area
     * @return true, this spell does not prevent targeting
     */
    @Override
    protected boolean canTarget(@NotNull Entity entity) {
        return true;
    }

    /**
     * Can this entity enter the spell area? Assumes the entity being checked is outside the spell area.
     *
     * @param entity the entity entering the area
     * @return true if the entity is not a Muggle, false otherwise
     */
    protected boolean canEnter(@NotNull Entity entity) {
        return !isMuggle(entity);
    }

    /**
     * Can this entity "hear" sounds from inside the spell area? Assumes the entity being checked is outside the spell area.
     *
     * @param entity the entity entering the area
     * @return true if the entity is not a Muggle, false otherwise
     */
    protected boolean canHear(@NotNull Entity entity) {
        return !isMuggle(entity);
    }

    /**
     * Check the proximity alarm conditions at the location.
     * Repello Muggleton does not have a proximity alarm.
     */
    protected boolean checkAlarm(@NotNull Location alertLocation) {
        return false;
    }

    /**
     * Do the proximity alarm action for this spell.
     * Repello Muggleton does not have a proximity alarm.
     */
    protected void proximityAlarm() {
    }
}