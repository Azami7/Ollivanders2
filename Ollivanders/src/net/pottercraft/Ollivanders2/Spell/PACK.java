package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2API;
import net.pottercraft.Ollivanders2.StationarySpell.COLLOPORTUS;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;

import java.util.ArrayList;

/**
 * Pack is the incantation of a spell used to make items pack themselves into a trunk.
 *
 * @author cakenggt
 * @author Azami7
 */
public final class PACK extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PACK ()
   {
      super();

      spellType = O2SpellType.PACK;

      flavorText = new ArrayList<String>() {{
         add("Books, clothes, telescope and scales all soared into the air and flew pell-mell into the trunk.");
         add("The Packing Charm");
      }};

      text = "When this hits a chest, it will suck any items nearby into it.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PACK (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.PACK;
      setUsesModifier();
   }

   @Override
   public void checkEffect ()
   {
      move();
      Block block = getBlock();
      if (block.getType() == Material.CHEST)
      {
         for (StationarySpellObj stat : Ollivanders2API.getStationarySpells().getActiveStationarySpells())
         {
            if (stat instanceof COLLOPORTUS)
            {
               stat.flair(10);
               return;
            }
         }
         Chest c = (Chest) block.getState();
         Inventory inv = c.getInventory();
         if (inv.getHolder() instanceof DoubleChest)
         {
            inv = inv.getHolder().getInventory();
         }
         for (Item item : getItems(usesModifier))
         {
            if (inv.addItem(item.getItemStack()).size() == 0)
            {
               item.remove();
            }
         }
      }
   }
}