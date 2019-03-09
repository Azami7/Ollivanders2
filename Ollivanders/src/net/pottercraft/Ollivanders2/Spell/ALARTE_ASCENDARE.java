package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.ArrayList;
import java.util.List;

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

      spellType = O2SpellType.ALARTE_ASCENDARE;

      flavorText = new ArrayList<String>() {{
         add("The Winged-Ascent Charm");
         add("He brandished his wand at the snake and there was a loud bang; the snake, instead of vanishing, "
               + "flew ten feet into the air and fell back to the floor with a loud smack.");
      }};

      text = "Shoots target entity in to the air.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public ALARTE_ASCENDARE (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.ALARTE_ASCENDARE;
      setUsesModifier();
   }

   /**
    * Search for entities or items at the projectile's current location
    */
   @Override
   protected void doCheckEffect ()
   {
      double up = usesModifier * 0.4;
      if (up > 4)
      {
         up = 4;
      }
      Vector vec = new Vector(0, up, 0);

      // check for entities first
      for (LivingEntity lentity : getLivingEntities(1.5))
      {
         if (lentity.getUniqueId() == player.getUniqueId())
            continue;

         lentity.setVelocity(lentity.getVelocity().add(vec));
         kill();
         return;
      }

      // check for items next
      List<Item> items = getItems(1.5);

      if (items != null && items.size() > 0)
      {
         Item item = items.get(0);
         item.setVelocity(item.getVelocity().add(vec));

         kill();
      }

      // projectile has stopped, kill the spell
      if (hasHitTarget())
      {
         kill();
      }
   }
}