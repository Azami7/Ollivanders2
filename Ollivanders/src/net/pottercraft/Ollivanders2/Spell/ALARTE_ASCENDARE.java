package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Shoots target high into air.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class ALARTE_ASCENDARE extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public ALARTE_ASCENDARE ()
   {
      super();

      flavorText.add("The Winged-Ascent Charm");
      flavorText.add("He brandished his wand at the snake and there was a loud bang; the snake, instead of vanishing, "
            + "flew ten feet into the air and fell back to the floor with a loud smack.");

      text = "Shoots target entity in to the air.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public ALARTE_ASCENDARE (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      double up = usesModifier * 0.4;
      if (up > 4)
      {
         up = 4;
      }
      Vector vec = new Vector(0, up, 0);
      for (LivingEntity lentity : getLivingEntities(1))
      {
         lentity.setVelocity(lentity.getVelocity().add(vec));
         kill();
         return;
      }
      for (Item item : getItems(1))
      {
         item.setVelocity(item.getVelocity().add(vec));
         kill();
         return;
      }
   }
}