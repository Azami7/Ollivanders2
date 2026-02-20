package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/**
 * Repairs a damageable item you aim it at.
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Mending_Charm">https://harrypotter.fandom.com/wiki/Mending_Charm</a>
 */
public class REPARO extends O2Spell {
    /**
     * The minimum this spell will repair
     */
    int minRepair = 30; // half the durability of a wooden sword

    /**
     * The maximum this spell will repair
     */
    int maxRepair = 251; // durability of an iron sword

    /**
     * The multiplier on usesModifier used to determine the level of repair
     */
    float repairMultiplier = 0.5f;

    /**
     * How much durability to repair
     */
    int repair;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public REPARO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.REPARO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Mending Charm");
            add("Mr. Weasley took Harry's glasses, gave them a tap of his wand and returned them, good as new.");
            add("The Mending Charm will repair broken objects with a flick of the wand.  Accidents do happen, so it is essential to know how to mend our errors.");
        }};

        text = "Repair the durability of a tool.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public REPARO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.REPARO;
        branch = O2MagicBranch.CHARMS;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled) {
            worldGuardFlags.add(Flags.ITEM_DROP);
            worldGuardFlags.add(Flags.ITEM_PICKUP);
        }

        initSpell();
    }

    /**
     * Determine the amount to repair based on caster's skill.
     */
    @Override
    void doInitSpell() {
        repair = (int) (usesModifier / repairMultiplier);

        if (repair < minRepair)
            repair = minRepair;
        else if (repair > maxRepair)
            repair = maxRepair;
    }

    /**
     * Find a damageable item and repair it.
     */
    @Override
    protected void doCheckEffect() {
        if (hasHitTarget())
            kill();

        List<Item> items = getNearbyItems(defaultRadius);

        for (Item item : items) {
            ItemStack stack = item.getItemStack();
            ItemMeta itemMeta = stack.getItemMeta();

            if (itemMeta instanceof Damageable) {
                int damage = ((Damageable) itemMeta).getDamage();
                damage = damage - repair;

                ((Damageable) itemMeta).setDamage(damage);
                stack.setItemMeta(itemMeta);

                item.setItemStack(stack);
                kill();

                player.sendMessage(Ollivanders2.chatColor + item.getName() + " looks newer than before.");

                break;
            }
        }
    }
}