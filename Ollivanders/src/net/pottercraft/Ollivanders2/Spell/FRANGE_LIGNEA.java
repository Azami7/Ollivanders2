package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Bursts a log into a stack of coreless wands, whose number depends on the player's spell level.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class FRANGE_LIGNEA extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public FRANGE_LIGNEA ()
   {
      super();

      spellType = O2SpellType.FRANGE_LIGNEA;
      text = "Frange lignea will cause a log of the spruce, oak, birch, or jungle species to explode into coreless wands.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public FRANGE_LIGNEA (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.FRANGE_LIGNEA;
      setUsesModifier();
   }

   @Override
   public void checkEffect ()
   {
      move();
      Block block = super.getBlock();
      if (block.getType() == Material.LOG)
      {
         block.getLocation().getWorld().createExplosion(block.getLocation(), 0);
         int data = block.getState().getData().toItemStack(1).getDurability() % 4;
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