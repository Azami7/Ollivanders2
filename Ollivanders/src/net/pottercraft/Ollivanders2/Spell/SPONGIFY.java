package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * The Softening Charm (Spongify) is a spell that softens a target area or object, making it rubbery and bouncy.
 *
 * @link https://github.com/Azami7/Ollivanders2/issues/93
 * @since 2.2.5
 * @see BlockTransfigurationSuper
 * @author Azami7
 */
public class SPONGIFY extends BlockTransfigurationSuper
{
   protected O2MagicBranch branch = O2MagicBranch.CHARMS;

   public SPONGIFY ()
   {
      super();

      flavorText.add("\"Today's lesson will most assuredly involve learning how to cast the Softening Charm, Spongify.\" -Filius Flitwick");
      flavorText.add("The Softening Charm");

      text = "Turns the blocks in a radius in to slime blocks.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public SPONGIFY (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      transfigureType = Material.SLIME_BLOCK;
      spellDuration = (int)(1200 * usesModifier);
      permanent = false;

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

      materialBlacklist.add(Material.WATER);
      materialBlacklist.add(Material.STATIONARY_WATER);
      materialBlacklist.add(Material.LAVA);
      materialBlacklist.add(Material.STATIONARY_LAVA);
      materialBlacklist.add(Material.FIRE);
   }
}
