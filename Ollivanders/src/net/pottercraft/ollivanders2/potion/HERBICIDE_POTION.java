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
 * Herbicide Potion — a splash potion that deals full Instant Damage to Creepers and Creakings, reduced damage to
 * everything else, and spawns a HERBICIDE stationary spell that kills plants within its radius.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Herbicide_Potion">Harry Potter Wiki - Herbicide Potion</a>
 */
public final class HERBICIDE_POTION extends O2SplashPotion {
    /**
     * Splash damage intensity applied to non-plant entities: 10% of full.
     */
    private final double minimumEffect = 0.1;

    /**
     * Radius in blocks within which the HERBICIDE stationary spell kills plant blocks.
     */
    private final int radius = 4;

    /**
     * Constructor
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

        potionColor = Color.fromRGB(51, 102, 0);

        duration = 30 * Ollivanders2Common.ticksPerSecond;

        minecraftPotionEffect = new PotionEffect(PotionEffectType.INSTANT_DAMAGE, duration, 1);
    }

    @Override
    public void drink(@NotNull Player player) {
        // no effect when drunk - this is a splash potion
    }

    /**
     * On splash, reduce the potion's damage to {@link #minimumEffect} for everything except Creepers and Creakings,
     * then spawn a HERBICIDE stationary spell at the splash location to kill plants within {@link #radius}.
     *
     * @param event the splash potion event
     */
    @Override
    public void doOnPotionSplashEvent(@NotNull PotionSplashEvent event) {
        Entity thrower = event.getEntity();
        Location eventLocation = event.getPotion().getLocation();

        for (LivingEntity e : event.getAffectedEntities()) {
            if (e.getType() != EntityType.CREEPER && e.getType() != EntityType.CREAKING) {
                event.setIntensity(e, minimumEffect);
            }
        }

        HERBICIDE herbicide = new HERBICIDE(p, thrower.getUniqueId(), eventLocation, radius, duration);
        Ollivanders2API.getStationarySpells().addStationarySpell(herbicide);
    }
}