package net.pottercraft.Ollivanders2.Spell;

import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Blasts spiders away
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class ARANIA_EXUMAI extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public ARANIA_EXUMAI ()
   {
      super();

      flavorText.add("\"Know any spells?\"\n\"One, but it's not powerful enough for all of them.\"\n\"Where's Hermione when you need her?\"\n\"Let's go! Arania Exumai\" -Harry Potter and Ron Weasley");
      flavorText.add("Defense Against Spiders");
      text = "Blasts away spiders.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public ARANIA_EXUMAI (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      List<Entity> entities = getCloseEntities(1);
      for (Entity entity : entities)
      {
         EntityType type = entity.getType();
         if (type == EntityType.SPIDER || type == EntityType.CAVE_SPIDER)
         {
            entity.setVelocity(player.getLocation().getDirection().normalize().multiply(usesModifier / 10));
            kill();
            return;
         }
      }
   }
}