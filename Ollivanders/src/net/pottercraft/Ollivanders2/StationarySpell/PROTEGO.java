package net.pottercraft.Ollivanders2.StationarySpell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.pottercraft.Ollivanders2.*;
import net.pottercraft.Ollivanders2.Spell.SpellProjectile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * Shield spell
 *
 */
public class PROTEGO extends StationarySpellObj implements StationarySpell
{
   public PROTEGO (Player player, Location location, StationarySpells name, Integer radius, Integer duration)
   {
      super(player, location, name, radius, duration);
   }

   public PROTEGO (Player player, Location location, StationarySpells name, Integer radius, Integer duration,
                   Map<String, String> spellData, Ollivanders2 plugin)
   {
      super(player, location, name, radius, duration);

      deserializeSpellData(spellData, plugin);
   }

   public void checkEffect (Ollivanders2 p)
   {
      Player ply = Bukkit.getPlayer(getPlayerUUID());
      if (ply == null)
      {
         kill();
         return;
      }
      double rightWand = p.wandCheck(ply);
      if (ply.isSneaking() && rightWand != -1)
      {
         location = new OLocation(ply.getEyeLocation());
         flair(1);
         List<SpellProjectile> projectiles = p.getProjectiles();
         if (projectiles != null)
         {
            for (SpellProjectile proj : projectiles)
            {
               if (isInside(proj.location))
               {
                  if (location.toLocation().distance(proj.location) > radius - 1)
                  {
                     Vector N = proj.location.toVector().subtract(location.toLocation().toVector()).normalize();
                     double b = p.getSpellNum(ply, Spells.PROTEGO) / rightWand / 10.0;
                     b += 1;
                     Vector V = proj.vector.clone();
                     proj.vector = N.multiply((V.dot(N))).multiply(-2).add(V).multiply(b);
                     flair(10);
                  }
               }
            }
         }
      }
      else
      {
         kill();
      }
   }

   /**
    * Serialize all data specific to this spell so it can be saved.
    *
    * @param p unused for this spell
    * @return a map of the serialized data
    */
   @Override
   public Map<String, String> serializeSpellData (Ollivanders2 p)
   {
      return new HashMap<>();
   }

   /**
    * Deserialize the data for this spell and load the data to this spell.
    *
    * @param spellData a map of the saved spell data
    * @param p unused for this spell
    */
   @Override
   public void deserializeSpellData (Map<String, String> spellData, Ollivanders2 p) { }
}