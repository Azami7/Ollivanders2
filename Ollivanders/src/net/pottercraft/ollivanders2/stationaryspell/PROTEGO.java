package net.pottercraft.ollivanders2.stationaryspell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.O2Spell;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.util.Vector;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Shield spell
 */
public class PROTEGO extends ShieldSpell
{
   /**
    * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
    *
    * @param plugin a callback to the MC plugin
    */
   public PROTEGO(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2StationarySpellType.PROTEGO;
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
   public PROTEGO(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, @NotNull O2StationarySpellType type, int radius, int duration)
   {
      super(plugin, pid, location, type, radius, duration);

      spellType = O2StationarySpellType.PROTEGO;
   }

   /**
    * Upkeep
    */
   @Override
   public void checkEffect ()
   {
      age();

      Player ply = Bukkit.getPlayer(getCasterID());
      if (ply == null)
      {
         kill();
         return;
      }

      double rightWand = Ollivanders2API.playerCommon.wandCheck(ply);
      if (ply.isSneaking() && rightWand != -1)
      {
         location = ply.getEyeLocation();
         flair(1);

         List<O2Spell> projectiles = p.getProjectiles();

         for (O2Spell proj : projectiles)
         {
            if (isInside(proj.location))
            {
               if (location.distance(proj.location) > radius - 1)
               {
                  Vector N = proj.location.toVector().subtract(location.toVector()).normalize();
                  double b = p.getSpellCount(ply, O2SpellType.PROTEGO) / rightWand / 10.0;
                  b += 1;
                  Vector V = proj.vector.clone();
                  proj.vector = N.multiply((V.dot(N))).multiply(-2).add(V).multiply(b);
                  flair(10);
               }
            }
         }
      }
   }

   /**
    * Handle entity combust by block events
    *
    * @param event the event
    */
   @Override
   void doOnEntityCombustEvent(@NotNull EntityCombustEvent event)
   {
      Entity entity = event.getEntity();
      Location entityLocation = entity.getLocation();

      if (isInside(entityLocation))
      {
         event.setCancelled(true);
         common.printDebugMessage("PROTEGO: canceled PlayerInteractEvent", null, null, false);
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