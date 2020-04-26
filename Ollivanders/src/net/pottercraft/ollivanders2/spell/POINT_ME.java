package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Spell which points you in the direction of north.
 *
 * @version Ollivanders2
 * @author autumnwoz
 */
public class POINT_ME extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public POINT_ME ()
   {
      super();

      spellType = O2SpellType.POINT_ME;

      flavorText = new ArrayList<String>() {{
         add("\"\'Point Me!\' he whispered again to his wand, and it spun around and pointed him to the right-hand one.\"");
         add("The Four-Point Spell");
      }};

      text = "Points the player north.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public POINT_ME (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.POINT_ME;
      setUsesModifier();
   }


   @Override
   public void checkEffect ()
   {
      Location l = player.getLocation().clone();
      l.setYaw(180);
      player.teleport(l);
      kill();
   }
}