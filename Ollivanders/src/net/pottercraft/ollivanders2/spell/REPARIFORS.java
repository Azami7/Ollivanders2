package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Reparifors is a healing spell that reverts minor magically-induced ailments, such as paralysis and poisoning.
 *
 * @see <a href = "http://harrypotter.wikia.com/wiki/Reparifors">http://harrypotter.wikia.com/wiki/Reparifors</a>
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
     * Find a target entity to heal.
     */
    @Override
    protected void doCheckEffect() {
        if (hasHitTarget())
            kill();

        for (Player target : getNearbyPlayers(defaultRadius)) {
            if (target.getUniqueId().equals(player.getUniqueId()))
                continue;

            // if they are affected by immobilized, reduce the duration of the effect
            if (Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), O2EffectType.IMMOBILIZE) && !(Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), O2EffectType.SUSPENSION))) {
                int decrease = ((int) usesModifier / 20) + 5; // age by at least 5 percent

                Ollivanders2API.getPlayers().playerEffects.ageEffectByPercent(target.getUniqueId(), O2EffectType.IMMOBILIZE, decrease);

                kill();
                return;
            }

            // reduce duration of poison by half
            if (target.hasPotionEffect(PotionEffectType.POISON)) {
                PotionEffect potionEffect = target.getPotionEffect(PotionEffectType.POISON);
                if (potionEffect != null) {
                    int duration = potionEffect.getDuration();
                    target.removePotionEffect(PotionEffectType.POISON);
                    target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, (duration / 2), 1));
                }

                kill();
                return;
            }

            // do a minor heal
            int duration = (((int) usesModifier / 10) * Ollivanders2Common.ticksPerSecond) + (15 * Ollivanders2Common.ticksPerSecond);
            target.addPotionEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, duration, 1));

            kill();
            return;
        }
    }
}
