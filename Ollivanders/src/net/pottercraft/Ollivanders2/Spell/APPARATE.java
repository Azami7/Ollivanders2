package net.pottercraft.Ollivanders2.Spell;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.pottercraft.Ollivanders2.Ollivanders2API;
import net.pottercraft.Ollivanders2.Ollivanders2WorldGuard;
import net.pottercraft.Ollivanders2.StationarySpell.O2StationarySpellType;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.ArrayList;

/**
 * Apparition code for players who have over 100 uses in apparition.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class APPARATE extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public APPARATE ()
   {
      super();

      spellType = O2SpellType.APPARATE;

      flavorText = new ArrayList<String>() {{
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

      // set up usage modifier, has to be done here to get the uses for this specific spell
      setUsesModifier();
   }

   /**
    * Teleport the caster to the location, or close to it, depending on skill level
    */
   @Override
   public void checkEffect ()
   {
      // check to see if the player can apparate out of this location
      if (canApparateOut())
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
         Double radius = (1 / usesModifier) * from.distance(to) * 0.1;
         Double newX = to.getX() - (radius / 2) + (radius * Math.random());
         Double newZ = to.getZ() - (radius / 2) + (radius * Math.random());
         to.setX(newX);
         to.setZ(newZ);

         // check to see if the player can apparate in to the target location
         if (canApparateTo(to))
         {
            location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 0);

            player.getWorld().createExplosion(player.getLocation(), 0);
            player.teleport(to);
            for (Entity e : player.getWorld().getEntities())
            {
               if (from.distance(e.getLocation()) <= 2)
               {
                  e.teleport(to);
               }
            }
            player.getWorld().createExplosion(player.getLocation(), 0);
         }
      }

      kill();
   }

   /**
    * Check to see if the player can apparate out of this location.
    *
    * @return true if the player can apparate, false otherwise
    */
   private boolean canApparateOut ()
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