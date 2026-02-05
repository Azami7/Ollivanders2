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
 * Draught of Living Death - an extremely powerful sleeping potion that induces permanent magical sleep.
 *
 * <p>This is one of the most dangerous potions in existence. When consumed, it puts the drinker
 * into a permanent magical sleep (SLEEPING effect). The sleep can only be broken by a player
 * who has the AWAKE effect active, which will render the potion's effects harmless.</p>
 *
 * <p>This potion requires a complex recipe with rare ingredients including Powdered Asphodel Root
 * and Infusion of Wormwood, making it a high-level potion to brew. It should be handled with
 * extreme caution.</p>
 *
 * @author Azami7
 * @since 2.2.8
 */
public class DRAUGHT_OF_LIVING_DEATH extends O2Potion {
    String awakeEffectMessage = "You yawn, close your eyes for a moment, then feel fine.";

    /**
     * Constructor for Draught of Living Death potion.
     *
     * <p>Initializes the potion with its complex recipe of rare ingredients (Powdered Asphodel Root,
     * Infusion of Wormwood, Valerian Root, Sopophorus Bean Juice, Sloth Brain, and Standard Potion
     * Ingredients), description text, flavor text, and potion color. Sets up the permanent SLEEPING
     * effect that will be applied when the potion is consumed.</p>
     *
     * @param plugin a callback to the plugin instance
     */
    public DRAUGHT_OF_LIVING_DEATH(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.DRAUGHT_OF_LIVING_DEATH;

        ingredients.put(O2ItemType.POWDERED_ASHPODEL_ROOT, 1);
        ingredients.put(O2ItemType.INFUSION_OF_WORMWOOD, 1);
        ingredients.put(O2ItemType.VALERIAN_ROOT, 1);
        ingredients.put(O2ItemType.SOPOPHORUS_BEAN_JUICE, 1);
        ingredients.put(O2ItemType.SLOTH_BRAIN, 1);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 2);

        potionColor = Color.fromRGB(200, 162, 200);
        potionSuccessMessage = "You fall into a powerful magic sleep.";

        text = "Puts the drinker in a permanent magical sleep.";
        flavorText.add("The Draught of Living Death brings upon its drinker a very powerful sleep that can last indefinitely. This is an extremely dangerous potion. Execute with maximum caution.");
        flavorText.add("By mixing an infusion of wormwood with powdered root of asphodel you can make a sleeping potion so powerful it is known as the Draught of Living Death.");
        flavorText.add("\"Mr. Potter. Our new celebrity. Tell me, what would I get if I added powdered root of asphodel to an infusion of wormwood?\" -Severus Snape");
    }

    /**
     * Drink the Draught of Living Death and enter permanent magical sleep.
     *
     * <p>The effect of this potion depends on whether the player has the AWAKE effect active:</p>
     * <ul>
     * <li>If the player has the AWAKE effect, the potion is harmless and the player returns to
     *     normal consciousness with a brief yawn</li>
     * <li>If the player does not have the AWAKE effect, they are put into permanent magical sleep
     *     (SLEEPING effect). The only way to break this sleep is for another player with the AWAKE
     *     effect to use a counter-potion or spell</li>
     * </ul>
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        if (Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.AWAKE)) {
            player.sendMessage(Ollivanders2.chatColor + awakeEffectMessage);
        }
        else {
            SLEEPING effect = new SLEEPING(p, 5, true, player.getUniqueId());
            effect.setPermanent(true);
            Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

            player.sendMessage(Ollivanders2.chatColor + "You fall into a powerful magic sleep.");
        }
    }

    /**
     * Get the message shown to players with the AWAKE effect when drinking this potion.
     *
     * <p>Returns the special message that is displayed to players who have the AWAKE effect active
     * when they drink the Draught of Living Death. This message indicates that the potion's sleep
     * effect is harmless to them due to their immunity to sleep.</p>
     *
     * @return the message displayed to awakened players ("You yawn, close your eyes for a moment, then feel fine.")
     */
    public String getAwakeEffectMessage() {
        return awakeEffectMessage;
    }
}
