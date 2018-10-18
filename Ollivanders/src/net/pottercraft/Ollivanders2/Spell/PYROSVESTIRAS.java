package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by Phevek on 10/17/18.
 *
 * Pyrosvestiras is a fire extinquishing spell if you decide that you want to put out that thing you lit on fire.
 *
 * @author Phevek
 */
public class PYROSVESTIRAS extends BlockTransfigurationSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PYROSVESTIRAS()
   {
      super();

      branch = O2MagicBranch.CHARMS;


      spellType = O2SpellType.PYROSVESTIRAS;

      flavorText = new ArrayList<String>() {{
         add("A charm that extinguishes fires. Most commonly employed by Dragonologists.");
         add("The Extinguishing Charm");
      }};

      text = "A spell that turns fire into air.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PYROSVESTIRAS(Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      branch = O2MagicBranch.CHARMS;

      if (usesModifier > 50)
      {
         radius = 10;
      }
      else if (usesModifier < 10)
      {
         radius = 1;
      }
      else
      {
         radius = (int) (usesModifier / 10);
      }

      permanent = true;

      transfigurationMap.put(Material.FIRE, Material.AIR);

      materialWhitelist.add(Material.FIRE);
   }
}
