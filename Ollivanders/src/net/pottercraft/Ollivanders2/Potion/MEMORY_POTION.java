package net.pottercraft.Ollivanders2.Potion;

import org.bukkit.Material;

/**
 * Doubles spell experience gained when casting spells.
 *
 * @author Azami7
 * @author cakenggt
 */
public final class MEMORY_POTION extends Potion
{
   public MEMORY_POTION ()
   {
      ingredients.put(Material.SUGAR_CANE, 3);
      ingredients.put(Material.GLOWSTONE_DUST, 2);

      name = "Memory Potion";
      text = "This potion improves the drinker's memory. All spell experience is doubled." + getIngredientsText();
   }
}
