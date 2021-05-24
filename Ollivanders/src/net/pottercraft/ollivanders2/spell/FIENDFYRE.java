package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.Ollivanders2Common;
import net.pottercraft.ollivanders2.stationaryspell.StationarySpellObj;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Spawns magma cubes, blazes, and ghasts
 *
 * @author lownes
 * @author Azami7
 * @version Ollivanders2
 */
public final class FIENDFYRE extends O2Spell
{
   private final int minCreatures = 1;
   private final int maxCreatures = 10;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public FIENDFYRE()
   {
      super();
      spellType = O2SpellType.FIENDFYRE;
      branch = O2MagicBranch.DARK_ARTS;

      flavorText = new ArrayList<String>()
      {{
         add("It was not normal fire; Crabbe had used a curse of which Harry had no knowledge: As they turned a corner the flames chased them as though they were alive, sentient, intent upon killing them. ");
         add("Bewitched Flame Curse");
      }};

      text = "Fiendfyre is a hellish curse which summons a mix of magma cubes, blazes, and ghasts.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public FIENDFYRE(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.FIENDFYRE;
      branch = O2MagicBranch.DARK_ARTS;

      initSpell();

      // world guard flags
      if (Ollivanders2.worldGuardEnabled)
         worldGuardFlags.add(Flags.MOB_SPAWNING);
   }

   /**
    * Check projectile location for HORCRUX spell and kill it, otherwise spawn fire entities
    */
   @Override
   protected void doCheckEffect ()
   {
      // check for stationary spells first to remove HORCRUX spells
      List<StationarySpellObj> stationaries = Ollivanders2API.getStationarySpells(p).getStationarySpellsAtLocation(location);

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
   private void spawnCreatures()
   {
      World world = location.getWorld();
      if (world == null)
      {
         common.printDebugMessage("FIENDFYE.spawnCreatues: world is null", null, null, true);
         kill();
         return;
      }

      int numCreatures = (int) usesModifier / 10;
      if (numCreatures < minCreatures)
      {
         numCreatures = minCreatures;
      }
      else if (numCreatures > maxCreatures)
      {
         numCreatures = maxCreatures;
      }

      common.printDebugMessage("spawning " + numCreatures + " fiendfyre creatures...", null, null, false);

      if (usesModifier > 100)
      {
         int numGhasts = Math.abs(Ollivanders2Common.random.nextInt()) % numCreatures;

         for (int x = 0; x < numGhasts; x++)
         {
            world.spawnEntity(location, EntityType.GHAST);
            numCreatures--;
         }
      }

      if (usesModifier > 50 && numCreatures > 0)
      {
         int numBlazes = Math.abs(Ollivanders2Common.random.nextInt()) % numCreatures;

         for (int x = 0; x < numBlazes; x++)
         {
            world.spawnEntity(location, EntityType.BLAZE);
            numCreatures--;
         }
      }

      if (numCreatures > 0)
      {
         for (int x = 0; x < numCreatures; x++)
         {
            world.spawnEntity(location, EntityType.MAGMA_CUBE);
         }
      }
   }
}