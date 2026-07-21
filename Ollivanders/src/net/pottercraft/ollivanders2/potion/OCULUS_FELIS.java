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
 * Oculus Felis grants the drinker a cat's night vision, applying NIGHT_VISION for 5 minutes.
 *
 * @author Azami7
 */
public class OCULUS_FELIS extends O2Potion {
    /**
     * Constructor.
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

        potionColor = Color.fromRGB(255, 191, 0); // amber
        // longer than vanilla Night Vision II so the effect outlasts a normal potion
        duration = Ollivanders2Common.ticksPerMinute * 5;
        potionSuccessMessage = "The world looks brighter.";

        text = "Oculus Felis is a potion which gives the drinker a cat’s ability to see in near-total darkness.";
    }

    /**
     * Apply the NIGHT_VISION effect to the drinker and send the success message.
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
