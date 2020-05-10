package net.pottercraft.ollivanders2.spell;

import java.util.List;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Enchants a flying broomstick.
 *
 * @author lownes
 * @author Azami7
 */
public final class VOLATUS extends O2Spell
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public VOLATUS()
   {
      super();

      spellType = O2SpellType.VOLATUS;
      branch = O2MagicBranch.CHARMS;

      text = "Volatus is used to enchant a broomstick for flight. "
            + "To make a magical broomstick, you must first craft a broomstick.  This recipe requires two sticks and a wheat. "
            + "Place the first stick in the upper-right corner, the next stick in the center, and the wheat in the lower-left. "
            + "Once you have a broomstick, place it in the ground in front of you and cast the spell Volatus at it. "
            + "Your experience with this spell determines how fast the broomstick can go.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public VOLATUS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.VOLATUS;
      branch = O2MagicBranch.CHARMS;
      initSpell();

      // world guard flags
      worldGuardFlags.add(Flags.ITEM_DROP);
      worldGuardFlags.add(Flags.ITEM_PICKUP);
   }

   @Override
   protected void doCheckEffect ()
   {
      for (Item item : getItems(1.5))
      {
         ItemStack stack = item.getItemStack();
         if (usesModifier >= 1 && isBroom(stack))
         {
            stack.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, (int) usesModifier);
            item.setItemStack(stack);
         }
         return;
      }

      if (hasHitTarget())
      {
         kill();
      }
   }

   /**
    * Finds out if an item is a broom.
    *
    * @param item - Item in question.
    * @return True if yes.
    */
   public boolean isBroom (ItemStack item)
   {
      if (item.getType() == Ollivanders2.broomstickMaterial)
      {
         ItemMeta meta = item.getItemMeta();
         if (meta.hasLore())
         {
            List<String> lore = meta.getLore();
            if (lore.contains("Flying vehicle used by magical folk"))
            {
               return true;
            }
         }
      }
      return false;
   }
}
