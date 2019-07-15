package net.pottercraft.Ollivanders2.StationarySpell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Location;

import java.util.UUID;

public abstract class ShieldSpell extends StationarySpellObj
{
   /**
    * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
    *
    * @param plugin a callback to the MC plugin
    */
   public ShieldSpell (Ollivanders2 plugin)
   {
      super(plugin);
   }

   /**
    * Constructor
    *
    * @param plugin   a callback to the MC plugin
    * @param playerID the player who cast the spell
    * @param loc      the center location of the spell
    * @param type     the type of this spell
    * @param radius   the radius for this spell
    * @param duration the duration of the spell
    */
   public ShieldSpell (Ollivanders2 plugin, UUID playerID, Location loc, O2StationarySpellType type, Integer radius, Integer duration)
   {
      super(plugin, playerID, loc, type, radius, duration);
   }
}
