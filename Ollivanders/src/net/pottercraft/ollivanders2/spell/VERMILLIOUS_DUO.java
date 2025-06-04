package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VERMILLIOUS_DUO extends SparksBase {
    /**
     * Default constructor for use in generating spell book text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public VERMILLIOUS_DUO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.VERMILLIOUS_DUO;

        text = "A stronger Red Sparks Charm that shoots red sparks from the caster's wand and can damage entities.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public VERMILLIOUS_DUO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.VERMILLIOUS_DUO;
        moveEffectData = Material.RED_STAINED_GLASS;
        damageModifier = 0.125;
        radius = 4;

        initSpell();
    }

    /**
     * Set the damage for this spell based on caster's skill level in this spell
     */
    @Override
    void doInitSpell() {
        setDamage();
    }
}
