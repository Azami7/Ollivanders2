package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Turns an entity to stone. If the stone is broken, the entity will not respawn at the end of the transfiguration.
 *
 * @author lownes
 * @author Azami7
 */
public final class DURO extends Transfiguration
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public DURO ()
   {
      super();

      flavorText.add("The Hardening Charm");
      flavorText.add("The Hardening Charm will turn an object into solid stone. This can be surprisingly handy in a tight spot. Of course, most students only seem to use this spell to sabotage their fellow students' schoolbags or to turn a pumpkin pasty to stone just before someone bites into it. It is unwise to try this unworthy trick on any of your teachers.");
      text = "Duro will transfigure an entity into a stone.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public DURO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      if (!hasTransfigured())
      {
         move();
         for (Entity e : getCloseEntities(1))
         {
            if (e.getType() != EntityType.PLAYER)
            {
               location = e.getLocation();
               location.getBlock().setType(Material.STONE);
               transfigureEntity(e, null, null);
               return;
            }
         }
      }
      else
      {
         if (lifeTicks > 160)
         {
            kill = true;
            if (location.getBlock().getType() == Material.STONE)
            {
               location.getBlock().setType(Material.AIR);
               endTransfigure();
            }
         }
         else
         {
            lifeTicks++;
            if (location.getBlock().getType() != Material.STONE)
            {
               kill = true;
            }
         }
      }
   }
}