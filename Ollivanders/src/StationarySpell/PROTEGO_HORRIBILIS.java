package StationarySpell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.StationarySpellObj;
import net.pottercraft.Ollivanders2.StationarySpells;

/**
 * Destroys spell projectiles crossing the boundary.
 *
 * @author lownes
 */
public class PROTEGO_HORRIBILIS extends StationarySpellObj implements StationarySpell
{

   /**
    *
    */
   private static final long serialVersionUID = -500146474072361113L;

   public PROTEGO_HORRIBILIS (Player player, Location location,
                              StationarySpells name, Integer radius, Integer duration)
   {
      super(player, location, name, radius, duration);
   }

   public void checkEffect (Ollivanders2 p)
   {
      age();
      List<SpellProjectile> projectiles = p.getProjectiles();
      if (projectiles != null)
      {
         List<SpellProjectile> projectiles2 = new ArrayList<SpellProjectile>(projectiles);
         for (SpellProjectile proj : projectiles2)
         {
            if (isInside(proj.location))
            {
               if (location.toLocation().distance(proj.location) > radius - 1)
               {
                  p.remProjectile(proj);
               }
            }
         }
      }
   }

}