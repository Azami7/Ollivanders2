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
 * Permanent lycanthropy curse that turns the player into a werewolf on full-moon nights (between moonrise and dawn),
 * applying {@link AGGRESSION} at level 10 and {@link LYCANTHROPY_SPEECH} while transformed and reverting them
 * otherwise. Damage the target deals while transformed can infect other players. {@link LYCANTHROPY_RELIEF} suppresses
 * transformation. Always permanent; {@link #setPermanent(boolean)} is a no-op. Detectable via Legilimens.
 *
 * @author azami7
 * @see ShapeShift
 */
public class LYCANTHROPY extends ShapeShift {
    /**
     * Secondary effects (AGGRESSION, LYCANTHROPY_SPEECH) applied while transformed, tracked so they can be removed on
     * revert to human form.
     */
    ArrayList<O2EffectType> additionalEffects = new ArrayList<>();

    /**
     * When true, transformation is suppressed even during a full moon. Toggled by {@link LYCANTHROPY_RELIEF} via
     * {@link #setRelief(boolean)}.
     */
    boolean relief = false;

    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    ignored - lycanthropy is always permanent
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
     * Restore the player to human form, remove the secondary effects, and mark the effect for removal.
     */
    @Override
    public void kill() {
        restore();
        removeAdditionalEffect();

        kill = true;
    }

    /**
     * Transform to wolf form on a full moon between moonrise and dawn, applying the secondary effects, and revert
     * otherwise. An active relief flag suppresses transformation.
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
     * Apply and track the secondary effects for werewolf form: AGGRESSION at level 10 and LYCANTHROPY_SPEECH.
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
     * Remove the secondary effects tracked in {@link #additionalEffects}.
     */
    private void removeAdditionalEffect() {
        for (O2EffectType effectType : additionalEffects) {
            Ollivanders2API.getPlayers().playerEffects.removeEffect(targetID, effectType);
        }
    }

    /**
     * No-op; lycanthropy is always permanent.
     *
     * @param perm ignored - lycanthropy is always permanent
     */
    @Override
    public void setPermanent(boolean perm) { }

    /**
     * No cleanup on removal; {@link #kill()} handles restoring the player and removing the secondary effects.
     */
    @Override
    public void doRemove() { }

    /**
     * While the target is transformed, schedule infection of any player they damage one second later (skipped if the
     * event was later cancelled or lycanthropy is disabled).
     */
    @Override
    void doOnEntityDamageByEntityEvent(@NotNull EntityDamageByEntityEvent event) {
        if (!event.getDamager().getUniqueId().equals(targetID))
            return;

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
     * Apply a new LYCANTHROPY effect to the player unless they already have the curse.
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
     * No custom watcher needed; transformation is driven by {@link #doCheckEffect()}.
     */
    @Override
    void customizeWatcher() {}

    /**
     * Set the relief flag; when true, transformation is suppressed even during a full moon.
     *
     * @param relief true to suppress transformation, false to allow normal transformation
     */
    public void setRelief (boolean relief) {
        this.relief = relief;
    }

    /**
     * Get whether transformation is currently suppressed by relief.
     *
     * @return true if relief is active
     */
    public boolean getRelief() {
        return relief;
    }
}