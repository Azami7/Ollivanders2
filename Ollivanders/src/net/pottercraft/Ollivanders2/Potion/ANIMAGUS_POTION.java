package net.pottercraft.Ollivanders2.Potion;

import org.bukkit.Material;

/**
 * Consumed after successfully casting the Animagus incantation, this will turn a player in to an Animagus.
 *
 * @author Azami7
 */
public final class ANIMAGUS_POTION extends Potion
{
   public ANIMAGUS_POTION ()
   {
      name = "Animagus Potion";
      text = "An Animagus is a wizard who elects to turn into an animal. This potion, if brewed and consumed correctly, " +
            "will transform the drinker in to their animal form. Thereafter, the Animagus can transform without the " +
            "potion, however it will take considerable practice to change forms consistently at will.";
      flavorText.add("\"You know that I can disguise myself most effectively.\" -Peter Pettigrew");
      flavorText.add("\"Normally, I have a very sweet disposition as a dog; in fact, more than once, James suggested I make the change permanent. The tail I could live with...but the fleas, they're murder.\" -Sirius Black");

      ingredients.put(Material.WATER_LILY, 1);
      ingredients.put(Material.CHORUS_FRUIT, 2);
      ingredients.put(Material.EYE_OF_ENDER, 1);
      ingredients.put(Material.SUGAR, 3);
   }
}
