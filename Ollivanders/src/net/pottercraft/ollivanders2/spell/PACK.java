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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    *
    * @param plugin the Ollivanders2 plugin
    */
   public PACK(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.PACK;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<>()
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

      // world guard flags
      if (Ollivanders2.worldGuardEnabled)
         worldGuardFlags.add(Flags.CHEST_ACCESS);

      // only allow targeting chests
      materialAllowList.addAll(Ollivanders2Common.chests);

      initSpell();
   }

   @Override
   void doInitSpell()
   {
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

      // get nearby items
      List<Item> nearbyItems = common.getItemsInRadius(location, radius);

      Block targetBlock = getTargetBlock();
      if (!(targetBlock instanceof InventoryHolder))
      {
         common.printDebugMessage("Pack targeted non-chest block", null, null, true);
         kill();
         return;
      }

      InventoryHolder holder = ((InventoryHolder) targetBlock);
      Inventory inventory = holder.getInventory();

      for (Item item : nearbyItems)
      {
         HashMap<Integer, ItemStack> overflow;

         try
         {
            overflow = inventory.addItem(item.getItemStack());
         }
         catch (Exception e)
         {
            common.printDebugMessage("Exception in PACK", e, null, true);
         }
      }
   }
}