package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Shoots caster high into air
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class ASCENDIO extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public ASCENDIO ()
   {
      super();

      flavorText.add("The Climbing Charm");
      flavorText.add("Underwater he casts a spell which propels him towards the surface, he flies out and lands on the decking where the crowd are.");
      text = "Propels the caster into the air.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public ASCENDIO (Ollivanders2 plugin, Player player, O2SpellType name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      double up = usesModifier * 0.8;
      if (up > 4)
      {
         up = 4;
      }
      Vector vec = new Vector(0, up, 0);
      player.setVelocity(player.getVelocity().add(vec));
      kill();
   }
}