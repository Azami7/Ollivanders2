package net.pottercraft.Ollivanders2.Spell;

import java.util.List;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.pottercraft.Ollivanders2.O2MagicBranch;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Turns sticks in to arrows. Created by Azami7 on 6/29/17. Imported from iarepandemonium/Ollivanders.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class CALAMUS extends Transfiguration
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public CALAMUS ()
   {
      super();

      spellType = O2SpellType.CALAMUS;
      branch = O2MagicBranch.TRANSFIGURATION;

      text = "Turns sticks in to arrows.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public CALAMUS(Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.CALAMUS;
      branch = O2MagicBranch.TRANSFIGURATION;

      initSpell();

      // world guard
      worldGuardFlags.add(DefaultFlag.ITEM_DROP);
   }

   /**
    * Look for sticks in the projectile's location and turn them in to arrows
    */
   @Override
   protected void doCheckEffect()
   {
      List<Item> items = getItems(1.5);

      if (items.size() > 0)
      {
         for (Item item : items)
         {
            Material mat = item.getItemStack().getType();

            if (mat == Material.STICK)
            {
               int amount = item.getItemStack().getAmount();
               Location loc = item.getLocation();
               ItemStack drop = new ItemStack(Material.ARROW);
               drop.setAmount(amount);
               loc.getWorld().dropItem(loc, drop);
               item.remove();

               break;
            }
         }

         kill();
         return;
      }

      // projectile has stopped, kill the spell
      if (hasHitTarget())
         kill();
   }
}
