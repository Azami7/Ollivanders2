package net.pottercraft.Ollivanders2.StationarySpell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Spell.SpellProjectile;

/**
 * Destroys spell projectiles crossing the boundary.
 *
 * @author lownes
 */
public class PROTEGO_HORRIBILIS extends StationarySpellObj implements StationarySpell
{
   public PROTEGO_HORRIBILIS (Ollivanders2 plugin, Player player, Location location, StationarySpells name, Integer radius, Integer duration)
   {
      super(plugin, player, location, name, radius, duration);
   }

   public PROTEGO_HORRIBILIS (Ollivanders2 plugin, Player player, Location location, StationarySpells name, Integer radius, Integer duration,
                              Map<String, String> spellData)
   {
      super(plugin, player, location, name, radius, duration);

      deserializeSpellData(spellData);
   }

   @Override
   public void checkEffect ()
   {
      age();
      List<SpellProjectile> projectiles = p.getProjectiles();
      if (projectiles != null)
      {
         List<SpellProjectile> projectiles2 = new ArrayList<>(projectiles);
         for (SpellProjectile proj : projectiles2)
         {
            if (isInside(proj.location))
            {
               if (location.distance(proj.location) > radius - 1)
               {
                  p.remProjectile(proj);
               }
            }
         }
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