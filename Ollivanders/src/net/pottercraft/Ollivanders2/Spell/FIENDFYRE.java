package net.pottercraft.Ollivanders2.Spell;

import java.util.List;

import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpells;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.Effect;

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
   private double lifeTime;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public FIENDFYRE ()
   {
      super();

      flavorText.add("It was not normal fire; Crabbe had used a curse of which Harry had no knowledge: As they turned a corner the flames chased them as though they were alive, sentient, intent upon killing them. ");
      flavorText.add("Bewitched Flame Curse");
      text = "Fiendfyre is a hellish curse which summons a mix of magma cubes, blazes, and ghasts.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public FIENDFYRE (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      lifeTime = usesModifier * 4;
   }

   @Override
   public void checkEffect ()
   {
      move();
      List<StationarySpellObj> stationaries = p.checkForStationary(location);
      for (StationarySpellObj stationary : stationaries)
      {
         if (stationary.name.equals(StationarySpells.HORCRUX))
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