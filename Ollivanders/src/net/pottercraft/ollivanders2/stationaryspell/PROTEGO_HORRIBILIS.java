package net.pottercraft.ollivanders2.stationaryspell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.AVADA_KEDAVRA;
import org.bukkit.Location;

import net.pottercraft.ollivanders2.spell.O2Spell;
import org.jetbrains.annotations.NotNull;

/**
 * Destroys spell projectiles crossing the boundary.
 *
 * @author lownes
 */
public class PROTEGO_HORRIBILIS extends ShieldSpell
{
   /**
    * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
    *
    * @param plugin a callback to the MC plugin
    */
   public PROTEGO_HORRIBILIS(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2StationarySpellType.PROTEGO_HORRIBILIS;
   }

   /**
    * Constructor
    *
    * @param plugin   a callback to the MC plugin
    * @param pid      the player who cast the spell
    * @param location the center location of the spell
    * @param type     the type of this spell
    * @param radius   the radius for this spell
    * @param duration the duration of the spell
    */
   public PROTEGO_HORRIBILIS(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, @NotNull O2StationarySpellType type, int radius, int duration)
   {
      super(plugin, pid, location, type, radius, duration);

      spellType = O2StationarySpellType.PROTEGO_HORRIBILIS;
   }

   /**
    * Upkeep
    */
   @Override
   public void checkEffect()
   {
      age();
      List<O2Spell> projectiles = p.getProjectiles();

      List<O2Spell> projectiles2 = new ArrayList<>(projectiles);
      for (O2Spell proj : projectiles2)
      {
         // https://harrypotter.fandom.com/wiki/Shield_Charm
         // "However, this shield isn't completely impenetrable, as it cannot block a Killing Curse."
         if (proj instanceof AVADA_KEDAVRA)
         {
            continue;
         }

         if (isInside(proj.location))
         {
            if (location.distance(proj.location) > radius - 1)
            {
               p.removeProjectile(proj);
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
   @NotNull
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
   public void deserializeSpellData(@NotNull Map<String, String> spellData) { }
}