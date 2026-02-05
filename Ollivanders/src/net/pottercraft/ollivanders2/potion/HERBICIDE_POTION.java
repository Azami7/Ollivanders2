package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.HERBICIDE;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Herbicide Potion - a splash potion that damages plant-based entities and kills plants.
 *
 * <p>This is a splash potion designed to be thrown rather than consumed directly. When thrown,
 * it deals full damage to plant-based entities (Creepers and Creakings) and creates a plant-killing
 * effect in a 4-block radius splash area. The potion will also harm other living creatures
 * (including players), but at reduced intensity (10% of normal damage).</p>
 *
 * <p>Effects when splashed:</p>
 * <ul>
 * <li>Full Instant Damage II effect for Creepers and Creakings</li>
 * <li>Reduced intensity (10%) for all other entities including players</li>
 * <li>HERBICIDE stationary spell effect that kills plants in the 4-block radius</li>
 * </ul>
 *
 * @author Azami7
 * @see <a href = "https://harrypotter.fandom.com/wiki/Herbicide_Potion">https://harrypotter.fandom.com/wiki/Herbicide_Potion</a>
 * @since 2.2.7
 */
public final class HERBICIDE_POTION extends O2SplashPotion {
    /**
     * The minimum damage intensity for non-plant entities.
     * Reduces damage to 10% for players and non-plant mobs when splashed.
     */
    private final double minimumEffect = 0.1;

    /**
     * The radius of the splash potion's effect in blocks.
     * The HERBICIDE stationary spell affects all plant blocks within this radius.
     */
    private final int radius = 4;

    /**
     * Constructor for Herbicide Potion.
     *
     * <p>Initializes the splash potion with its ingredients (Lionfish Spines, Flobberworm Mucus,
     * Horklump Juice, and Standard Potion Ingredients), description text, potion color, and the
     * Instant Damage effect. Sets up the 30-second duration for the plant-killing effect.</p>
     *
     * @param plugin a callback to the plugin instance
     */
    public HERBICIDE_POTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.HERBICIDE_POTION;

        ingredients.put(O2ItemType.LIONFISH_SPINES, 4);
        ingredients.put(O2ItemType.FLOBBERWORM_MUCUS, 2);
        ingredients.put(O2ItemType.HORKLUMP_JUICE, 2);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 2);

        text = "The Herbicide Potion damages plant-based entities and kills plants in a radius. It is minorly harmful to other living creatures.";

        // potion color
        potionColor = Color.fromRGB(51, 102, 0);

        // set duration of potion effect to 30 seconds
        duration = 30 * Ollivanders2Common.ticksPerSecond;

        minecraftPotionEffect = new PotionEffect(PotionEffectType.INSTANT_DAMAGE, duration, 1);
    }

    /**
     * Drink the Herbicide Potion - no effect when consumed directly.
     *
     * <p>This is a splash potion intended to be thrown, not consumed. Drinking it has no special
     * effect. The potion's primary functionality is triggered through the splash event when thrown,
     * which damages plant-based entities and kills plants in the affected area.</p>
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        // No effect when drunk - this is a splash potion
    }

    /**
     * Handle the splash potion event and apply herbicide effects.
     *
     * <p>When the potion splashes:</p>
     * <ul>
     * <li>Creepers and Creakings receive full damage intensity from the potion effect</li>
     * <li>All other entities (players, mobs, etc.) receive reduced damage (10% intensity)</li>
     * <li>A HERBICIDE stationary spell is created at the splash location to kill plant blocks
     *     in the affected radius (4 blocks) for the duration of the potion effect (30 seconds)</li>
     * </ul>
     *
     * @param event the splash potion thrown event containing affected entities and location
     */
    @Override
    public void doOnPotionSplashEvent(@NotNull PotionSplashEvent event) {
        Entity thrower = event.getEntity();
        Location eventLocation = event.getPotion().getLocation();

        // minimize the effect on non-creeper or creaking
        for (LivingEntity e : event.getAffectedEntities()) {
            if (e.getType() != EntityType.CREEPER && e.getType() != EntityType.CREAKING) {
                event.setIntensity(e, minimumEffect);
            }
        }

        // affect all the plant blocks in the radius of the potion effect
        HERBICIDE herbicide = new HERBICIDE(p, thrower.getUniqueId(), eventLocation, radius, duration);
        Ollivanders2API.getStationarySpells().addStationarySpell(herbicide);
    }
}