package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Healing spell that reverts minor magically-induced ailments on a nearby player.
 * <p>
 * Reparifors uses the entity-scanning projectile model: each tick it scans for players near
 * the projectile and treats the first non-caster it finds. Only one ailment is treated per
 * cast, checked in priority order:
 * </p>
 * <ol>
 * <li><strong>Immobilize</strong> (if not also suspended) — ages the
 *     {@link net.pottercraft.ollivanders2.effect.O2EffectType#IMMOBILIZE} effect by a percentage
 *     that scales with caster experience.</li>
 * <li><strong>Poison</strong> — halves the remaining duration of the Minecraft
 *     {@link PotionEffectType#POISON} effect, preserving the original amplifier.</li>
 * <li><strong>Minor heal</strong> — applies a one-time {@link PotionEffectType#INSTANT_HEALTH}
 *     effect if no specific ailment was found.</li>
 * </ol>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Reparifors">Reparifors</a>
 */
public class REPARIFORS extends O2Spell {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public REPARIFORS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.REPARIFORS;
        branch = O2MagicBranch.CHARMS;

        text = "A healing spell for minor ailments such as paralysis or poisoning.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public REPARIFORS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.REPARIFORS;
        branch = O2MagicBranch.CHARMS;

        initSpell();
    }

    /**
     * Scan for a nearby player and treat their highest-priority ailment.
     * <p>
     * Each tick, the method scans within {@link #defaultRadius} for players. The caster is
     * skipped. The first non-caster player found is treated using a priority cascade:
     * </p>
     * <ol>
     * <li>If the target has {@link O2EffectType#IMMOBILIZE} but not {@link O2EffectType#SUSPENSION},
     *     the immobilize effect is aged by a caster-experience-scaled percentage. Suspension blocks
     *     this treatment because freeing a suspended player could cause them to fall.</li>
     * <li>Otherwise, if the target has {@link PotionEffectType#POISON}, the poison duration is
     *     halved (preserving the original amplifier).</li>
     * <li>Otherwise, a one-time {@link PotionEffectType#INSTANT_HEALTH} is applied as a minor heal.</li>
     * </ol>
     * <p>
     * Only one ailment is treated per cast (single-target, single-ailment). If the projectile
     * hits a block before finding a player, the spell is killed silently.
     * </p>
     */
    @Override
    protected void doCheckEffect() {
        if (hasHitBlock())
            kill();

        for (Player target : getNearbyPlayers(defaultRadius)) {
            if (target.getUniqueId().equals(caster.getUniqueId()))
                continue;

            kill();
            // if they are affected by immobilize but not suspension, reduce the duration of the effect
            // we cannot remove immobilize when a player is suspended, or they would be able to move, potentially fall, in suspension
            if (Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), O2EffectType.IMMOBILIZE) && !(Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), O2EffectType.SUSPENSION))) {
                int decrease = ((int) usesModifier / 2) + 5; // age by at least 5 percent

                Ollivanders2API.getPlayers().playerEffects.ageEffectByPercent(target.getUniqueId(), O2EffectType.IMMOBILIZE, decrease);
                return;
            }

            // reduce duration of poison by half
            if (target.hasPotionEffect(PotionEffectType.POISON)) {
                PotionEffect potionEffect = target.getPotionEffect(PotionEffectType.POISON);
                if (potionEffect != null) {
                    int reducedDuration = potionEffect.getDuration() / 2;
                    target.removePotionEffect(PotionEffectType.POISON);
                    target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, reducedDuration, potionEffect.getAmplifier()));
                }

                return;
            }

            // do a minor heal
            target.addPotionEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 5, 1)); // duration not used for this instant effect
            return;
        }
    }
}
