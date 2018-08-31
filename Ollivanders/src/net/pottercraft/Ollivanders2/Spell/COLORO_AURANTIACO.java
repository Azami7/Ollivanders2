package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Created by Azami7 on 6/30/17.
 *
 * Target sheep or colored block turns orange.
 *
 * @author Azami7
 */
public final class COLORO_AURANTIACO extends ColoroSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public COLORO_AURANTIACO ()
   {
      super();

      text = "Turns target colorable entity or block orange.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public COLORO_AURANTIACO (Ollivanders2 plugin, Player player, O2SpellType name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      //set color orange
      color = DyeColor.ORANGE;
   }
}