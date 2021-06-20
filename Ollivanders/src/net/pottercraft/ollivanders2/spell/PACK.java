package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.stationaryspell.COLLOPORTUS;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.StationarySpellObj;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Pack is the incantation of a spell used to make items pack themselves into a trunk.
 *
 * @author cakenggt
 * @author Azami7
 */
public final class PACK extends O2Spell
{
   private int radius;
   private final static int maxRadius = 20;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PACK()
   {
      super();

      spellType = O2SpellType.PACK;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>()
      {{
         add("Books, clothes, telescope and scales all soared into the air and flew pell-mell into the trunk.");
         add("The Packing Charm");
      }};

      text = "When this hits a chest, it will suck any items nearby into it.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PACK(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.PACK;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      // world guard flags
      if (Ollivanders2.worldGuardEnabled)
         worldGuardFlags.add(Flags.CHEST_ACCESS);

      // remove chests from material blacklist
      for (Material chest : Ollivanders2Common.chests)
      {
         materialBlackList.remove(chest);
      }

      radius = ((int) usesModifier / 10) + 1;
      if (radius > maxRadius)
      {
         radius = maxRadius;
      }
   }

   @Override
   protected void doCheckEffect()
   {
      if (!hasHitTarget())
      {
         return;
      }

      Block block = getTargetBlock();
      if (block == null)
      {
         common.printDebugMessage("PACK.doCheckEffect: target block is null", null, null, true);
         kill();
         return;
      }

      if (Ollivanders2Common.chests.contains(block.getType()))
      {
         for (StationarySpellObj stat : Ollivanders2API.getStationarySpells(p).getActiveStationarySpells())
         {
            if (stat instanceof COLLOPORTUS)
            {
               stat.flair(10);

               kill();
               return;
            }
         }

         Inventory inv;

         try
         {
            Chest c = (Chest) block.getState();
            inv = c.getInventory();
            if (inv.getHolder() instanceof DoubleChest)
            {
               inv = inv.getHolder().getInventory();
            }
         }
         catch (Exception e)
         {
            kill();
            return;
         }

         for (Item item : getItems(radius))
         {
            if (inv.addItem(item.getItemStack()).size() == 0)
            {
               item.remove();
            }
         }
      }

      kill();
   }
}