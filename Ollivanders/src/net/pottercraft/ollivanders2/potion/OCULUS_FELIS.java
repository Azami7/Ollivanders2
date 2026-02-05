package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.NIGHT_VISION;
import net.pottercraft.ollivanders2.item.O2ItemType;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Oculus Felis - grants feline night vision for seeing in darkness.
 *
 * <p>When consumed, this potion applies the NIGHT_VISION II effect to the player for 5 minutes,
 * granting them the ability to see clearly in near-total darkness. This potion mimics a cat's
 * superior night vision, allowing the player to navigate dark areas as if they were brightly lit.</p>
 *
 * <p>The 5-minute duration is longer than the standard Minecraft Night Vision II potion, providing
 * extended visibility for the drinker. This is particularly useful for exploring dark caves,
 * navigating at night, or other low-light scenarios.</p>
 *
 * @author Azami7
 * @since 2.21
 */
public class OCULUS_FELIS extends O2Potion {
    /**
     * Constructor for Oculus Felis potion.
     *
     * <p>Initializes the potion with its ingredients (Crushed Cat's Eye Opal, Mooncalf Milk,
     * Jobberknoll Feather, Asphodel, and Standard Potion Ingredients), description text, potion
     * color, and the Night Vision II potion effect. Sets up the 5-minute duration for the night
     * vision effect.</p>
     *
     * @param plugin a callback to the plugin instance
     */
    public OCULUS_FELIS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.OCULUS_FELIS;

        ingredients.put(O2ItemType.CRUSHED_CATS_EYE_OPAL, 2);
        ingredients.put(O2ItemType.MOONCALF_MILK, 1);
        ingredients.put(O2ItemType.JOBBERKNOLL_FEATHER, 1);
        ingredients.put(O2ItemType.ASPHODEL, 1);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 2);

        // potion color
        potionColor = Color.fromRGB(255, 191, 0); // amber
        // potion duration - last 5 minutes, which is longer than night vision II lasts
        duration = Ollivanders2Common.ticksPerMinute * 5;
        potionSuccessMessage = "The world looks brighter.";

        text = "Oculus Felis is a potion which gives the drinker a cat’s ability to see in near-total darkness.";
    }

    /**
     * Drink Oculus Felis and gain night vision.
     *
     * <p>Applies the NIGHT_VISION II effect to the player for 5 minutes. This effect allows the
     * player to see clearly in darkness and low-light conditions, as if the world were lit by
     * daylight. The enhanced vision mimics a feline's ability to see in near-total darkness.</p>
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        NIGHT_VISION effect = new NIGHT_VISION(p, duration, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        player.sendMessage(Ollivanders2.chatColor + potionSuccessMessage);
    }
}
