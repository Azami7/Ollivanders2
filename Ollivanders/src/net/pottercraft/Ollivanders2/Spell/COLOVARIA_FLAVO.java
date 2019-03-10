package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.O2Color;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Target sheep or colored block turns yellow.
 *
 * @author Azami7
 */
public final class COLOVARIA_FLAVO extends ColoroSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public COLOVARIA_FLAVO ()
   {
      super();

      spellType = O2SpellType.COLOVARIA_FLAVO;
      text = "Turns target colorable entity or block yellow.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public COLOVARIA_FLAVO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.COLOVARIA_FLAVO;
      setUsesModifier();

      color = O2Color.YELLOW;
   }
}
