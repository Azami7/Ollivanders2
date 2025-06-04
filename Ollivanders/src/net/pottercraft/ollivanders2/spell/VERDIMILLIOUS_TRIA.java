package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class VERDIMILLIOUS_TRIA extends VERDIMILLIOUS_DUO {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public VERDIMILLIOUS_TRIA(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.VERDIMILLIOUS_TRIA;

        text = "Shoots green sparks out of the caster's wand which damages entities and reveals powerful dark magic in items by making them temporarily glow.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public VERDIMILLIOUS_TRIA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.VERMILLIOUS_DUO;
        moveEffectData = Material.RED_STAINED_GLASS;
        damageModifier = 0.25;
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
