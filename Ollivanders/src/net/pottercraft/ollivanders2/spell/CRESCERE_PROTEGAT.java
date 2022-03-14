package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.jetbrains.annotations.NotNull;

/**
 * Grows a Stationary Spell's radius. Only the player who created the Stationary Spell can change its radius.
 */
public final class CRESCERE_PROTEGAT extends O2Spell
{
    private static final int minIncrease = 1;
    private static final int maxIncrease = 10;

    int increase;

    /**
     * Stationary spell types that cannot be targeted by this spell.
     */
    List<O2StationarySpellType> spellBlockedList = new ArrayList<>()
    {{
        add(O2StationarySpellType.COLLOPORTUS);
        add(O2StationarySpellType.HORCRUX);
        add(O2StationarySpellType.HARMONIA_NECTERE_PASSUS);
        add(O2StationarySpellType.ALIQUAM_FLOO);
    }};

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public CRESCERE_PROTEGAT(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.CRESCERE_PROTEGAT;
        branch = O2MagicBranch.CHARMS;

        text = "Grows a stationary spell's radius. Only the player who created the Stationary Spell can change it's radius.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public CRESCERE_PROTEGAT(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);
        spellType = O2SpellType.CRESCERE_PROTEGAT;
        branch = O2MagicBranch.CHARMS;

        initSpell();
    }

    /**
     * Set the amount of increase based on the caster's skill.
     */
    @Override
    void doInitSpell()
    {
        increase = (int) usesModifier / 20;
        if (increase < minIncrease)
            increase = minIncrease;
        else if (increase > maxIncrease)
            increase = maxIncrease;
    }

    /**
     * Look for a stationary spell at the projectile's location and increase its radius.
     * <p>
     * 1. Target spell must have been cast by this caster.
     * 2. Target spell cannot be higher difficulty level than this spell.
     * 3. Target spell cannot be on the spell blocked list (ex. floo network, vanishing cabinets)
     * <p>
     * {@link O2StationarySpell}
     */
    @Override
    protected void doCheckEffect()
    {
        // projectile has stopped, kill the spell
        if (hasHitTarget())
            kill();

        O2StationarySpell targetSpell = null;

        for (O2StationarySpell spell : Ollivanders2API.getStationarySpells().getStationarySpellsAtLocation(location))
        {
            // if the stationary spell type is not in the blocked list for this spell
            // was cast by the caster of this spell
            // is inside the radius of this spell, then target it
            // and the target spell is at or lower than this spell in level
            if (!spellBlockedList.contains(spell.getSpellType())
                    && spell.getCasterID().equals(player.getUniqueId())
                    && spell.getSpellType().getLevel().ordinal() <= this.spellType.getLevel().ordinal())
            {
                targetSpell = spell;
                break;
            }
        }

        // if we found a target stationary spell, increase its radius
        if (targetSpell != null)
        {
            targetSpell.increaseRadius(increase);
            targetSpell.flair(10);
            kill();
        }
    }
}