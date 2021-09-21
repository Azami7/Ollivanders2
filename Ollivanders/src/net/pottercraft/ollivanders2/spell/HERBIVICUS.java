package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.material.Crops;
import org.jetbrains.annotations.NotNull;

/**
 * Herbivicus causes crops in a radius to grow.
 *
 * @author lownes
 * @author Azami7
 * @version Ollivanders2
 */
public final class HERBIVICUS extends O2Spell
{
   private List<CropState> stateList = new ArrayList<>();

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public HERBIVICUS(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.HERBIVICUS;
      branch = O2MagicBranch.HERBOLOGY;

      flavorText = new ArrayList<>()
      {{
         add("The Plant-Growing Charm");
      }};

      text = "Herbivicus causes crops within a radius to grow.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public HERBIVICUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.HERBIVICUS;
      branch = O2MagicBranch.HERBOLOGY;

      initSpell();

      // pass-through materials
      projectilePassThrough.add(Material.WATER);

      stateList.add(CropState.SEEDED);
      stateList.add(CropState.GERMINATED);
      stateList.add(CropState.VERY_SMALL);
      stateList.add(CropState.SMALL);
      stateList.add(CropState.MEDIUM);
      stateList.add(CropState.TALL);
      stateList.add(CropState.VERY_TALL);
      stateList.add(CropState.RIPE);
   }

   /**
    * Grow all the crops in a radius of the spell target
    */
   @Override
   protected void doCheckEffect()
   {
      if (!hasHitTarget())
         return;

      double radius = usesModifier;

      for (Block block : Ollivanders2API.common.getBlocksInRadius(location, radius))
      {
         BlockData blockData = block.getBlockData();

         if (blockData instanceof Ageable)
         {
            int cropState = ((Ageable) blockData).getAge();

            int newCropState = cropState + 1;
            if (newCropState > 7)
            {
               newCropState = 7;
            }

            ((Ageable) blockData).setAge(newCropState);
            block.setBlockData(blockData);
         }
      }

      kill();
   }
}