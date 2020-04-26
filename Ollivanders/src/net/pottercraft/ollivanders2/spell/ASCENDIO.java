package net.pottercraft.ollivanders2.spell;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.pottercraft.ollivanders2.Ollivanders2;

import java.util.ArrayList;

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

      spellType = O2SpellType.ASCENDIO;

      flavorText = new ArrayList<String>() {{
         add("The Climbing Charm");
         add("Underwater he casts a spell which propels him towards the surface, he flies out and lands on the decking where the crowd are.");
      }};

      text = "Propels the caster into the air.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public ASCENDIO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.ASCENDIO;
      setUsesModifier();
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