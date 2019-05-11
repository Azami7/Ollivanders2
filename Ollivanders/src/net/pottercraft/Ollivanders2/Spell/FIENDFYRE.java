package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.pottercraft.Ollivanders2.Ollivanders2API;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;
import net.pottercraft.Ollivanders2.StationarySpell.O2StationarySpellType;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Spawns magma cubes, blazes, and ghasts
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class FIENDFYRE extends DarkArts
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public FIENDFYRE ()
   {
      super();
      spellType = O2SpellType.FIENDFYRE;

      // set up usage modifier, has to be done here to get the uses for this specific spell
      setUsesModifier();

      flavorText = new ArrayList<String>() {{
         add("It was not normal fire; Crabbe had used a curse of which Harry had no knowledge: As they turned a corner the flames chased them as though they were alive, sentient, intent upon killing them. ");
         add("Bewitched Flame Curse");
      }};

      text = "Fiendfyre is a hellish curse which summons a mix of magma cubes, blazes, and ghasts.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public FIENDFYRE (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.FIENDFYRE;
      setUsesModifier();

      // world guard flags
      worldGuardFlags.add(DefaultFlag.MOB_SPAWNING);
   }

   /**
    * Check projectile location for HORCRUX spell and kill it, otherwise spawn fire entities
    */
   @Override
   protected void doCheckEffect ()
   {
      // check for stationary spells first to remove HORCRUX spells
      List<StationarySpellObj> stationaries = Ollivanders2API.getStationarySpells().getStationarySpellsAtLocation(location);

      for (StationarySpellObj stationary : stationaries)
      {
         if (stationary.getSpellType().equals(O2StationarySpellType.HORCRUX))
         {
            stationary.kill();
            kill();
            break;
         }
      }

      // spawn magmacubes, blazes, and ghasts
      if (!isKilled() && hasHitTarget())
      {
         location.subtract(vector);
         spawnCreatures();

         kill();
      }
   }

   /**
    * Spawn magmacubes, blazes, and ghasts according to usesModifier
    */
   private void spawnCreatures ()
   {
      World world = location.getWorld();
      for (int x = 1; x < usesModifier; x++)
      {
         world.spawnEntity(location, EntityType.MAGMA_CUBE);
      }
      for (int x = 1; x < usesModifier / 5; x++)
      {
         world.spawnEntity(location, EntityType.BLAZE);
      }
      for (int x = 1; x < usesModifier / 10; x++)
      {
         world.spawnEntity(location, EntityType.GHAST);
      }
   }
}