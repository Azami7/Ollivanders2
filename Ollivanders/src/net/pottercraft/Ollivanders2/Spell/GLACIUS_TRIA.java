package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

/**
 * Glacius Tria has the same effect as Glacius but with 4x the duration and radius.
 *
 * @see GlaciusSuper
 * @version Ollivanders2
 * @author Azami7
 */
public final class GLACIUS_TRIA extends GlaciusSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public GLACIUS_TRIA ()
   {
      super();

      flavorText.add("The Strongest Freezing Charm");
      text = "Glacius Tria will freeze blocks in a radius four times that of glacius, but for one quarter the time.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public GLACIUS_TRIA (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      //25% duration
      durationModifier = 0.25;
      //4x radius radius
      radiusModifier = 4.0;
   }
}