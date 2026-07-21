package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Repello Muggleton (Muggle-Repelling Charm): a concealment shield that keeps muggles from entering, seeing into, or
 * hearing into its area. A muggle who tries to enter is turned away with a random compulsion message. It does not block
 * targeting and has no proximity alarm.
 *
 * @author Azami7
 * @version Ollivanders2
 * @see <a href="https://harrypotter.fandom.com/wiki/Muggle-Repelling_Charm">Harry Potter Wiki - Muggle-Repelling Charm</a>
 */
public class REPELLO_MUGGLETON extends ConcealmentShieldSpell {
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
    public REPELLO_MUGGLETON(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.REPELLO_MUGGLETON;

        initMessages();
    }

    /**
     * Constructor for casting a new Repello Muggleton spell.
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the UUID of the player who cast the spell
     * @param location the center location of the spell
     * @param radius   the initial radius of the protected area in blocks
     * @param duration the initial duration of the spell in ticks
     */
    public REPELLO_MUGGLETON(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location, radius, duration);

        spellType = O2StationarySpellType.REPELLO_MUGGLETON;

        initMessages();
    }

    void initRadiusAndDurationMinMax() {
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
    }

    /**
     * Populate the pool of entry-deny messages, one of which is shown at random to each muggle turned away.
     */
    private void initMessages() {
        entryDenyMessages.add("You just remembered you need to do something someplace else.");
        entryDenyMessages.add("You just recalled an important appointment you need to get to somewhere else.");
        entryDenyMessages.add("Why were you going that way? You want to go a different way.");
        entryDenyMessages.add("You realize you don't actually want to go that way.");
        entryDenyMessages.add("You hear someone behind you calling your name.");
    }

    /**
     * Whether an entity is a muggle. Only players can be muggles; all other entities are treated as magical.
     *
     * @param entity the entity to check
     * @return true if the entity is a player flagged as a muggle
     */
    private boolean isMuggle(Entity entity) {
        if (entity instanceof Player) {
            return p.getO2Player((Player) entity).isMuggle();
        }

        return false;
    }

    /**
     * @param entity the entity to check
     * @return true unless the entity is a muggle; muggles cannot see players concealed here
     */
    protected boolean canSee(@NotNull LivingEntity entity) {
        boolean isMuggle = isMuggle(entity);

        if (isMuggle)
            common.printDebugMessage(entity.getName() + " cannot see players in this area", null, null, false);
        else
            common.printDebugMessage(entity.getName() + " can see players in this area", null, null, false);

        return !isMuggle;
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
     * @return true unless the entity is a muggle; muggles are turned away at the boundary
     */
    public boolean canEnter(@NotNull LivingEntity entity) {
        return !isMuggle(entity);
    }

    /**
     * @param entity the entity to check
     * @return true unless the entity is a muggle; muggles cannot hear into the area
     */
    protected boolean canHear(@NotNull LivingEntity entity) {
        return !isMuggle(entity);
    }

    /**
     * @param player the player near the boundary
     * @return false; this spell has no proximity alarm
     */
    protected boolean checkAlarm(@NotNull Player player) {
        return false;
    }

    /**
     * @param entity the entity near the boundary
     * @return false; this spell has no proximity alarm
     */
    protected boolean checkAlarm(@NotNull LivingEntity entity) {
        return false;
    }

    /**
     * No-op; this spell has no proximity alarm.
     */
    protected void proximityAlarm() {
    }
}