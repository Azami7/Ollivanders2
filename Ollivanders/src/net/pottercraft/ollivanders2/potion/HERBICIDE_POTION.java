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
 * Herbicide Potion (or simply Herbicide) is a potion that kills or damages creepers and kills all plants in the splash
 * area of the potion. It will also harm other entities, including players, though not as much as Creepers.
 *
 * @author Azami7
 * @see <a href = "https://harrypotter.fandom.com/wiki/Herbicide_Potion">https://harrypotter.fandom.com/wiki/Herbicide_Potion</a>
 * @since 2.2.7
 */
public final class HERBICIDE_POTION extends O2SplashPotion {
    // the minimum intensity for this effect when thrown on non-plant entities
    private final double minimumEffect = 0.1;

    // the radius of a splash potion's effect in minecraft
    private final int radius = 4;

    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public HERBICIDE_POTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.HERBICIDE_POTION;

        ingredients.put(O2ItemType.LIONFISH_SPINES, 4);
        ingredients.put(O2ItemType.FLOBBERWORM_MUCUS, 2);
        ingredients.put(O2ItemType.HORKLUMP_JUICE, 2);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 2);

        text = "The Herbicide Potion is damages plant-based entities and kills plants in a radius. It is minorly harmful to other living creatures.";

        // potion color
        potionColor = Color.fromRGB(51, 102, 0);

        // set duration of potion effect to 30 seconds
        duration = 30 * Ollivanders2Common.ticksPerSecond;

        minecraftPotionEffect = new PotionEffect(PotionEffectType.INSTANT_DAMAGE, duration, 1);
    }

    /**
     * Drink this potion and do effects
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
    }

    /**
     * Reduce intensity of this potion if the affected entity is not a Creeper.
     *
     * @param event the splash potion thrown event
     */
    @Override
    public void thrownEffect(@NotNull PotionSplashEvent event) {
        Entity thrower = event.getEntity();
        Location eventLocation = event.getPotion().getLocation();

        if (!(thrower instanceof Player))
            return;

        // minimize the effect on non-creeper or creaking
        for (LivingEntity e : event.getAffectedEntities()) {
            if (e.getType() != EntityType.CREEPER || e.getType() != EntityType.CREAKING) {
                event.setIntensity(e, minimumEffect);
            }
        }

        // affect all the plant blocks in the radius of the potion effect
        HERBICIDE herbicide = new net.pottercraft.ollivanders2.stationaryspell.HERBICIDE(p, thrower.getUniqueId(), eventLocation, radius, duration);
        Ollivanders2API.getStationarySpells().addStationarySpell(herbicide);
    }
}