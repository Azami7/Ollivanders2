package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Wealth effect that periodically grants the affected player magical currency.
 *
 * <p>WEALTH is a beneficial effect that periodically generates magical currency coins and adds them
 * to the target player's inventory. Every 120 game ticks (6 seconds), the effect generates a random
 * coin based on weighted probability influenced by the strength multiplier. The higher the strength,
 * the more likely the player receives high-value coins (Galleons) instead of low-value coins (Knuts).
 * The probability distribution is: strength-adjusted random % 100, with Galleons (>90%), Sickles
 * (>60%), and Knuts (default). The effect is detectable by both mind-reading spells (Legilimens)
 * and information spells (Informous) which report the target "feels fortunate".</p>
 *
 * <p>Wealth Configuration:</p>
 * <ul>
 * <li>Generation interval: every 120 game ticks (6 seconds)</li>
 * <li>Coin types: Galleon (high value), Sickle (medium value), Knut (low value)</li>
 * <li>Strength parameter: multiplies random value to affect coin probability</li>
 * <li>Probability calculation: (random % 100) * strength</li>
 * <li>Detection text: "feels fortunate"</li>
 * </ul>
 *
 * @author Azami7
 */
public class WEALTH extends O2Effect {
    /**
     * Multiplier that affects how likely the player will get a more valuable coin.
     */
    int strength = 1;

    /**
     * The player affected by this effect
     */
    Player target;

    /**
     * Constructor for creating a wealth effect.
     *
     * <p>Creates a wealth effect that periodically generates magical coins for the target player.
     * Initializes the strength multiplier to 1 (normal probability). Detection text is set for both
     * mind-reading spells (Legilimens) and information spells (Informous). The target player reference
     * is acquired at initialization time.</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the wealth effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to grant wealth
     */
    public WEALTH(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.WEALTH;
        checkDurationBounds();

        informousText = legilimensText = "feels fortunate";
    }

    /**
     * Age the wealth effect and generate coins periodically.
     *
     * <p>Called each game tick. This method ages the effect counter. Every 120 ticks (6 seconds),
     * a random coin is generated based on weighted probability. The strength multiplier is applied to
     * the random value to increase the chance of higher-value coins. Generated coins are immediately
     * added to the target player's inventory.</p>
     */
    @Override
    public void checkEffect() {
        // on first pass, keep track of the target player so we do not have to call getPlayer() every tick
        if (target == null) {
            Player player = p.getServer().getPlayer(targetID);

            // if player is still null, player not found, kill and return
            if (player == null) {
                kill();
                return;
            }
        }

        // age the effect
        age(1);

        int rand = (Math.abs(Ollivanders2Common.random.nextInt()) % 100) * strength;

        // only take action once per 10 seconds, which is every 120 ticks
        if ((duration % (Ollivanders2Common.ticksPerSecond * 10)) == 0) {
            List<ItemStack> kit = new ArrayList<>();

            ItemStack money;

            if (rand > 90)
                money = O2ItemType.GALLEON.getItem(1);
            else if (rand > 60)
                money = O2ItemType.SICKLE.getItem(1);
            else
                money = O2ItemType.KNUT.getItem(1);

            kit.add(money);

            O2PlayerCommon.givePlayerKit(target, kit);
        }
    }

    /**
     * Set the strength multiplier for coin probability generation.
     *
     * <p>The strength multiplier affects the weighted probability of coin generation. A strength of 1
     * uses normal probability distribution. Higher values increase the likelihood of generating
     * high-value coins (Galleons) over low-value coins (Knuts).</p>
     *
     * @param strength a positive integer multiplier where 1 is normal strength
     */
    public void setStrength(int strength) {
        this.strength = strength;
    }

    /**
     * Perform cleanup when the wealth effect is removed.
     *
     * <p>The default implementation does nothing, as WEALTH has no persistent state to clean up.
     * When removed, the player keeps all coins that were generated while the effect was active.</p>
     */
    @Override
    public void doRemove() {
    }
}
