package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Killing Curse - https://harrypotter.fandom.com/wiki/Killing_Curse - does direct damage to a living entity according
 * to your level in the spell.
 */
public final class AVADA_KEDAVRA extends O2Spell
{
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public AVADA_KEDAVRA(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.AVADA_KEDAVRA;
        branch = O2MagicBranch.DARK_ARTS;

        flavorText = new ArrayList<>()
        {{
            add("The Killing Curse");
            add("There was a flash of blinding green light and a rushing sound, as though a vast, invisible something was soaring through the air — instantaneously the spider rolled over onto its back, unmarked, but unmistakably dead");
            add("\"Yes, the last and worst. Avada Kedavra. ...the Killing Curse.\" -Bartemius Crouch Jr (disguised as Alastor Moody)");
        }};

        text = "Cause direct damage to a living thing, possibly killing it.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public AVADA_KEDAVRA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);
        spellType = O2SpellType.AVADA_KEDAVRA;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
        {
            worldGuardFlags.add(Flags.PVP);
            worldGuardFlags.add(Flags.DAMAGE_ANIMALS);
        }

        moveEffectData = Material.GREEN_WOOL;

        initSpell();
    }

    /**
     * Harm or kill a damageable entity
     */
    @Override
    protected void doCheckEffect()
    {
        if (hasHitTarget())
            kill();

        List<Damageable> entities = getNearbyDamageableEntities(defaultRadius);
        if (!entities.isEmpty())
        {
            for (Damageable entity : entities)
            {
                if (entity.getUniqueId().equals(player.getUniqueId()))
                    continue;

                entity.damage(usesModifier * 2, player);
                common.printDebugMessage("Targeting " + entity.getName(), null, null, false) ;

                kill();
                return;
            }
        }
    }
}