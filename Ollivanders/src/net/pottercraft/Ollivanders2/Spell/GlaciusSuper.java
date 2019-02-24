package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by Azami7 on 7/2/17.
 *
 * Glacius will cause a great cold to descend in a radius from it's impact point which freezes blocks. The radius and
 * duration of the freeze depend on your experience.
 *
 * @author Azami7
 */
public abstract class GlaciusSuper extends BlockTransfigurationSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public GlaciusSuper ()
   {
      super();

      branch = O2MagicBranch.CHARMS;
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public GlaciusSuper (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      branch = O2MagicBranch.CHARMS;

      if (usesModifier > 50)
      {
         radius = 5;
      }
      else if (usesModifier < 10)
      {
         radius = 1;
      }
      else
      {
         radius = (int) (usesModifier / 10);
      }
      permanent = false;

      spellDuration = (int)(usesModifier * 1200);

      transfigurationMap.put(Material.FIRE, Material.AIR);
      transfigurationMap.put(Material.WATER, Material.ICE);
      transfigurationMap.put(Material.LAVA, Material.OBSIDIAN);
      transfigurationMap.put(Material.ICE, Material.PACKED_ICE);

      materialWhitelist.add(Material.FIRE);
      materialWhitelist.add(Material.WATER);
      materialWhitelist.add(Material.LAVA);
      materialWhitelist.add(Material.ICE);
   }
}
