package net.pottercraft.Ollivanders2.Spell;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Spell which places a block of water against the targeted block.
 *
 * @author Azami7
 * @version Ollivanders2
 * @since 2.2.4
 */
public final class AGUAMENTI extends BlockTransfiguration
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public AGUAMENTI()
   {
      super();

      spellType = O2SpellType.AGUAMENTI;
      branch = O2MagicBranch.CHARMS;

      text = "Aguamenti will cause water to erupt against the surface you cast it on.";
      flavorText = new ArrayList<String>()
      {{
         add("The Water-Making Spell conjures clean, drinkable water from the end of the wand.");
         add("The Water-Making Spell");
      }};
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public AGUAMENTI (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.AGUAMENTI;
      branch = O2MagicBranch.CHARMS;

      transfigureType = Material.WATER;
      permanent = false;
      radius = 1;

      // pass-through materials
      projectilePassThrough.remove(Material.WATER);

      // set materials that can be transfigured by this spell
      materialWhitelist.add(Material.AIR);

      // world-guard flags
      worldGuardFlags.add(DefaultFlag.BUILD);

      initSpell();
   }

   /**
    * Override to make the target block the pass-through block before the actual target block
    *
    * @return the target block for aguamenti
    */
   @Override
   public Block getTargetBlock ()
   {
      if (hasHitTarget())
      {
         // we want the pass-through block before this block
         return location.subtract(super.vector).getBlock();
      }

      return null;
   }
}
