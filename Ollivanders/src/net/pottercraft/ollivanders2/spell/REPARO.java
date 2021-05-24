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
 * Repairs an itemstack you aim it at.
 *
 * @author lownes
 * @author Azami7
 */
public final class REPARO extends O2Spell
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public REPARO()
   {
      super();

      spellType = O2SpellType.REPARO;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<>()
      {{
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
   public REPARO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.REPARO;
      branch = O2MagicBranch.CHARMS;
      initSpell();

      // world guard flags
      worldGuardFlags.add(Flags.ITEM_DROP);
      worldGuardFlags.add(Flags.ITEM_PICKUP);
   }

   @Override
   protected void doCheckEffect ()
   {
      List<Item> items = getItems(1.5);

      for (Item item : items)
      {
         ItemStack stack = item.getItemStack();
         ItemMeta itemMeta = stack.getItemMeta();

         if (itemMeta instanceof Damageable)
         {
            int damage = ((Damageable) itemMeta).getDamage();

            damage = damage - (int)usesModifier;

            if (damage < 0)
            {
               damage = 0;
            }

            ((Damageable) itemMeta).setDamage(damage);
            stack.setItemMeta(itemMeta);

            item.setItemStack(stack);
            kill();

            break;
         }
      }

      if (hasHitTarget())
      {
         kill();
      }
   }
}