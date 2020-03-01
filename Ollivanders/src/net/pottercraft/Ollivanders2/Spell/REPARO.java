package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.pottercraft.Ollivanders2.O2MagicBranch;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.inventory.meta.ItemMeta;

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

      initSpell();

      flavorText = new ArrayList<String>()
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
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public REPARO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.REPARO;
      branch = O2MagicBranch.CHARMS;
      initSpell();

      // world guard flags
      worldGuardFlags.add(DefaultFlag.ITEM_DROP);
      worldGuardFlags.add(DefaultFlag.ITEM_PICKUP);
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
            damage -= usesModifier * usesModifier;
            if (damage < 0)
            {
               damage = 0;
            }

            ((Damageable) itemMeta).setDamage(damage);
            item.setItemStack(stack);
            kill();
         }
      }

      if (hasHitTarget())
      {
         kill();
      }
   }
}