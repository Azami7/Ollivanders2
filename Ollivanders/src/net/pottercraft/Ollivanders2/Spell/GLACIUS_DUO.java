package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

/**
 * Glacius Duo has the same effect as Glacius but with 2x the duration and radius.
 *
 * @see GlaciusSuper
 * @author Azami7
 */
public final class GLACIUS_DUO extends GlaciusSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public GLACIUS_DUO ()
   {
      super();

      flavorText.add("A Stronger Freezing Charm");
      flavorText.add("\"It's about preparing ourselves ...for what's waiting for us out there.\" -Hermione Granger");
      text = "Glacius Duo will freeze blocks in a radius twice that of glacius, but for half the time.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public GLACIUS_DUO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      // 50% duration
      durationModifier = 0.5;
      // 2x radius
      radiusModifier = 2.0;
   }
}