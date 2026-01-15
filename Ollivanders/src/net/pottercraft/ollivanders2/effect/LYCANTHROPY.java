package net.pottercraft.ollivanders2.effect;

import java.util.ArrayList;
import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.MoonPhase;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.common.TimeCommon;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * Permanent lycanthropy curse that transforms player into a werewolf during full moons.
 *
 * <p>LYCANTHROPY is a permanent curse that transforms the affected player into a werewolf during
 * full moons (every 8 in-game days). The curse is tied to the lunar cycle: the player transforms
 * into wolf form after moonrise (game time > 13000) on full moon days and reverts to human form
 * before moonrise or after sunrise on full moon days, or on any non-full-moon day. While transformed,
 * additional effects are automatically applied: aggressive behavior (AGGRESSION with level 10) and
 * wolf-like speech (LYCANTHROPY_SPEECH). The effect is always permanent and cannot be modified
 * via setPermanent(). The curse can spread through damage dealt by angry wolves transformed by this
 * effect. Detectable by mind-reading spells (Legilimens) which report the player "is a werewolf".</p>
 *
 * <p>Transformation Cycle (based on in-game full moon every 8 days):</p>
 * <ul>
 * <li>Checks for full moon day and time after sunset (game time > 13000)</li>
 * <li>On full moon after moonrise: transforms to wolf, applies AGGRESSION and LYCANTHROPY_SPEECH</li>
 * <li>Before moonrise on full moon or after sunrise: reverts to human form, removes additional effects</li>
 * <li>On non-full-moon days: reverts to human form, removes additional effects</li>
 * </ul>
 *
 * <p>Infection Mechanism:</p>
 * <ul>
 * <li>Angry wolves transformed by this effect can infect other players through damage</li>
 * <li>Infection occurs 1 second (20 ticks) after damage is dealt to allow event processing</li>
 * <li>New infection creates new LYCANTHROPY effect on the damaged player</li>
 * </ul>
 *
 * <p>Relief Mechanism:</p>
 * <ul>
 * <li>LYCANTHROPY_RELIEF effect can temporarily suppress transformation</li>
 * <li>When relief is active, player does not transform even during full moon periods</li>
 * <li>Relief flag is managed by setRelief() and getRelief() methods</li>
 * </ul>
 *
 * @author azami7
 * @see AGGRESSION for the aggressive behavior applied during transformation
 * @see LYCANTHROPY_SPEECH for the wolf-like speech applied during transformation
 * @see ShapeShiftSuper for the shape-shifting transformation mechanism
 */
public class LYCANTHROPY extends ShapeShiftSuper {
    /**
     * Tracks additional effects applied during werewolf transformation.
     *
     * <p>Stores the O2EffectType values of effects that are added when the player transforms
     * into wolf form (AGGRESSION and LYCANTHROPY_SPEECH). These effects are tracked for easy
     * removal when the player reverts to human form.</p>
     */
    ArrayList<O2EffectType> additionalEffects = new ArrayList<>();

    /**
     * Relief flag that suppresses werewolf transformation.
     *
     * <p>When set to true by LYCANTHROPY_RELIEF, this flag prevents the player from transforming
     * into wolf form even during full moon periods. The relief effect checks this flag during
     * doCheckEffect() to suppress transformation symptoms.</p>
     */
    boolean relief = false;

    /**
     * Constructor for creating a permanent lycanthropy curse effect.
     *
     * <p>Creates a permanent curse that ties the player's form to the lunar cycle. The player will
     * transform into a wolf during full moons and revert to human form otherwise. Sets the detection
     * text for mind-reading spells to "is a werewolf". The effect is always permanent and transformation
     * is controlled by the upkeep() method based on in-game time and moon phase.</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    duration parameter (ignored - lycanthropy is always permanent)
     * @param isPermanent ignored - lycanthropy is always permanent
     * @param pid         the unique ID of the player to curse with lycanthropy
     */
    public LYCANTHROPY(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, true, pid);

        effectType = O2EffectType.LYCANTHROPY;
        checkDurationBounds();

        legilimensText = "is a werewolf";

