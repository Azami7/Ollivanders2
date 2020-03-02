package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2Color;
import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Target sheep or colored block turns red.
 *
 * @author Azami7
 */
public final class COLOVARIA_VERMICULO extends ColoroSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public COLOVARIA_VERMICULO ()
   {
      spellType = O2SpellType.COLOVARIA_VERMICULO;
      branch = O2MagicBranch.CHARMS;

      text = "Turns target colorable entity or block red.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public COLOVARIA_VERMICULO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.COLOVARIA_VERMICULO;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      color = O2Color.RED;
   }
}
