package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Grants the affected player a random magical coin (Galleon, Sickle, or Knut) every 10 seconds. A higher
 * {@link #setStrength(int)} multiplier skews the odds toward more valuable coins. Detectable via Informous and
 * Legilimens.
 *
 * @author Azami7
 */
public class WEALTH extends O2Effect {
    /**
     * Multiplier that affects how likely the player will get a more valuable coin.
     */
    int strength = 1;

    /**
     * Constructor
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
     * Ages the effect and, once every 10 seconds, adds one strength-weighted coin to the target's inventory.
     */
    @Override
    public void checkEffect() {
        age(1);

        // only grant a coin once every 10 seconds
        if ((duration % (Ollivanders2Common.ticksPerSecond * 10)) == 0) {
            List<ItemStack> kit = new ArrayList<>();

            ItemStack money;
            int rand = Ollivanders2Common.random.nextInt(100) * strength;

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
     * Set the strength multiplier that skews coin generation toward more valuable coins.
     *
     * @param strength a positive multiplier where 1 is normal
     */
    public void setStrength(int strength) {
        this.strength = strength;
    }

    @Override
    public void doRemove() {
    }
}
