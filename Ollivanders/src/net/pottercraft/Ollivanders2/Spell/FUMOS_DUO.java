package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Causes blindness in a radius larger than fumos.
 *
 * @see FumosSuper
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class FUMOS_DUO extends FumosSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public FUMOS_DUO ()
   {
      super();

      flavorText.add("A Stronger Smoke-Screen Spell");
      text = "Fumos Duo will cause those in an area to be blinded by a smoke cloud. The blindness lasts for a time twice as long as that created by Fumos";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public FUMOS_DUO (Ollivanders2 plugin, Player player, O2SpellType name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      strength = 2;
   }
}
