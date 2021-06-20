package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Turns a target area or object to stone.
 *
 * @author Azami7
 * @see BlockTransfiguration
 * @since 2.2.5
 */
public final class DURO extends BlockTransfiguration
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public DURO()
   {
      super();

      spellType = O2SpellType.DURO;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>()
      {{
         add("The Hardening Charm");
         add("The Hardening Charm will turn an object into solid stone. This can be surprisingly handy in a tight spot. Of course, most students only seem to use this spell to sabotage their fellow students' schoolbags or to turn a pumpkin pasty to stone just before someone bites into it. It is unwise to try this unworthy trick on any of your teachers.");
      }};

      text = "Duro will turn the blocks in a radius in to stone.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public DURO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.DURO;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      transfigureType = Material.STONE;
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
   }
}