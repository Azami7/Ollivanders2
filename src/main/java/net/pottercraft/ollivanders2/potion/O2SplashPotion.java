package net.pottercraft.ollivanders2.potion;

import org.bukkit.Material;
import org.bukkit.event.entity.PotionSplashEvent;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Adds functionality to O2Potions when they are splash potions.
 *
 * @since 2.2.7
 * @author Azami7
 */
public abstract class O2SplashPotion extends O2Potion {
	/**
	 * Constructor
	 *
	 * @param plugin a callback to the plugin
	 */
	public O2SplashPotion(@NotNull Ollivanders2 plugin) {
		super(plugin);

		potionMaterialType = Material.SPLASH_POTION;
	}

	/**
	 * Do the effect this potion has when thrown.
	 *
	 * @param event the splash potion thrown event
	 */
	public abstract void thrownEffect(@NotNull PotionSplashEvent event);
}