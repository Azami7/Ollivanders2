package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Lumos Maxima: a blinding flash that applies Blindness to nearby players in a skill-scaled radius. The caster is not
 * affected.
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Lumos_Maxima">Harry Potter Wiki - Lumos Maxima</a>
 */
public final class LUMOS_MAXIMA extends AddPotionEffectInRadius {
    private final static int minEffectRadiusConfig = 5;
    private final static int maxEffectRadiusConfig = 10;
    private final static int minDurationInSecondsConfig = 5;
    private final static int maxDurationInSecondsConfig = 30;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public LUMOS_MAXIMA(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.LUMOS_MAXIMA;
        branch = O2MagicBranch.CHARMS;

        text = "Creates a blinding flash from the tip of the caster's wand.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public LUMOS_MAXIMA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.LUMOS_MAXIMA;
        branch = O2MagicBranch.CHARMS;

        potionEffectTypes.add(PotionEffectType.BLINDNESS);
        amplifier = 1;

        minDurationInSeconds = minDurationInSecondsConfig;
        maxDurationInSeconds = maxDurationInSecondsConfig;
        minEffectRadius = minEffectRadiusConfig;
        maxEffectRadius = maxEffectRadiusConfig;
        targetSelf = false;

        initSpell();
    }

    /**
     * Spawn a flash particle at the caster's location to represent the blinding light.
     */
    @Override
    void doFlair() {
        world.spawnParticle(Particle.FLASH, caster.getLocation(), 1);
    }
}