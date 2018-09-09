package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Created by Azami7 on 6/30/17.
 *
 * Target sheep or colored block turns purple.
 *
 * @author Azami7
 */
public final class COLORO_OSTRUM extends ColoroSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public COLORO_OSTRUM (O2SpellType type)
   {
      super(type);

      text = "Turns target colorable entity or block purple.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param type
    * @param rightWand
    */
   public COLORO_OSTRUM (Ollivanders2 plugin, Player player, O2SpellType type, Double rightWand)
   {
      super(plugin, player, type, rightWand);

      //set color purple
      color = DyeColor.PURPLE;
   }
}
