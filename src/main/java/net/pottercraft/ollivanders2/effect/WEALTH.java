package net.pottercraft.ollivanders2.effect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;

/**
 * Add money to a player's inventory every 10 seconds
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
	 * Constructor
	 *
	 * @param plugin   a callback to the MC plugin
	 * @param duration the duration of the effect
	 * @param pid      the player this effect acts on
	 */
	public WEALTH(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
		super(plugin, duration, pid);

		effectType = O2EffectType.WEALTH;
		informousText = legilimensText = "feels fortunate";

		target = p.getServer().getPlayer(targetID);

		divinationText.add("will be blessed by fortune");
		divinationText.add("will have unnatural luck");
		divinationText.add("shall be granted a wish");
		divinationText.add("will be gifted by a leprechaun");
	}

	/**
	 * Age this effect each game tick.
	 */
	@Override
	public void checkEffect() {
		age(1);

		int rand = (Math.abs(Ollivanders2Common.random.nextInt()) % 100) * strength;

		// only take action once per 10 seconds, which is every 120 ticks
		if ((duration % 120) == 0) {
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
	 * Set the strength of this effect
	 *
	 * @param strength a positive integer where 1 is normal strength
	 */
	public void setStrength(int strength) {
		this.strength = strength;
	}

	/**
	 * Do any cleanup related to removing this effect from the player
	 */
	@Override
	public void doRemove() {
	}
}
