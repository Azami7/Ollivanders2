package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.item.O2ItemType;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Oculus Felis gives the drinker night vision.
 *
 * @author Azami7
 * @since 2.21
 */
public class OCULUS_FELIS extends O2Potion {
    /**
     * Constructor.
     *
     * @param plugin the callback to the MC plugin
     */
    public OCULUS_FELIS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.OCULUS_FELIS;

        ingredients.put(O2ItemType.CRUSHED_CATS_EYE_OPAL, 2);
        ingredients.put(O2ItemType.MOONCALF_MILK, 1);
        ingredients.put(O2ItemType.JOBBERKNOLL_FEATHER, 1);
        ingredients.put(O2ItemType.ASPHODEL, 1);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 2);

        text = "Oculus Felis is a potion which gives the drinker a cat’s ability to see in near-total darkness.";

        minecraftPotionEffect = new PotionEffect(PotionEffectType.NIGHT_VISION, duration, 1);
        potionColor = Color.fromRGB(255, 191, 0); // amber

        // last 5 minutes, which is longer than night vision II lasts
        duration = Ollivanders2Common.ticksPerMinute * 5;
    }

    /**
     * Drink this potion and do effects
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        player.sendMessage(Ollivanders2.chatColor + "The world looks brighter.");
    }
}
