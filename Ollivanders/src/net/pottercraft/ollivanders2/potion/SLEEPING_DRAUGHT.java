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
 * Sleeping Draught — applies the SLEEPING effect, unless the drinker has the AWAKE effect (which no-ops it).
 *
 * @author Azami7
 */
public class SLEEPING_DRAUGHT extends O2Potion {
    String awakeEffectMessage = "You yawn, otherwise nothing happens.";

    /**
     * Constructor
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
     * Apply the SLEEPING effect for ~2 minutes, unless the player has AWAKE, in which case nothing happens.
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

            player.sendMessage(Ollivanders2.chatColor + potionSuccessMessage);
        }
    }

    /**
     * Get the message shown to players with the AWAKE effect when they drink this potion.
     *
     * @return the awake-effect message
     */
    public String getAwakeEffectMessage() {
        return awakeEffectMessage;
    }
}
