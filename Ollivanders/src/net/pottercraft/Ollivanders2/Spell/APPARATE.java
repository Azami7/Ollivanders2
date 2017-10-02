package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.StationarySpell.NULLUM_APPAREBIT;
import net.pottercraft.Ollivanders2.StationarySpell.NULLUM_EVANESCUNT;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;

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

      flavorText.add("A magical means of transportation.");
      flavorText.add("Harry felt Dumbledore's arm twist away from him and re-doubled his grip: the next thing he knew everything went black; he was pressed very hard from all directions; he could not breathe, there were iron bands tightening around his chest; his eyeballs were being forced back into his head; his ear-drums were being pushed deeper into his skull.");
      flavorText.add("\"We just Apparated, didn't we sir?\"\n\"Yes, and quite successfully too, I might add. Most people vomit the first time.\" -Harry Potter and Albus Dumbledore");
      text = "Apparition is a two sided spell. To apparate to a predetermined location, simply say apparate and list your x, y, and z coordinates. "
            + "To apparate to the location of your cursor, within 140 meters, just say the word apparate. "
            + "Your accuracy is determined by the distance traveled and your experience. "
            + "If there are any entities close to you when you apparate, they will be taken with you as well by side-along apparition.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public APPARATE (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      kill();
      location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 0);
      boolean canApparateOut = true;
      for (StationarySpellObj stat : p.getStationary())
      {
         if (stat instanceof NULLUM_EVANESCUNT && stat.isInside(player.getLocation()) && stat.active)
         {
            stat.flair(10);
            canApparateOut = false;
         }
      }
      if (canApparateOut)
      {
         Location from = player.getLocation().clone();
         Location to;
         Location eyeLocation = player.getEyeLocation();
         Material inMat = eyeLocation.getBlock().getType();
         int distance = 0;
         while ((inMat == Material.AIR || inMat == Material.FIRE || inMat == Material.WATER || inMat == Material.STATIONARY_WATER || inMat == Material.LAVA || inMat == Material.STATIONARY_LAVA) && distance < 160)
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
         boolean canApparateIn = true;
         for (StationarySpellObj stat : p.getStationary())
         {
            if (stat instanceof NULLUM_APPAREBIT && stat.isInside(to) && stat.active)
            {
               stat.flair(10);
               canApparateIn = false;
            }
         }
         if (canApparateIn)
         {
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
   }
}