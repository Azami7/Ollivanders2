package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.REGENERATION;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Regeneration Potion — applies the REGENERATION effect, healing the drinker over time.
 *
 * @author Azami7
 */
public final class REGENERATION_POTION extends O2Potion {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin instance
     */
    public REGENERATION_POTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.REGENERATION_POTION;

        ingredients.put(O2ItemType.BONE, 1);
        ingredients.put(O2ItemType.BLOOD, 1);
        ingredients.put(O2ItemType.ROTTEN_FLESH, 1);
        ingredients.put(O2ItemType.SALAMANDER_FIRE, 1);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 4);

        potionColor = Color.WHITE;
        potionSuccessMessage = "You feel recovered.";

        text = "This potion will heal a player.";
        flavorText.add("\"Bone of the father, unknowingly given, you will renew your son! Flesh of the servant, willingly sacrificed, you will revive your master. Blood of the enemy, forcibly taken, you will resurrect your foe.\" -Peter Pettigrew");
    }

    /**
     * Apply the REGENERATION effect to the drinker and send the success message.
     *
     * @param player the player who drank the potion
     */
    public void drink(@NotNull Player player) {
        REGENERATION effect = new REGENERATION(p, duration, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        player.sendMessage(Ollivanders2.chatColor + potionSuccessMessage);
    }
}