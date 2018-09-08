package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Created by kristin on 6/29/17.
 *
 * Target sheep or colored block turns blue.
 *
 * @author Azami7
 */
public final class COLORO_CAERULUS extends ColoroSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public COLORO_CAERULUS ()
   {
      super();

      text = "Turns target colorable entity or block blue.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public COLORO_CAERULUS (Ollivanders2 plugin, Player player, O2SpellType name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      //set color blue
      color = DyeColor.BLUE;
   }
}
