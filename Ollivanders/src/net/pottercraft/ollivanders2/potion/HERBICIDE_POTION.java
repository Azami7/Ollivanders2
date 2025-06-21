package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Herbicide Potion (or simply Herbicide) is a potion that kills or damages creepers. It will also harm other
 * entities, including players, though not as much as Creepers.
 *
 * @author Azami7
 * @since 2.2.7
 */
public final class HERBICIDE_POTION extends O2SplashPotion {
    private final double minimumEffect = 0.1;

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

        text = "The Herbicide Potion is damages or kills Creepers. It is also harmful to other living creatures.";

        // set duration of potion effect to 30 seconds
        duration = 600;
        minecraftPotionEffect = new PotionEffect(PotionEffectType.INSTANT_DAMAGE, duration, 1);
        potionColor = Color.fromRGB(51, 102, 0);
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
        for (LivingEntity e : event.getAffectedEntities()) {
            if (e.getType() != EntityType.CREEPER) {
                event.setIntensity(e, minimumEffect);
            }
        }
    }
}