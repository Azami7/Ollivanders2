package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;

/**
 * Herbivicus causes crops in a radius to grow.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class HERBIVICUS extends Herbology
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public HERBIVICUS ()
   {
      super();

      flavorText.add("The Plant-Growing Charm");
      text = "Herbivicus causes crops within a radius to grow.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public HERBIVICUS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      move();
      double radius = usesModifier;
      if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER && getBlock().getType() != Material.LAVA && getBlock().getType() != Material.STATIONARY_LAVA)
      {
         for (Block block : getBlocksInRadius(location, radius))
         {
            if (block.getState().getData() instanceof Crops)
            {
               List<CropState> stateList = new ArrayList<CropState>();
               stateList.add(CropState.SEEDED);
               stateList.add(CropState.GERMINATED);
               stateList.add(CropState.VERY_SMALL);
               stateList.add(CropState.SMALL);
               stateList.add(CropState.MEDIUM);
               stateList.add(CropState.TALL);
               stateList.add(CropState.VERY_TALL);
               stateList.add(CropState.RIPE);
               int currentState = stateList.indexOf(((Crops) block.getState().getData()).getState());
               int newState = currentState + 1;
               if (newState > 7)
               {
                  newState = 7;
               }
               BlockState blockState = block.getState();
               MaterialData blockData = blockState.getData();
               CropState newCropState = stateList.get(newState);
               ((Crops) blockData).setState(newCropState);
               blockState.setData(blockData);
               blockState.update();
            }
         }
         kill();
      }
   }
}