package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.Ollivanders2WorldGuard;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;

import java.util.ArrayList;

/**
 * Apparition code for players who have over 100 uses in apparition.
 *
 * @author lownes
 * @author Azami7
 * @version Ollivanders2
 */
public final class APPARATE extends O2Spell
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public APPARATE()
   {
      super();

      spellType = O2SpellType.APPARATE;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>()
      {{
         add("A magical means of transportation.");
         add("Harry felt Dumbledore's arm twist away from him and re-doubled his grip: the next thing he knew everything went black; he was pressed very hard from all directions; he could not breathe, there were iron bands tightening around his chest; his eyeballs were being forced back into his head; his ear-drums were being pushed deeper into his skull.");
         add("\"We just Apparated, didn't we sir?\"\n\"Yes, and quite successfully too, I might add. Most people vomit the first time.\" -Harry Potter and Albus Dumbledore");
      }};

      text = "Apparition is a two sided spell. To apparate to a predetermined location, simply say apparate and list your x, y, and z coordinates. "
            + "To apparate to the location of your cursor, within 140 meters, just say the word apparate. "
            + "Your accuracy is determined by the distance traveled and your experience. "
            + "If there are any entities close to you when you apparate, they will be taken with you as well by side-along apparition.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public APPARATE (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.APPARATE;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      // world-guard flags
      worldGuardFlags.add(DefaultFlag.EXIT_VIA_TELEPORT);
   }

   /**
    * Teleport the caster to the location, or close to it, depending on skill level
    */
   @Override
   public void checkEffect ()
   {
      if (!checkSpellAllowed())
      {
         kill();
         return;
      }

      // check to see if the player can apparate out of this location
      if (canApparateFrom())
      {
         Location from = player.getLocation().clone();
         Location to;
         Location eyeLocation = player.getEyeLocation();
         Material inMat = eyeLocation.getBlock().getType();
         int distance = 0;
         while ((inMat == Material.AIR || inMat == Material.FIRE || inMat == Material.WATER || inMat == Material.LAVA) && distance < 160)
         {
            eyeLocation = eyeLocation.add(eyeLocation.getDirection());
            distance++;
            inMat = eyeLocation.getBlock().getType();
         }
         to = eyeLocation.subtract(eyeLocation.getDirection()).clone();
         to.setPitch(from.getPitch());
         to.setYaw(from.getYaw());
         double radius = (1 / usesModifier) * from.distance(to) * 0.1;
         double newX = (to.getX() - (radius / 2)) + (radius * Math.random());
         double newZ = (to.getZ() - (radius / 2)) + (radius * Math.random());
         to.setX(newX);
         to.setZ(newZ);

         // check to see if the player can apparate in to the target location
         if (canApparateTo(to))
         {
            player.getWorld().createExplosion(from.getX(), from.getY(), from.getZ(), 2, false, false);

            player.teleport(to);
            for (Entity e : player.getWorld().getEntities())
            {
               if (from.distance(e.getLocation()) <= 2)
               {
                  e.teleport(to);
               }
            }

            // where the player actually ended up
            Location resultingLocation = player.getLocation();
            player.getWorld().createExplosion(resultingLocation.getX(), resultingLocation.getY(), resultingLocation.getZ(), 2, false, false);
         }
      }

      kill();
   }

   /**
    * Check to see if the player can apparate out of this location.
    *
    * @return true if the player can apparate, false otherwise
    */
   private boolean canApparateFrom ()
   {
      // check world guard permissions at location
      if (Ollivanders2.worldGuardEnabled)
      {
         Ollivanders2WorldGuard wg = new Ollivanders2WorldGuard(p);

         if (!wg.checkWGFlag(player, location, DefaultFlag.EXIT_VIA_TELEPORT))
         {
            return false;
         }
      }

      // check for Nullum Evanescunt at location
      if (Ollivanders2API.getStationarySpells().checkLocationForSpell(player.getLocation(), O2StationarySpellType.NULLUM_EVANESCUNT))
      {
         return false;
      }

      return true;
   }

   /**
    * Check to see if the player can apparate to the destination
    *
    * @param destination the target location to apparate to
    * @return true if the player can apparate, false otherwise
    */
   private boolean canApparateTo (Location destination)
   {
      // check world guard permissions at destination
      if (Ollivanders2.worldGuardEnabled)
      {
         Ollivanders2WorldGuard wg = new Ollivanders2WorldGuard(p);

         if (!wg.checkWGFlag(player, destination, DefaultFlag.ENTRY))
         {
            return false;
         }
      }

      // check for Nullum Apparebit at destination
      if (Ollivanders2API.getStationarySpells().checkLocationForSpell(player.getLocation(), O2StationarySpellType.NULLUM_APPAREBIT))
      {
         return false;
      }

      return true;
   }
}