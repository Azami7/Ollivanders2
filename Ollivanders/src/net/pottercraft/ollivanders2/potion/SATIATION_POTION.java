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
 * Satiation potion adds the Saturation potion effect.
 *
 * @author Azami7
 * @since 2.21
 */
public class SATIATION_POTION extends O2Potion {
    public SATIATION_POTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        // potion config
        potionType = O2PotionType.SATIATION_POTION;
        potionColor = Color.fromRGB(248, 125, 35); // warm orange
        duration = Ollivanders2Common.ticksPerMinute * 5;

        // ingredients
        ingredients.put(O2ItemType.CHOPPED_MALLOW_LEAVES, 2);
        ingredients.put(O2ItemType.KNOTGRASS, 1);
        ingredients.put(O2ItemType.HONEYWATER, 2);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 3);

        // spellbook text
        text = "The satiation potion will satisfy the drinker's hunger and restore their energy.";

        // Saturation I
        minecraftPotionEffect = new PotionEffect(PotionEffectType.SATURATION, duration, 0);
    }

    /**
     * Drink this potion and do effects
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        player.sendMessage(Ollivanders2.chatColor + "You feel completely satisfied and nourished.");
    }
}