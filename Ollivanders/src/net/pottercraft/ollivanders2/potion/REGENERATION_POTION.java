package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * The regeneration potion restores a player's experience after death if deathExpLoss is enabled.
 *
 * @author Azami7
 */
public final class REGENERATION_POTION extends O2Potion {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public REGENERATION_POTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.REGENERATION_POTION;

        ingredients.put(O2ItemType.BONE, 1);
        ingredients.put(O2ItemType.BLOOD, 1);
        ingredients.put(O2ItemType.ROTTEN_FLESH, 1);
        ingredients.put(O2ItemType.SALAMANDER_FIRE, 1);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 4);

        text = "This potion will heal a player.";
        flavorText.add("\"Bone of the father, unknowingly given, you will renew your son! Flesh of the servant, willingly sacrificed, you will revive your master. Blood of the enemy, forcibly taken, you will resurrect your foe.\" -Peter Pettigrew");

        minecraftPotionEffect = new PotionEffect(PotionEffectType.REGENERATION, duration, 1);
        potionColor = Color.WHITE;
    }

    /**
     * Drink this potion and do effects
     *
     * @param player the player who drank the potion
     */
    public void drink(@NotNull Player player) {
        player.sendMessage(Ollivanders2.chatColor + "You feel recovered.");
    }
}