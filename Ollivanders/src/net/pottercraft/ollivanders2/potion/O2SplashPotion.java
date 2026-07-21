package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.event.entity.PotionSplashEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for splash potions — potions that are thrown and affect all entities in the impact radius rather
 * than being drunk. Sets the material to {@link Material#SPLASH_POTION}; subclasses implement
 * {@link #doOnPotionSplashEvent(PotionSplashEvent)} for their impact effect.
 *
 * @author Azami7
 */
public abstract class O2SplashPotion extends O2Potion {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin instance
     */
    public O2SplashPotion(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionMaterialType = Material.SPLASH_POTION;
    }

    /**
     * Apply this splash potion's effect to the entities hit when it impacts and breaks.
     *
     * @param event the splash potion event, carrying the affected entities and each one's effect intensity
     */
    public abstract void doOnPotionSplashEvent(@NotNull PotionSplashEvent event);
}