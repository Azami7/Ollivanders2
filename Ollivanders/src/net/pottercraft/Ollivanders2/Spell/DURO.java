package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Turns a target area or object to stone.
 *
 * @since 2.2.5
 * @see BlockTransfigurationSuper
 * @author Azami7
 */
public final class DURO extends BlockTransfigurationSuper
{
   public O2SpellType spellType = O2SpellType.DURO;

   protected ArrayList<String> flavorText = new ArrayList<String>() {{
      add("The Hardening Charm");
      add("The Hardening Charm will turn an object into solid stone. This can be surprisingly handy in a tight spot. Of course, most students only seem to use this spell to sabotage their fellow students' schoolbags or to turn a pumpkin pasty to stone just before someone bites into it. It is unwise to try this unworthy trick on any of your teachers.");
   }};

   protected String text = "Duro will turn the blocks in a radius in to stone.";

   protected O2MagicBranch branch = O2MagicBranch.CHARMS;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public DURO () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public DURO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      transfigureType = Material.STONE;
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