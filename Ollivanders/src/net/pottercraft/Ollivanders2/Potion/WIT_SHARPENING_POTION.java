package net.pottercraft.Ollivanders2.Potion;

import org.bukkit.Material;

/**
 * The Wit-Sharpening Potion is a potion which allows the drinker to think more clearly. Due to this, it acts a a
 * counteragent to the Confundus Charm.
 *
 * @author Azami7
 */
public final class WIT_SHARPENING_POTION extends Potion
{
   //TODO add uses for this potion
   public WIT_SHARPENING_POTION ()
   {
      name = "Wit-Sharpening Potion";
      text = "The Wit-Sharpening Potion is a potion which allows the drinker to think more clearly. Due to this, it acts a a counteragent to the Confundus Charm.";
      flavorText.add("\"Some of you will benefit from today's assignment: Wit-Sharpening Potion. Perhaps you should begin immediately.\" -Severus Snape");

      ingredients.put(Material.BEETROOT, 2);
      ingredients.put(Material.FERMENTED_SPIDER_EYE, 1);
      ingredients.put(Material.GHAST_TEAR, 2);
   }
}
