package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

/**
 * Glacius will cause a great cold to descend in a radius from it's impact point which freezes blocks. The radius and
 * duration of the freeze depend on your experience.
 *
 * @see GlaciusSuper
 * @author Azami7
 */
public final class GLACIUS extends GlaciusSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public GLACIUS ()
   {
      super();

      flavorText.add("A charm that conjures a blast of freezing cold air from the end of the wand.");
      flavorText.add("The Freezing Charm");
      text = "Turns fire in to air, water in to ice, ice to packed ice, and lava in to obsidian.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public GLACIUS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      // normal duration
      durationModifier = 1.0;
      // normal radius
      radiusModifier = 1.0;
   }
}