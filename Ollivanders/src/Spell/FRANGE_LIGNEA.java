package Spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Bursts a log into a stack of coreless wands, whose number depends on
 * the player's spell level.
 *
 * @author lownes
 */
public class FRANGE_LIGNEA extends SpellProjectile implements Spell
{

   public FRANGE_LIGNEA (Ollivanders2 p, Player player, Spells name, Double rightWand)
   {
      super(p, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      Block block = super.getBlock();
      if (block.getType() == Material.LOG)
      {
         block.getLocation().getWorld().createExplosion(block.getLocation(), 0);
         @SuppressWarnings("deprecation")
         int data = block.getData() % 4;
         String[] woodTypes = {"Oak", "Spruce", "Birch", "Jungle"};
         int number = (int) (usesModifier * 0.8);
         if (number > 0)
         {
            ItemStack shellStack = new ItemStack(Material.STICK, number);
            ItemMeta shellM = shellStack.getItemMeta();
            shellM.setDisplayName("Coreless Wand");
            List<String> lore = new ArrayList<String>();
            lore.add(woodTypes[data]);
            shellM.setLore(lore);
            shellStack.setItemMeta(shellM);
            player.getWorld().dropItemNaturally(block.getLocation(), shellStack);
         }
         block.setType(Material.AIR);
      }
   }
}