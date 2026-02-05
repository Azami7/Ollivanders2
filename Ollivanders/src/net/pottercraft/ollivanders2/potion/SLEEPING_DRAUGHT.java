package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.effect.SLEEPING;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Sleeping Draught - causes the drinker to fall into a deep, dreamless sleep.
 *
 * <p>When consumed, this potion applies the SLEEPING effect to the player for approximately
 * 2 minutes, causing them to fall into a deep, enchanted sleep. While asleep, the player cannot
 * perform most actions and is essentially incapacitated for the duration of the effect.</p>
 *
 * <p>The sleep can be immediately countered if the player has the AWAKE effect active, which
 * will prevent the sleeping effect from taking hold. This is the primary counter to the
 * Draught of Living Death, which puts players into permanent sleep.</p>
 *
 * <p>This potion is useful for incapacitating threats temporarily or for puzzle scenarios where
 * sleep is required.</p>
 *
 * @author Azami7
 * @since 2.2.8
 */
public class SLEEPING_DRAUGHT extends O2Potion {
    String awakeEffectMessage = "You yawn, otherwise nothing happens.";

    /**
     * Constructor for Sleeping Draught potion.
     *
     * <p>Initializes the potion with its ingredients (Lavender Sprig, Flobberworm Mucus, Valerian
     * Sprigs, and Standard Potion Ingredients), description text, flavor text, and potion color.
     * Sets up the SLEEPING effect that will be applied when the potion is consumed for
     * approximately 2 minutes of deep sleep.</p>
     *
     * @param plugin a callback to the plugin instance
     */
    public SLEEPING_DRAUGHT(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.SLEEPING_DRAUGHT;

        ingredients.put(O2ItemType.LAVENDER_SPRIG, 4);
        ingredients.put(O2ItemType.FLOBBERWORM_MUCUS, 2);
        ingredients.put(O2ItemType.VALERIAN_SPRIGS, 4);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 6);

        potionColor = Color.fromRGB(75, 0, 130);
        potionSuccessMessage = "You fall into a deep, dreamless, enchanted sleep.";

        text = "A Sleeping Draught causes the drinker to fall almost instantly into a deep, dreamless sleep.";
        flavorText.add("\"I've got it all worked out. I've filled these with a simple Sleeping Draught.\" -Hermione Granger");
        flavorText.add("\"You'll need to drink all of this, Harry. It's a potion for dreamless sleep.\" -Madam Pomfrey");
        flavorText.add("\"Then, without the smallest change of expression, they both keeled over backwards on to the floor.\"");
        flavorText.add("\"If I thought I could help you by putting you into an enchanted sleep and allowing you to postpone the moment when you would have to think about what has happened tonight, I would do it. But I know better.\" -Albus Dumbledore");
    }

    /**
     * Drink the Sleeping Draught and fall into an enchanted sleep.
     *
     * <p>The effect of this potion depends on whether the player has the AWAKE effect:</p>
     * <ul>
     * <li>If the player has the AWAKE effect, the potion is harmless and they return to normal consciousness</li>
     * <li>If the player does not have the AWAKE effect, they are put into a deep, dreamless sleep
     *     (SLEEPING effect) for approximately 2 minutes. While asleep, they cannot perform most actions
     *     and are essentially incapacitated until the effect wears off</li>
     * </ul>
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        if (Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.AWAKE)) {
            player.sendMessage(Ollivanders2.chatColor + "You yawn, otherwise nothing happens.");
        }
        else {
            // put them asleep for ~2 minutes
            SLEEPING effect = new SLEEPING(p, 2400, false, player.getUniqueId());
            Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

            player.sendMessage(Ollivanders2.chatColor + "You fall into a deep, dreamless, enchanted sleep.");
        }
    }

    /**
     * Get the message shown to players with the AWAKE effect when drinking this potion.
     *
     * <p>Returns the special message that is displayed to players who have the AWAKE effect active
     * when they drink the Sleeping Draught. This message indicates that the potion's sleep effect
     * is harmless to them due to their immunity to sleep.</p>
     *
     * @return the message displayed to awakened players ("You yawn, otherwise nothing happens.")
     */
    public String getAwakeEffectMessage() {
        return awakeEffectMessage;
    }
}
