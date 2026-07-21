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
 * Healing spell that treats one minor, magically-induced ailment on the nearest non-caster player per cast, chosen in
 * priority order: immobilize, then poison, otherwise a small heal.
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Reparifors">Harry Potter Wiki - Reparifors</a>
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
     * Treat the highest-priority ailment on the nearest non-caster player: age an {@link O2EffectType#IMMOBILIZE}
     * effect (unless the player is also under {@link O2EffectType#SUSPENSION}), else halve a
     * {@link PotionEffectType#POISON} duration, else apply a small {@link PotionEffectType#INSTANT_HEALTH} heal. Only
     * one ailment is treated per cast; killed silently if the projectile hits a block first.
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
