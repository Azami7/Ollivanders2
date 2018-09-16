package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
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
   public O2SpellType spellType = O2SpellType.ARANIA_EXUMAI;

   protected ArrayList<String> flavorText = new ArrayList<String>() {{
      add("\"Know any spells?\"\n\"One, but it's not powerful enough for all of them.\"\n\"Where's Hermione when you need her?\"\n\"Let's go! Arania Exumai\" -Harry Potter and Ron Weasley");
      add("Defense Against Spiders");
   }};

   protected String text = "Blasts away spiders.";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public ARANIA_EXUMAI () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public ARANIA_EXUMAI (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
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