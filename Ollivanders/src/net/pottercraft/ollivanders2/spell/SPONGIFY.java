package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

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
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public SPONGIFY ()
   {
      super();

      spellType = O2SpellType.SPONGIFY;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>() {{
         add("\"Today's lesson will most assuredly involve learning how to cast the Softening Charm, Spongify.\" -Filius Flitwick");
         add("The Softening Charm");
      }};

      text = "Turns the blocks in a radius in to slime blocks.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public SPONGIFY (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.SPONGIFY;
      branch = O2MagicBranch.CHARMS;
      setUsesModifier();

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
      materialBlacklist.add(Material.LAVA);
      materialBlacklist.add(Material.FIRE);
   }
}
