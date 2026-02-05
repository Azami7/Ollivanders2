package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.REGENERATION;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Regeneration Potion - heals the drinker over time through magical regeneration.
 *
 * <p>When consumed, this potion applies the REGENERATION II effect to the player for the default
 * potion duration. The Regeneration effect causes the player to slowly heal over time, restoring
 * lost health points. This is a restorative potion useful for recovering from injuries sustained
 * during combat or other dangerous activities.</p>
 *
 * <p>The potion is crafted with components associated with life and resurrection, including bone,
 * blood, rotten flesh, and salamander fire, reflecting its purpose of restoring life and vitality.</p>
 *
 * @author Azami7
 */
public final class REGENERATION_POTION extends O2Potion {
    /**
     * Constructor for Regeneration Potion.
     *
     * <p>Initializes the potion with its ingredients (Bone, Blood, Rotten Flesh, Salamander Fire,
     * and Standard Potion Ingredients), description text, flavor text, potion color, and the
     * Regeneration effect. Sets up the healing effect that will be applied when the
     * potion is consumed.</p>
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
     * Drink the Regeneration Potion and gain health regeneration.
     *
     * <p>Applies the REGENERATION effect to the player for the default potion duration.
     * This effect causes the player's health to gradually restore over time. The regeneration
     * effect is particularly useful for recovering health after combat, falls, or other
     * damage-dealing events.</p>
     *
     * @param player the player who drank the potion
     */
    public void drink(@NotNull Player player) {
        REGENERATION effect = new REGENERATION(p, duration, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        player.sendMessage(Ollivanders2.chatColor + potionSuccessMessage);
    }
}