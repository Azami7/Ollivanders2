package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.StationarySpellObj;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Spawns magma cubes, blazes, and ghasts
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class FIENDFYRE extends DarkArts
{
   private double lifeTime;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public FIENDFYRE ()
   {
      super();

      spellType = O2SpellType.FIENDFYRE;

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
      lifeTime = usesModifier * 4;
      setUsesModifier();
   }

   @Override
   public void checkEffect ()
   {
      move();
      List<StationarySpellObj> stationaries = Ollivanders2API.getStationarySpells().getStationarySpellsAtLocation(location);
      for (StationarySpellObj stationary : stationaries)
      {
         if (stationary.getSpellType().equals(O2StationarySpellType.HORCRUX))
         {
            stationary.kill();
         }
      }
      if (lifeTicks > lifeTime)
      {
         spawnCreatures();
         kill();
      }
      if (location.getBlock().getType() != Material.AIR)
      {
         location.subtract(vector);
         spawnCreatures();
         kill();
      }
   }

   /**
    * This spawns the magmacubes, blazes, and ghasts according to usesModifier
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