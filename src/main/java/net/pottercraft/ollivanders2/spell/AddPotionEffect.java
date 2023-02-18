package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;

/**
 * Spell that adds a potion effect to one or more targets.
 * <p>
 * Reference:
 * https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffect.html
 */
public abstract class AddPotionEffect extends O2Spell {
	/**
	 * The duration for this effect.
	 */
	int durationInSeconds;

	/**
	 * The longest this effect can last.
	 */
	int maxDurationInSeconds = 300; // 5 minutes;

	/**
	 * The least amount of time this effect can last.
	 */
	int minDurationInSeconds = 5; // 5 seconds

	/**
	 * Duration modifier is a multiplier on the usesModifier for this spell
	 */
	double durationModifier = 1.0; // 100% of usesModifier

	/**
	 * Strength modifier, 0 is no modifier.
	 */
	int amplifier = 0;

	/**
	 * Maximum strength this potion effect can be.
	 * <p>
	 * Reference: https://minecraft.fandom.com/wiki/Effect
	 */
	int maxAmplifier = 127;

	/**
	 * Minimum strength this potion effect can be.
	 * <p>
	 * Reference: https://minecraft.fandom.com/wiki/Effect
	 */
	int minAmplifier = -127;

	/**
	 * Amplifier modifier is a multiplier on the usesModifier for this spell
	 */
	double amplifierModifier = 0.1; // 10% of usesModifier

	/**
	 * Number of targets that can be affected
	 */
	int numberOfTargets = 1;

	/**
	 * Whether the spell targets the caster
	 */
	boolean targetSelf = false;

	/**
	 * The potion effect. Set to luck by default.
	 */
	List<PotionEffectType> effectTypes = new ArrayList<>();

	/**
	 * Default constructor for use in generating spell text. Do not use to cast the
	 * spell.
	 *
	 * @param plugin the Ollivanders2 plugin
	 */
	public AddPotionEffect(Ollivanders2 plugin) {
		super(plugin);

		branch = O2MagicBranch.CHARMS;
	}

	/**
	 * Constructor.
	 *
	 * @param plugin    a callback to the MC plugin
	 * @param player    the player who cast this spell
	 * @param rightWand which wand the player was using
	 */
	public AddPotionEffect(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
		super(plugin, player, rightWand);

		branch = O2MagicBranch.CHARMS;
	}

	/**
	 * If a target player is within the radius of the projectile, add the potion
	 * effect to the player.
	 */
	@Override
	protected void doCheckEffect() {
		affectRadius(defaultRadius, false);

		if (hasHitTarget())
			kill();
	}

	/**
	 * Affect targets within the radius.
	 *
	 * @param radius the radius of the spell
	 * @param flair  whether or not to show a visual flair
	 */
	void affectRadius(double radius, boolean flair) {
		if (flair)
			Ollivanders2Common.flair(location, (int) radius, 10);

		if (targetSelf) {
			addEffectsToTarget(player);
			numberOfTargets = numberOfTargets - 1;
		}

		for (LivingEntity livingEntity : getNearbyLivingEntities(radius)) {
			// stop when the limit of targets is reached
			if (numberOfTargets <= 0) {
				kill();
				return;
			}

			if (!targetSelf && livingEntity.getUniqueId() == player.getUniqueId())
				continue;

			addEffectsToTarget(livingEntity);
			numberOfTargets = numberOfTargets - 1;
		}
	}

	/**
	 * Add the effect to a target living entity.
	 *
	 * @param target the target living entity
	 */
	void addEffectsToTarget(@NotNull LivingEntity target) {
		// duration
		durationInSeconds = (int) (usesModifier * durationModifier);
		if (durationInSeconds < minDurationInSeconds)
			durationInSeconds = minDurationInSeconds;
		else if (durationInSeconds > maxDurationInSeconds)
			durationInSeconds = maxDurationInSeconds;
		int duration = durationInSeconds * Ollivanders2Common.ticksPerSecond;

		// amplifier
		amplifier = (int) (usesModifier * amplifierModifier);
		if (amplifier < minAmplifier)
			amplifier = minAmplifier;
		else if (amplifier > maxAmplifier)
			amplifier = maxAmplifier;

		for (PotionEffectType effectType : effectTypes) {
			org.bukkit.potion.PotionEffect effect = new org.bukkit.potion.PotionEffect(effectType, duration, amplifier);
			target.addPotionEffect(effect);

			common.printDebugMessage("Added " + effectType.toString() + " to " + target.getName()
					+ " with amplifier of " + amplifier + " and duration of " + durationInSeconds + " seconds", null,
					null, false);
		}
	}
}
