package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.O2Color;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Target sheep or colored block turns blue.
 *
 * @author Azami7
 */
public final class COLOVARIA_CAERULUS extends ColoroSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public COLOVARIA_CAERULUS ()
   {
      super();

      spellType = O2SpellType.COLOVARIA_CAERULUS;
      text = "Turns target colorable entity or block blue.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public COLOVARIA_CAERULUS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.COLOVARIA_CAERULUS;

      // set up usage modifier, has to be done here to get the uses for this specific spell
      setUsesModifier();

      color = O2Color.BLUE;
   }
}
