package net.pottercraft.Ollivanders2.Potion;

import org.bukkit.Material;

/**
 * Relieve the symptoms of Lycanthropy
 *
 * @author Azami7
 * @author cakenggt
 */
public final class WOLFSBANE_POTION extends Potion
{
   public WOLFSBANE_POTION ()
   {
      ingredients.put(Material.SPIDER_EYE, 2);
      ingredients.put(Material.ROTTEN_FLESH, 3);
      ingredients.put(Material.POISONOUS_POTATO, 1);

      name = "Wolfsbane Potion";
      text = "This potion will relieve, though not cure, the symotoms of Lycanthropy. It is a complex potion and requires"
            + "the most advanced potion-making skills." + getIngredientsText();

      flavorText.add("\"There is no known cure, although recent developments in potion-making have to a great extent alleviated the worst symptoms.\" —Newton Scamander");
   }
}
