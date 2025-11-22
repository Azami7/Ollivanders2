package net.pottercraft.ollivanders2.effect;

import java.util.ArrayList;
import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
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
 * <li>Infection occurs 1 tick after damage is dealt</li>
 * <li>New infection creates new LYCANTHROPY effect on the damaged player</li>
 * </ul>
 *
 * @author azami7
 * @see AGGRESSION for the aggressive behavior applied during transformation
 * @see LYCANTHROPY_SPEECH for the wolf-like speech applied during transformation
 * @see ShapeShiftSuper for the shape-shifting transformation mechanism
 */
public class LYCANTHROPY extends ShapeShiftSuper {
    ArrayList<O2EffectType> additionalEffects = new ArrayList<>();

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
     * Age the lycanthropy effect and manage transformation based on lunar cycle.
     *
     * <p>Called each game tick. This method checks the in-game time and full moon cycle (every 8 days).
     * On full moon days after sunset (game time > 13000), the player transforms into wolf form and
     * aggressive/speech effects are applied. On other times or non-full-moon days, the player reverts
     * to human form and additional effects are removed. Transformation is automatically managed by
     * this upkeep cycle and persists indefinitely until the effect is manually killed.</p>
     */
    @Override
    protected void upkeep() {
        Player target = p.getServer().getPlayer(targetID);

        if (target == null) {
            kill();
            return;
        }

        long curTime = target.getWorld().getTime();
        if (!transformed) {
            // only need to check after sunset
            if (curTime > 13000) {
                long day = target.getWorld().getFullTime() / 24000;
                if ((day % 8) == 0) {
                    // moonrise on a full moon day
                    transform();

                    addAdditionalEffects();

                    target.playSound(target.getEyeLocation(), Sound.ENTITY_WOLF_AMBIENT, 1, 0);
                }
            }
        }
        else {
            long day = target.getWorld().getFullTime() / 24000;
            boolean restore = false;

            if ((day % 8) == 0) {
                // if it is a full moon day before moonrise or after sunrise
                if (curTime < 13000 || curTime > 23500) {
                    restore = true;
                }
            }
            else {
                // it is not a full moon day
                restore = true;
            }

            if (restore) {
                restore();
                removeAdditionalEffect();
            }
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
    public void setPermanent(boolean perm) {
    }

    /**
     * Perform cleanup when the lycanthropy effect is removed.
     *
     * <p>The default implementation does nothing. The main cleanup is handled by the kill() method
     * which restores the player to human form and removes additional effects. Once killed, the player
     * is no longer affected by the lunar cycle transformation.</p>
     */
    @Override
    public void doRemove() {
    }

    /**
     * Handle damage events to propagate lycanthropy infection through angry wolves.
     *
     * <p>When an angry wolf (transformed by this effect) damages a player, the target player is infected
     * with lycanthropy after a 1 tick delay (if they don't already have the effect). This allows the
     * curse to spread from player-controlled werewolves to other players.</p>
     *
     * @param event the entity damage event where a wolf damages another entity
     */
    @Override
    void doOnEntityDamageByEntityEvent(@NotNull EntityDamageByEntityEvent event) {
        if (!O2EffectType.LYCANTHROPY.isEnabled())
            return;

        if (event.getDamager() instanceof Wolf) {
            Wolf wolf = (Wolf) event.getDamager();
            Player damaged = p.getServer().getPlayer(targetID);
            if (damaged == null) {
                common.printDebugMessage("Null player in Lycanthropy.doOnDamage", null, null, true);
                return;
            }

            if (wolf.isAngry()) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!event.isCancelled())
                            infectPlayer(damaged);
                    }
                }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
            }
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
}