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
 * The Muggle-Repelling Charm (Repello Muggleton) - a stationary concealment shield spell that prevents
 * non-magical people from seeing or entering a protected area.
 *
 * <p>When a muggle attempts to enter the spell area, they are repelled with an inexplicable compulsion to
 * go elsewhere - they remember urgent tasks or important appointments. Muggles also cannot see wizards or
 * witches who are concealed within the spell area, nor can they hear conversations happening inside.
 * Unlike some concealment spells, this charm does not trigger proximity alarms when muggles approach.</p>
 *
 * <p><strong>Behavior:</strong></p>
 * <ul>
 *   <li>Muggles cannot enter the spell area ({@link #canEnter(LivingEntity)} returns false)</li>
 *   <li>Muggles cannot see wizards inside ({@link #canSee(LivingEntity)} returns false)</li>
 *   <li>Muggles cannot hear conversations from inside ({@link #canHear(LivingEntity)} returns false)</li>
 *   <li>All entities (including muggles) can target players inside without restriction</li>
 *   <li>No proximity alarms are triggered when muggles approach the boundary</li>
 * </ul>
 *
 * @author Azami7
 * @version Ollivanders2
 * @see <a href="https://harrypotter.fandom.com/wiki/Muggle-Repelling_Charm">https://harrypotter.fandom.com/wiki/Muggle-Repelling_Charm</a>
 * @since 2.21
 */
public class REPELLO_MUGGLETON extends ConcealmentShieldSpell {
    /**
     * Minimum spell radius in blocks (5 blocks).
     * The smallest protected area that can be created with this spell.
     */
    public static final int minRadiusConfig = 5;

    /**
     * Maximum spell radius in blocks (20 blocks).
     * The largest protected area that can be created with this spell.
     */
    public static final int maxRadiusConfig = 20;

    /**
     * Minimum spell duration in ticks (30 seconds).
     * The shortest time a spell can persist after being cast.
     */
    public static final int minDurationConfig = Ollivanders2Common.ticksPerSecond * 30;

    /**
     * Maximum spell duration in ticks (30 minutes).
     * The longest time a spell can persist after being cast.
     */
    public static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 30;

    /**
     * Constructs a REPELLO_MUGGLETON spell from deserialized data.
     *
     * <p>Used only for loading saved spells from disk at server startup. This constructor should not be
     * called directly when casting a new spell - use the full constructor instead.</p>
     *
     * @param plugin a callback to the MC plugin
     */
    public REPELLO_MUGGLETON(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.REPELLO_MUGGLETON;

        initMessages();
    }

    /**
     * Constructs a new REPELLO_MUGGLETON spell cast by a player.
     *
     * <p>Creates the spell at the specified location with the given radius and duration.
     * Initializes entry denial messages that muggles will see when blocked from entering.</p>
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

    /**
     * Initializes the min/max radius and duration boundaries for this spell.
     *
     * <p>Sets the spell's radius and duration constraints to the configured values:
     * radius 5-20 blocks, duration 30 seconds to 30 minutes.</p>
     */
    void initRadiusAndDurationMinMax() {
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
    }

    /**
     * Initializes the entry denial messages muggles see when blocked.
     *
     * <p>Populates the spell's message list with various reasons that compel muggles to avoid entering
     * the protected area. A random message from this list is shown to each muggle that attempts entry.</p>
     */
    private void initMessages() {
        entryDenyMessages.add("You just remembered you need to do something someplace else.");
        entryDenyMessages.add("You just recalled an important appointment you need to get to somewhere else.");
        entryDenyMessages.add("Why were you going that way? You want to go a different way.");
        entryDenyMessages.add("You realize you don't actually want to go that way.");
        entryDenyMessages.add("You hear someone behind you calling your name.");
    }

    /**
     * Checks if an entity is a muggle (non-magical person).
     *
     * <p>Only players can be muggles. Non-player entities (mobs, animals, etc.) are not considered muggles
     * and are treated as magical beings for spell purposes.</p>
     *
     * @param entity the entity to check
     * @return true if the entity is a player marked as a muggle, false otherwise
     */
    private boolean isMuggle(Entity entity) {
        if (entity instanceof Player) {
            return p.getO2Player((Player) entity).isMuggle();
        }

        return false;
    }

    /**
     * Determines if an entity outside the spell can see players inside.
     *
     * <p>Muggles (non-magical people) cannot see wizards and witches concealed within the spell area.
     * Only magical beings have the ability to perceive those inside the protected zone.</p>
     *
     * @param entity the entity outside the spell area checking visibility
     * @return false if the entity is a muggle, true if they can see inside
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
     * Determines if an entity outside the spell can target players inside.
     *
     * <p>REPELLO_MUGGLETON does not prevent entities from targeting wizards inside the protected area.
     * Unlike some concealment spells, it focuses on hiding and preventing entry rather than blocking attacks.</p>
     *
     * @param entity the entity outside the spell area attempting to target
     * @return true - all entities (including muggles) can target inside this spell
     */
    @Override
    public boolean canTarget(@NotNull LivingEntity entity) {
        return true;
    }

    /**
     * Determines if an entity can enter the spell's protected area.
     *
     * <p>Only magical beings (wizards, witches, and non-player entities) can enter.
     * Muggles are prevented from entering and receive an entry denial message explaining their compulsion to leave.</p>
     *
     * @param entity the entity attempting to enter the spell area
     * @return false if the entity is a muggle, true if they can enter
     */
    public boolean canEnter(@NotNull LivingEntity entity) {
        return !isMuggle(entity);
    }

    /**
     * Determines if an entity outside the spell can hear conversations from inside.
     *
     * <p>Muggles cannot hear sounds from protected wizards inside the spell area.
     * Non-muggles can hear conversations normally.</p>
     *
     * @param entity the entity outside the spell area checking hearing capability
     * @return true if the entity is not a Muggle, false if muggles cannot hear
     */
    protected boolean canHear(@NotNull LivingEntity entity) {
        return !isMuggle(entity);
    }

    /**
     * Checks if a player near the spell boundary should trigger a proximity alarm.
     *
     * <p>REPELLO_MUGGLETON does not have proximity alarm functionality.
     * This spell silently repels muggles without alerting the protected players.</p>
     *
     * @param player the player outside the spell near the proximity boundary
     * @return always false - no alarms are triggered for this spell
     */
    protected boolean checkAlarm(@NotNull Player player) {
        return false;
    }

    /**
     * Checks if a non-player entity near the spell boundary should trigger a proximity alarm.
     *
     * <p>REPELLO_MUGGLETON does not have proximity alarm functionality.
     * Hostile entities approaching the boundary do not trigger alerts.</p>
     *
     * @param entity the entity outside the spell near the proximity boundary
     * @return always false - no alarms are triggered for this spell
     */
    protected boolean checkAlarm(@NotNull LivingEntity entity) {
        return false;
    }

    /**
     * Executes the proximity alarm action for this spell.
     *
     * <p>This method does nothing for REPELLO_MUGGLETON since the spell
     * does not have proximity alarm functionality.</p>
     */
    protected void proximityAlarm() {
    }
}