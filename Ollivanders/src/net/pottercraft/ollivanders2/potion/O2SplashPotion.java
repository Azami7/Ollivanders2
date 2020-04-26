package net.pottercraft.ollivanders2.potion;

import org.bukkit.event.entity.PotionSplashEvent;

/**
 * Adds functionality to O2Potions when they are splash potions.
 *
 * @since 2.2.7
 * @author Azami7
 */
public interface O2SplashPotion
{
   /**
    * Do the effect this potion has when thrown.
    *
    * @param event the splash potion thrown event
    */
   public void thrownEffect (PotionSplashEvent event);
}