package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Causes blindness in a radius
 *
 * @see FumosSuper
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class FUMOS extends FumosSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public FUMOS ()
   {
      super();

      flavorText.add("The Smoke-Screen Spell");
      text = "Fumos will cause those in an area to be blinded by a smoke cloud.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public FUMOS (Ollivanders2 plugin, Player player, O2SpellType name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      strength = 1;
   }
}
