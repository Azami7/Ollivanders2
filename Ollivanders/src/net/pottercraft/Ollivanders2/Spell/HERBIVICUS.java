package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2API;
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

      spellType = O2SpellType.HERBIVICUS;

      flavorText = new ArrayList<String>() {{
         add("The Plant-Growing Charm");
      }};

      text = "Herbivicus causes crops within a radius to grow.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public HERBIVICUS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.HERBIVICUS;
      setUsesModifier();
   }

   @Override
   public void checkEffect ()
   {
      move();
      double radius = usesModifier;
      Material targetBlockType = getBlock().getType();
      if (targetBlockType != Material.AIR && targetBlockType != Material.FIRE && targetBlockType != Material.WATER && targetBlockType != Material.LAVA)
      {
         for (Block block : Ollivanders2API.common.getBlocksInRadius(location, radius))
         {
            if (block.getState().getData() instanceof Crops)
            {
               List<CropState> stateList = new ArrayList<>();
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