package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.material.Colorable;
import org.bukkit.material.MaterialData;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.ArrayList;

/**
 * Changes color of sheep and colorable blocks to a random color
 *
 * @author lownes
 * @author Azami7
 */
public final class COLOVARIA extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public COLOVARIA ()
   {
      super();

      spellType = O2SpellType.COLOVARIA;

      flavorText = new ArrayList<String>() {{
         add("The Colour Change Charm");
         add("[...] he wished he had not mixed up the incantations for Colour Change and Growth Charms, so that the rat he was supposed to be turning orange swelled shockingly and was the size of a badger before Harry could rectify his mistake.");
      }};

      text = "Changes color of sheep and colorable blocks to another color.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public COLOVARIA (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.COLOVARIA;
      setUsesModifier();
   }

   public void checkEffect ()
   {
      move();
      DyeColor[] values = DyeColor.values();
      DyeColor newColor = values[Math.abs(Ollivanders2.random.nextInt() % values.length)];
      for (LivingEntity live : getLivingEntities(2))
      {
         if (live instanceof Sheep)
         {
            Sheep sheep = (Sheep) live;
            sheep.setColor(newColor);
            kill();
            return;
         }
      }
      if (getBlock().getType() != Material.AIR)
      {
         for (Block block : p.common.getBlocksInRadius(location, usesModifier))
         {
            if (block.getState().getData() instanceof Colorable)
            {
               BlockState bs = block.getState();
               Colorable colorable = (Colorable) bs.getData();
               colorable.setColor(newColor);
               bs.setData((MaterialData) colorable);
               bs.update();
               kill();
            }
         }
      }
   }
}