package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.Color;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Wiggenweld Potion - a powerful healing potion that awakens sleeping players.
 *
 * <p>This splash potion is a critical tool for reversing sleep effects. When thrown, it removes
 * the SLEEPING effect from all affected players, instantly awakening them from magically-induced
 * sleep such as that caused by the Sleeping Draught or Draught of Living Death. The potion
 * provides both healing through an Instant Health effect and awakening functionality.</p>
 *
 * <p>As a splash potion, the Wiggenweld Potion is thrown rather than consumed directly. The healing
 * effect affects all entities in the splash radius, while the awakening effect specifically targets
 * and wakes sleeping players. This makes it an essential potion for countering sleep-based attacks
 * and rescuing incapacitated allies.</p>
 *
 * <p>The complex recipe with many rare ingredients reflects the potion's powerful dual-purpose
 * effects.</p>
 *
 * @author Azami7
 * @since 2.2.8
 */
public class WIGGENWELD_POTION extends O2SplashPotion {
    /**
     * Constructor for Wiggenweld Potion.
     *
     * <p>Initializes the splash potion with its complex recipe of many rare ingredients (Horklump
     * Juice, Flobberworm Mucus, Chizpurfle Fangs, Billywig Sting Slime, Mint Sprig, Boom Berry
     * Juice, Mandrake Leaf, Honeywater, Sloth Brain Mucus, Moondew Drop, Salamander Blood,
     * Lionfish Spines, Unicorn Horn, Wolfsbane, and Standard Potion Ingredients), description text,
     * flavor text, potion color, and the Instant Health effect. Sets up the dual awakening and
     * healing functionality of this powerful potion.</p>
     *
     * @param plugin a callback to the plugin instance
     */
    public WIGGENWELD_POTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.WIGGENWELD_POTION;

        ingredients.put(O2ItemType.HORKLUMP_JUICE, 1);
        ingredients.put(O2ItemType.FLOBBERWORM_MUCUS, 2);
        ingredients.put(O2ItemType.CHIZPURFLE_FANGS, 1);
        ingredients.put(O2ItemType.BILLYWIG_STING_SLIME, 2);
        ingredients.put(O2ItemType.MINT_SPRIG, 1);
        ingredients.put(O2ItemType.BOOM_BERRY_JUICE, 1);
        ingredients.put(O2ItemType.MANDRAKE_LEAF, 2);
        ingredients.put(O2ItemType.HONEYWATER, 1);
        ingredients.put(O2ItemType.SLOTH_BRAIN_MUCUS, 1);
        ingredients.put(O2ItemType.MOONDEW_DROP, 3);
        ingredients.put(O2ItemType.SALAMANDER_BLOOD, 1);
        ingredients.put(O2ItemType.LIONFISH_SPINES, 10);
        ingredients.put(O2ItemType.UNICORN_HORN, 1);
        ingredients.put(O2ItemType.WOLFSBANE, 2);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 1);

        text = "The Wiggenweld Potion is a healing potion which can also be used to wake the drinker from magically-induced sleep.";
        flavorText.add("\"Today you will learn to brew the Wiggenweld Potion. It is a powerful healing potion that can be used to heal injuries, or reverse the effects of a Sleeping Draught.\" -Severus Snape");

        potionColor = Color.fromRGB(0, 206, 209);
        minecraftPotionEffect = new PotionEffect(PotionEffectType.INSTANT_HEALTH, duration, 1);
    }

    /**
     * Drink the Wiggenweld Potion - no effect when consumed directly.
     *
     * <p>This is a splash potion intended to be thrown, not consumed. Drinking it has no special
     * effect. The potion's primary functionality is triggered through the splash event when thrown,
     * which removes the SLEEPING effect from affected players and applies the healing effect.</p>
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        // No effect when drunk - this is a splash potion
    }

    /**
     * Handle the splash potion event and awaken sleeping players.
     *
     * <p>When the Wiggenweld Potion splashes, it removes the SLEEPING effect from all affected
     * players in the splash radius. This instantly wakes any players who are magically asleep due
     * to the Sleeping Draught, Draught of Living Death, or other sleep-inducing effects. The
     * potion's healing effect (Instant Health II) is automatically applied through the standard
     * Minecraft potion system.</p>
     *
     * @param event the splash potion event containing affected entities and splash location
     */
    @Override
    public void doOnPotionSplashEvent(@NotNull PotionSplashEvent event) {
        for (LivingEntity e : event.getAffectedEntities()) {
            if (e instanceof Player) {
                Player player = (Player) e;
                Ollivanders2API.getPlayers().playerEffects.removeEffect(player.getUniqueId(), O2EffectType.SLEEPING);
            }
        }
    }
}
