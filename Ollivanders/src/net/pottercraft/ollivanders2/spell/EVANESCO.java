package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Vanishes non-living entities.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Vanishing_Spell">https://harrypotter.fandom.com/wiki/Vanishing_Spell</a>
 */
public class EVANESCO extends EntityTransfiguration {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public EVANESCO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.EVANESCO;
        branch = O2MagicBranch.TRANSFIGURATION;

        flavorText = new ArrayList<>() {{
            add("The Vanishing Spell");
            add("The contents of Harry’s potion vanished; he was left standing foolishly beside an empty cauldron.");
        }};

        text = "Evanesco will temporarily vanish any non-living entity.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public EVANESCO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.EVANESCO;
        branch = O2MagicBranch.TRANSFIGURATION;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled) {
            worldGuardFlags.add(Flags.USE);
            worldGuardFlags.add(Flags.BUILD);
        }

        durationModifier = 4;

        initSpell();
    }

    /**
     * Set the success rate for this spell based on the caster's skill and year (if enabled)
     */
    @Override
    void doInitSpell() {
        float successMultiplier = 1f;

        if (Ollivanders2.useYears) {
            if (this.spellType.getLevel().ordinal() > casterO2P.getYear().getHighestLevelForYear().ordinal())
                successMultiplier = 0.5f;
            else
                successMultiplier = 2f;
        }

        successRate = (int) ((usesModifier / 2) * successMultiplier);
    }

    /**
     * Reject living entities and delegate remaining checks to the parent.
     */
    @Override
    boolean targetTypeCheck(@NotNull Entity entity) {
        if (entity instanceof LivingEntity)
            return false;

        return super.targetTypeCheck(entity);
    }

    /**
     * Remove the entity and set the transfigured entity to the original
     *
     * @param entity the entity to transfigure
     * @return the original entity, now removed/killed
     */
    @Override
    @Nullable
    protected Entity transfigureEntity(@NotNull Entity entity) {
        originalEntity.remove();

        return originalEntity;
    }
}