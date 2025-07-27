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
 * Hunger potion adds the Hunger potion effect.
 *
 * @author Azami7
 * @since 2.21
 */
public class HUNGER_POTION extends O2Potion {
    public HUNGER_POTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        // potion config
        potionType = O2PotionType.HUNGER_POTION;
        potionColor = Color.fromRGB(88, 118, 83); // dark green
        duration = Ollivanders2Common.ticksPerMinute * 3;

        // ingredients
        ingredients.put(O2ItemType.ROTTEN_FLESH, 2);
        ingredients.put(O2ItemType.GROUND_SCARAB_BEETLE, 1);
        ingredients.put(O2ItemType.CRUSHED_FIRE_SEEDS, 1);
        ingredients.put(O2ItemType.CRUSHED_GURDYROOT, 1);
        ingredients.put(O2ItemType.INFUSION_OF_WORMWOOD, 1);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 3);

        // spellbook text
        text = "The hunger potion will cause the drinker to feel ravenous.";

        // Hunger I
        minecraftPotionEffect = new PotionEffect(PotionEffectType.HUNGER, duration, 0);
    }

    /**
     * Drink this potion and do effects
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        player.sendMessage(Ollivanders2.chatColor + "You feel ravenously hungry.");
    }
}