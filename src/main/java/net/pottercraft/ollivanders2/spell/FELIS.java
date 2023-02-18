package net.pottercraft.ollivanders2.spell;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.CatWatcher;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;

/**
 * Turn target player in to a cat, in rare cases an ocelot.
 */
public final class FELIS extends PlayerDisguise {
	/**
	 * Default constructor for use in generating spell text. Do not use to cast the
	 * spell.
	 *
	 * @param plugin the Ollivanders2 plugin
	 */
	public FELIS(Ollivanders2 plugin) {
		super(plugin);

		spellType = O2SpellType.FELIS;
		branch = O2MagicBranch.TRANSFIGURATION;

		text = "Turns target player in to a cat.";
	}

	/**
	 * Constructor.
	 *
	 * @param plugin    a callback to the MC plugin
	 * @param player    the player who cast this spell
	 * @param rightWand which wand the player was using
	 */
	public FELIS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
		super(plugin, player, rightWand);

		spellType = O2SpellType.FELIS;
		branch = O2MagicBranch.TRANSFIGURATION;

		targetType = EntityType.CAT;
		disguiseType = DisguiseType.getType(targetType);
		disguise = new MobDisguise(disguiseType);
		CatWatcher watcher = (CatWatcher) disguise.getWatcher();
		watcher.setAdult();
		watcher.setType(EntityCommon.getRandomCatType());

		int rand = Ollivanders2Common.random.nextInt() % 10;
		if (rand == 0)
			watcher.isTamed();

		initSpell();
	}

	/**
	 * Calculate success rate based on player skill level
	 */
	@Override
	void doInitSpell() {
		calculateSuccessRate();
	}
}
