package net.pottercraft.Ollivanders2.Potion;

import org.bukkit.Material;

/**
 * All spells cast are twice as powerful.
 *
 * @author Azami7
 * @author cakenggt
 */
public final class BARUFFIOS_BRAIN_ELIXIR extends Potion
{
   public BARUFFIOS_BRAIN_ELIXIR ()
   {
      name = "Baruffio's Brain Elixir";
      text = "Baruffio's Brain Elixir is a potion that increases the taker's brain power. All spells cast are twice as powerful.";
      flavorText.add("\"I've performed tests on the potion sample you collected. My best guess is that it was supposed to be Baruffio's Brain Elixir. Now, that's a potion which doesn't work at the best of times, but whoever brewed this was seriously incompetent! Forget boosting one's brain; this concoction would most likely melt it!\" —Gethsemane Prickle");

      ingredients.put(Material.REDSTONE, 5);
      ingredients.put(Material.GOLD_NUGGET, 1);
   }
}
