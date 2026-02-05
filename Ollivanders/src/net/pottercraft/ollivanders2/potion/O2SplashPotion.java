package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.event.entity.PotionSplashEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for splash potion implementations.
 *
 * <p>O2SplashPotion extends O2Potion to provide functionality specific to splash potions that are
 * thrown rather than consumed directly. Splash potions affect a radius of entities when they impact
 * and break, unlike normal potions which are consumed by a player drinking them.</p>
 *
 * <p>Subclasses must implement {@link #doOnPotionSplashEvent(PotionSplashEvent)} to define the
 * custom effects that occur when the splash potion explodes. The potion material type is
 * automatically set to {@link Material#SPLASH_POTION}.</p>
 *
 * <p>Examples of splash potions include HERBICIDE_POTION which kills plants in a radius and
 * damages plant-based entities when thrown.</p>
 *
 * @author Azami7
 * @since 2.2.7
 */
public abstract class O2SplashPotion extends O2Potion {
    /**
     * Constructor for O2SplashPotion.
     *
     * <p>Initializes the splash potion by calling the parent O2Potion constructor and setting
     * the potion material type to {@link Material#SPLASH_POTION}. Subclasses should call this
     * constructor via {@code super(plugin)} and then set up their specific recipe, ingredients,
     * effects, and other potion properties.</p>
     *
     * @param plugin a callback to the plugin instance
     */
    public O2SplashPotion(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionMaterialType = Material.SPLASH_POTION;
    }

    /**
     * Handle the effects that occur when this splash potion is thrown and impacts.
     *
     * <p>This abstract method is called when the splash potion breaks and its effects are applied.
     * Subclasses must implement this method to define their custom splash effects, such as:</p>
     * <ul>
     * <li>Modifying the intensity of the potion effect for different entity types</li>
     * <li>Creating stationary spells or effects at the splash location</li>
     * <li>Applying custom transformations or interactions to affected entities</li>
     * </ul>
     *
     * <p>The PotionSplashEvent contains information about the potion, thrower, splash location,
     * affected entities, and the original potion effect intensity that can be modified.</p>
     *
     * @param event the splash potion event containing splash location, affected entities, and effect intensity
     */
    public abstract void doOnPotionSplashEvent(@NotNull PotionSplashEvent event);
}