        form = EntityType.WOLF;
        permanent = true;
        transformed = false;
    }

    /**
     * Kill the lycanthropy effect and restore the player to human form.
     *
     * <p>Reverts the player to human form if currently transformed, removes any additional effects
     * (AGGRESSION, LYCANTHROPY_SPEECH) that were applied, and marks the effect for removal from
     * the player's effect list.</p>
     */
    @Override
    public void kill() {
        restore();
        removeAdditionalEffect();

        kill = true;
    }

    /**
     * Age the lycanthropy effect and manage transformation based on lunar cycle and relief status.
     *
     * <p>Called each game tick. This method performs transformation checks in the following priority:</p>
     * <ol>
     * <li>If relief flag is active: revert to human form and remove secondary effects</li>
     * <li>If on full moon day and time is between moonrise (13000) and dawn (23000): transform to wolf
     * and apply AGGRESSION and LYCANTHROPY_SPEECH effects</li>
     * <li>Otherwise: revert to human form and remove secondary effects</li>
     * </ol>
     *
     * <p>Transformation is automatically managed by this check cycle and persists indefinitely until
     * the effect is manually killed or relief is applied.</p>
     */
    @Override
    protected void doCheckEffect() {
        long curTime = target.getWorld().getTime();

        // does this player have lycanthropy relief?
        if (relief) {
            if (transformed) {
                restore();
                removeAdditionalEffect();
            }
        }
        // if it is a time they should be transformed, transform them
        // transformation time is on a full moon day between Moonrise and Dawn
        else if ((MoonPhase.getMoonPhase(target.getWorld()) == MoonPhase.FULL_MOON)) {
            if ((curTime > TimeCommon.MOONRISE.getTick()) && (curTime < TimeCommon.DAWN.getTick())){
                if (!transformed) {
                    transform();
                    addAdditionalEffects();
                    target.playSound(target.getEyeLocation(), Sound.ENTITY_WOLF_AMBIENT, 1, 0);
                }
            }
        }
        // player is transformed and it is outside of the transformation time, restore them
        else if (transformed) {
            restore();
            removeAdditionalEffect();
        }
    }

    /**
     * Add secondary effects that accompany werewolf transformation.
     *
     * <p>Applies two effects during werewolf transformation: AGGRESSION (with aggression level 10) and
     * LYCANTHROPY_SPEECH (with 5 tick duration). These effects are tracked in additionalEffects list
     * for removal when the player reverts to human form.</p>
     */
    private void addAdditionalEffects() {
        AGGRESSION aggression = new AGGRESSION(p, 5, true, targetID);
        aggression.setAggressionLevel(10);
        Ollivanders2API.getPlayers().playerEffects.addEffect(aggression);
        additionalEffects.add(O2EffectType.AGGRESSION);

        LYCANTHROPY_SPEECH speech = new LYCANTHROPY_SPEECH(p, 5, true, targetID);
        Ollivanders2API.getPlayers().playerEffects.addEffect(speech);
        additionalEffects.add(O2EffectType.LYCANTHROPY_SPEECH);
    }

    /**
     * Remove secondary effects when player reverts to human form.
     *
     * <p>Removes all effects tracked in additionalEffects list (AGGRESSION, LYCANTHROPY_SPEECH) when
     * the player transforms back to human form outside of full moon periods.</p>
     */
    private void removeAdditionalEffect() {
        for (O2EffectType effectType : additionalEffects) {
            Ollivanders2API.getPlayers().playerEffects.removeEffect(targetID, effectType);
        }
    }

    /**
     * Prevent any code from modifying the permanent status of lycanthropy.
     *
     * <p>Overrides the parent method to ensure lycanthropy always remains permanent. The duration
     * parameter is ignored and the effect cannot be made temporary through this method.</p>
     *
     * @param perm ignored - lycanthropy is always permanent
     */
    @Override
    public void setPermanent(boolean perm) { }

    /**
     * Perform cleanup when the lycanthropy effect is removed.
     *
     * <p>The default implementation does nothing. The main cleanup is handled by the kill() method
     * which restores the player to human form and removes additional effects. Once killed, the player
     * is no longer affected by the lunar cycle transformation.</p>
     */
    @Override
    public void doRemove() { }

    /**
     * Spread lycanthropy curse to players damaged by transformed werewolf.
     *
     * <p>When the affected player is transformed into wolf form, any damage they deal to other players
     * can spread the lycanthropy curse. This method checks if the damage event should spread infection
     * and schedules the infection to occur 1 second after the damage event (allowing time for the event
     * to be processed and verified it wasn't cancelled).</p>
     *
     * @param event the entity damage event that may spread lycanthropy
     */
    @Override
    void doOnEntityDamageByEntityEvent(@NotNull EntityDamageByEntityEvent event) {
        common.printDebugMessage("doOnEntityDamageByEntityEvent", null, null, false);

        // if something, like a protective effect, canceled this effect, we do nothing
        if (event.isCancelled())
            return;

        // make sure lycanthropy is enabled
        if (!O2EffectType.LYCANTHROPY.isEnabled()) {
            common.printDebugMessage("lycanthropy not enabled", null, null, false);
            return;
        }

        // make sure the damaged entity is a player
        if (!(event.getEntity() instanceof Player)) {
            common.printDebugMessage("attacked entity not a player", null, null, false);
            return;
        }

        // if this effect's target is currently transformed, infect the damaged player
        if (transformed) {
            common.printDebugMessage("infecting player", null, null, false);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!event.isCancelled())
                        infectPlayer((Player) event.getEntity());
                }
            }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
        }
    }

    /**
     * Infect a player with the lycanthropy curse.
     *
     * <p>Creates and applies a new LYCANTHROPY effect to the specified player if they don't already
     * have the curse.</p>
     *
     * @param player the player to infect with lycanthropy
     */
    private void infectPlayer(Player player) {
        if (!Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.LYCANTHROPY)) {
            LYCANTHROPY effect = new LYCANTHROPY(p, 5, true, player.getUniqueId());
            Ollivanders2API.getPlayers().playerEffects.addEffect(effect);
        }
    }

    /**
     * No custom watcher needed for LYCANTHROPY effect.
     *
     * <p>The lycanthropy transformation is managed through the checkEffect() mechanism based on
     * world time and moon phase, so no custom event watchers are needed.</p>
     */
    @Override
    void customizeWatcher() {}

    /**
     * Set the relief flag to suppress or allow werewolf transformation.
     *
     * <p>When relief is set to true, the player will revert to human form and will not transform
     * even during full moon periods. When set to false, normal transformation behavior resumes.
     * This method is called by LYCANTHROPY_RELIEF effect to toggle relief state.</p>
     *
     * @param relief true to suppress transformation, false to allow normal transformation
     */
    public void setRelief (boolean relief) {
        this.relief = relief;
    }

    /**
     * Get the current relief flag status.
     *
     * <p>Returns whether the lycanthropy transformation is currently suppressed by relief.</p>
     *
     * @return true if relief is active and transformation is suppressed, false otherwise
     */
    public boolean getRelief() {
        return relief;
    }
}