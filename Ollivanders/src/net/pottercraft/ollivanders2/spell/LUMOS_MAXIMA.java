package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Lumos Maxima, a charm that produces a blinding flash of bright white light.
 *
 * <p>Lumos Maxima is an instant-radius charm that applies Blindness to nearby entities
 * within 5-10 blocks of the caster. The effect lasts between 5 and 30 seconds depending on
 * caster skill level and is accompanied by a flash particle effect at the caster's location.
 * The caster is not affected by the spell.</p>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Lumos_Maxima">Lumos Maxima</a>
 */
public final class LUMOS_MAXIMA extends AddPotionEffectInRadius {
    private final static int minEffectRadiusConfig = 5;
    private final static int maxEffectRadiusConfig = 10;
    private final static int minDurationInSecondsConfig = 5;
    private final static int maxDurationInSecondsConfig = 30;

    /**
     * Default constructor for use in generating spell text.
     *
     * <p>Do not use this constructor to cast the spell. Use the three-parameter constructor instead.</p>
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
     * Constructor for casting the spell.
     *
     * <p>Applies Blindness effect to nearby entities within 5-10 blocks of the caster.
     * The caster is not affected. Effect duration ranges from 5 to 30 seconds based on
     * skill level, accompanied by a flash particle effect.</p>
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public LUMOS_MAXIMA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.LUMOS_MAXIMA;
        branch = O2MagicBranch.CHARMS;

        effectTypes.add(PotionEffectType.BLINDNESS);
        amplifier = 1;

        minDurationInSeconds = minDurationInSecondsConfig;
        maxDurationInSeconds = maxDurationInSecondsConfig;
        minEffectRadius = minEffectRadiusConfig;
        maxEffectRadius = maxEffectRadiusConfig;
        targetSelf = false;

        initSpell();
    }

    /**
     * Spawn a flash particle effect at the caster's location.
     *
     * <p>Displays a bright flash particle to visually represent the blinding light produced
     * by the spell.</p>
     */
    @Override
    void doFlair() {
        world.spawnParticle(Particle.FLASH, caster.getLocation(), 1);
    }
}