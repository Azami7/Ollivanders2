package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;

import org.bukkit.entity.Player;

/**
 * Creates an explosion at the target location twice as powerful as bombarda. Doesn't break blocks.
 *
 * @see BombardaSuper
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class BOMBARDA_MAXIMA extends BombardaSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public BOMBARDA_MAXIMA ()
   {
      super();

      flavorText.add("A more powerful explosion incantation.");
      flavorText.add("\"Come on, let’s get destroying... Confringo? Stupefy? Bombarda? Which would you use?\" -Albus Potter");
      text = "Bombarda Maxima creates an explosion twice as powerful as Bombarda which doesn't damage the terrain.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public BOMBARDA_MAXIMA (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      strength = 1.6;
   }
}