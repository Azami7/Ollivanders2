package net.pottercraft.ollivanders2.spell;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.LlamaWatcher;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.EntityCommon;

/**
 * Turn target player in to a llama.
 */
public final class LAMA extends PlayerDisguise {
	/**
	 * Default constructor for use in generating spell text. Do not use to cast the
	 * spell.
	 *
	 * @param plugin the Ollivanders2 plugin
	 */
	public LAMA(Ollivanders2 plugin) {
		super(plugin);

		spellType = O2SpellType.LAMA;
		branch = O2MagicBranch.TRANSFIGURATION;

		text = "Turns target player in to a llama.";
	}

	/**
	 * Constructor.
	 *
	 * @param plugin    a callback to the MC plugin
	 * @param player    the player who cast this spell
	 * @param rightWand which wand the player was using
	 */
	public LAMA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
		super(plugin, player, rightWand);

		spellType = O2SpellType.LAMA;
		branch = O2MagicBranch.TRANSFIGURATION;

		targetType = EntityType.LLAMA;
		disguiseType = DisguiseType.getType(targetType);
		disguise = new MobDisguise(disguiseType);
		LlamaWatcher watcher = (LlamaWatcher) disguise.getWatcher();
		watcher.setAdult();
		watcher.setColor(EntityCommon.getRandomLlamaColor());

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
