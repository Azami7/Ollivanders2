package net.pottercraft.Ollivanders2.StationarySpell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.pottercraft.Ollivanders2.Spell.O2SpellType;
import net.pottercraft.Ollivanders2.Spell.O2Spell;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Shield spell
 *
 */
public class PROTEGO extends StationarySpellObj implements StationarySpell
{
   /**
    * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
    *
    * @param plugin a callback to the MC plugin
    */
   public PROTEGO (Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2StationarySpellType.PROTEGO;
   }

   /**
    * Constructor
    *
    * @param plugin a callback to the MC plugin
    * @param pid the player who cast the spell
    * @param location the center location of the spell
    * @param type the type of this spell
    * @param radius the radius for this spell
    * @param duration the duration of the spell
    */
   public PROTEGO (Ollivanders2 plugin, UUID pid, Location location, O2StationarySpellType type, Integer radius,
                   Integer duration)
   {
      super(plugin, pid, location, type, radius, duration);

      spellType = O2StationarySpellType.PROTEGO;
   }

   public void checkEffect ()
   {
      Player ply = Bukkit.getPlayer(getCasterID());
      if (ply == null)
      {
         kill();
         return;
      }
      double rightWand = p.playerCommon.wandCheck(ply);
      if (ply.isSneaking() && rightWand != -1)
      {
         location = ply.getEyeLocation();
         flair(1);
         List<O2Spell> projectiles = p.getProjectiles();
         if (projectiles != null)
         {
            for (O2Spell proj : projectiles)
            {
               if (isInside(proj.location))
               {
                  if (location.distance(proj.location) > radius - 1)
                  {
                     Vector N = proj.location.toVector().subtract(location.toVector()).normalize();
                     double b = p.getSpellNum(ply, O2SpellType.PROTEGO) / rightWand / 10.0;
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
    * @return a map of the serialized data
    */
   @Override
   public Map<String, String> serializeSpellData ()
   {
      return new HashMap<>();
   }

   /**
    * Deserialize the data for this spell and load the data to this spell.
    *
    * @param spellData a map of the saved spell data
    */
   @Override
   public void deserializeSpellData (Map<String, String> spellData) { }
}