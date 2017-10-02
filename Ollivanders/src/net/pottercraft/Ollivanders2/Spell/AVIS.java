package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Bat;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Conjures a flock of birds from the tip of the wand.
 *
 * @author Azami7
 */
public final class AVIS extends Charms
{
   private int birdCount = 0;
   private int maxBirds = 2;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public AVIS ()
   {
      super();

      flavorText.add("The Bird-Conjuring Charm");
      flavorText.add("Most of the class had already left, although several twittering yellow birds were still zooming around the room, all of Hermione's creation; nobody else had succeeded in conjuring so much as a feather from thin air.");
      flavorText.add("\"Oh, hello, Harry ... I was just practicing.\" -Hermione Granger conjuring small golden birds just before sending them to attack Ron");
      text = "Causes one or more birds to fly out of the tip of your wand.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public AVIS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      if (usesModifier > 100)
         maxBirds += 10;
      else
         maxBirds += (int)usesModifier / 10;
   }

   public void checkEffect()
   {
      if (birdCount < maxBirds)
      {
         move();

         if (Ollivanders2.mcVersionCheck())
         {
            Parrot bird = (Parrot) location.getWorld().spawnEntity(location, EntityType.PARROT);

            int rand = Math.abs(Ollivanders2.random.nextInt() % 5);
            Parrot.Variant variant;
            if (rand == 0)
               variant = Parrot.Variant.CYAN;
            else if (rand == 1)
               variant = Parrot.Variant.GRAY;
            else if (rand == 2)
               variant = Parrot.Variant.BLUE;
            else if (rand == 3)
               variant = Parrot.Variant.GREEN;
            else
               variant = Parrot.Variant.RED;
            bird.setVariant(variant);
         }
         else
         {
            Bat bird = (Bat) location.getWorld().spawnEntity(location, EntityType.BAT);
         }

         birdCount++;
      }
      else
      {
         kill();
      }
   }
}