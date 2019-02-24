package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.O2Color;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Target sheep or colored block turns green.
 *
 * @author Azami7
 */
public final class COLOVARIA_VERIDI extends ColoroSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public COLOVARIA_VERIDI ()
   {
      super();

      spellType = O2SpellType.COLOVARIA_VERIDI;
      text = "Turns target colorable entity or block green.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public COLOVARIA_VERIDI (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.COLOVARIA_VERIDI;
      setUsesModifier();

      color = O2Color.GREEN;
   }
}

