package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.item.enchantment.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Deletes an item entity.
 * <p>
 * Reference: https://harrypotter.fandom.com/wiki/Eradication_Spell
 */
public final class DELETRIUS extends O2Spell
{
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public DELETRIUS(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.DELETRIUS;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>()
        {{
            add("The Eradication Spell");
            add("'Deletrius!' Mr Diggory shouted, and the smoky skull vanished in a wisp of smoke.");
        }};

        text = "Cause an item entity to stop existing.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public DELETRIUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);
        spellType = O2SpellType.DELETRIUS;
        branch = O2MagicBranch.CHARMS;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.ITEM_PICKUP);

        initSpell();

        failureMessage = "You are unable to destroy the item.";
    }

    /**
     * Delete a item
     */
    @Override
    protected void doCheckEffect()
    {
        if (hasHitTarget())
            kill();

        List<Item> items = getItems(defaultRadius);

        if (items.size() > 0)
        {
            // handle success chance
            int successRate = (int) (usesModifier / 4);
            if (successRate < Math.abs(Ollivanders2Common.random.nextInt() % 100))
            {
                sendFailureMessage();
                return;
            }

            Item item = items.get(0);

            // handle if this item is enchanted
            Enchantment enchantment = Ollivanders2API.getItems().enchantedItems.getEnchantment(item.getItemStack());
            if (enchantment == null || enchantment.getType().getLevel().ordinal() <= spellType.getLevel().ordinal())
                item.remove();
            else
                sendFailureMessage();

            kill();
        }
    }
}