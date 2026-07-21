package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.SATIATION;
import net.pottercraft.ollivanders2.item.O2ItemType;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Satiation Potion — applies the SATIATION effect, which restores food saturation and staves off hunger.
 *
 * @author Azami7
 */
public class SATIATION_POTION extends O2Potion {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin instance
     */
    public SATIATION_POTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.SATIATION_POTION;

        ingredients.put(O2ItemType.CHOPPED_MALLOW_LEAVES, 2);
        ingredients.put(O2ItemType.KNOTGRASS, 1);
        ingredients.put(O2ItemType.HONEYWATER, 2);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 3);

        potionColor = Color.fromRGB(248, 125, 35); // warm orange
        duration = Ollivanders2Common.ticksPerMinute * 5;
        potionSuccessMessage = "You feel completely satisfied and nourished.";

        text = "The satiation potion will satisfy the drinker's hunger and restore their energy.";
    }

    /**
     * Apply the SATIATION effect to the player and send the success message.
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        SATIATION effect = new SATIATION(p, duration, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        player.sendMessage(Ollivanders2.chatColor + potionSuccessMessage);
    }
}