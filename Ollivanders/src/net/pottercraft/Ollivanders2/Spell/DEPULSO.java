package net.pottercraft.Ollivanders2.Spell;

import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Shoots the hit entity away from you.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class DEPULSO extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public DEPULSO ()
   {
      super();

      flavorText.add("They were supposed to be practising the opposite of the Summoning Charm today — the Banishing Charm. Owing to the potential for nasty accidents when objects kept flying across the room. Professor Flitwick had given each student a stack of cushions on which to practise, the theory being that these wouldn’t hurt anyone if they went off target.");
      flavorText.add("The Banishing Charm");
      text = "Depulso will repel any entity you hit with it.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public DEPULSO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      List<Entity> entities = getCloseEntities(1);
      for (Entity entity : entities)
      {
         entity.setVelocity(player.getLocation().getDirection().normalize().multiply(usesModifier / 20));
         kill();
         return;
      }
   }
}