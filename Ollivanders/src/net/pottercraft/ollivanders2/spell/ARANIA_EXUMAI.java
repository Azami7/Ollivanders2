package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Blasts spiders away
 * <p>
 * https://harrypotter.fandom.com/wiki/Arania_Exumai
 */
public final class ARANIA_EXUMAI extends Knockback
{
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public ARANIA_EXUMAI(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.ARANIA_EXUMAI;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>()
        {{
            add("\"Know any spells?\"\n\"One, but it's not powerful enough for all of them.\"\n\"Where's Hermione when you need her?\"\n\"Let's go! Arania Exumai\" -Harry Potter and Ron Weasley");
            add("Defense Against Spiders");
        }};

        text = "Knocks back spiders.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public ARANIA_EXUMAI(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);
        spellType = O2SpellType.ARANIA_EXUMAI;
        branch = O2MagicBranch.CHARMS;

        strengthReducer = 10;

        entityAllowedList.add(EntityType.SPIDER);

        initSpell();
    }
}