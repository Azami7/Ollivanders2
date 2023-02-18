package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2Player;

/**
 * Prior Incantato is a spell that forced a wand to show an "echo" of the most
 * recent spell it had performed.
 * <p>
 * http://harrypotter.wikia.com/wiki/Reverse_Spell
 */
public class PRIOR_INCANTATO extends O2Spell {
	/**
	 * The radius of players who will "see" the effect
	 */
	private static final int visibleRadius = 10;

	/**
	 * Default constructor for use in generating spell text. Do not use to cast the
	 * spell.
	 *
	 * @param plugin the Ollivanders2 plugin
	 */
	public PRIOR_INCANTATO(Ollivanders2 plugin) {
		super(plugin);

		spellType = O2SpellType.PRIOR_INCANTATO;
		branch = O2MagicBranch.CHARMS;

		flavorText = new ArrayList<>() {
			{
				add("The Reverse Spell");
				add("\"Placing his wand tip to tip against Harry's wand and saying the spell, Amos causes a shadow of the Dark Mark to erupt from where the two wands meet, showing that this was the last spell cast with Harry's wand.\"");
			}
		};

		text = "Force a player's wand to reveal the last spell cast. Your success depends on your experience with this spell.";
	}

	/**
	 * Constructor.
	 *
	 * @param plugin    a callback to the MC plugin
	 * @param player    the player who cast this spell
	 * @param rightWand which wand the player was using
	 */
	public PRIOR_INCANTATO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
		super(plugin, player, rightWand);

		spellType = O2SpellType.INFORMOUS;
		branch = O2MagicBranch.CHARMS;

		initSpell();
	}

	/**
	 * Find a nearby player and attempt to force the shadow of the last spell from
	 */
	@Override
	protected void doCheckEffect() {
		if (hasHitTarget())
			kill();

		for (Player target : getNearbyPlayers(defaultRadius)) {
			if (target.getUniqueId() == player.getUniqueId())
				continue;

			int rand = (Math.abs(Ollivanders2Common.random.nextInt()) % 10);

			if (usesModifier > rand)
				doPriorIncantato(target);
			else
				player.sendMessage(Ollivanders2.chatColor + target.getName() + "'s wand resists your spell.");

			kill();
			return;
		}
	}

	/**
	 * Show the prior incantation for the target's wand.
	 *
	 * @param target the target player
	 */
	private void doPriorIncantato(@NotNull Player target) {
		O2Player o2p = p.getO2Player(target);
		if (o2p == null) {
			common.printDebugMessage("Null o2player in PRIOR_INCANTATO.doPriorIncantato", null, null, true);
			return;
		}

		O2SpellType prior = o2p.getPriorIncantatem();

		if (prior == null) {
			player.sendMessage(Ollivanders2.chatColor + target.getName() + "'s wand has not cast a spell.");

			return;
		}

		// if the wand has previously cast a spell, let the target plus all nearby
		// players "see" the prior incantato
		List<Entity> nearbyPlayers = EntityCommon.getNearbyEntitiesByType(target.getLocation(), visibleRadius,
				EntityType.PLAYER);

		for (Entity entity : nearbyPlayers) {
			if (!(entity instanceof Player) || entity.getUniqueId() == target.getUniqueId())
				continue;

			entity.sendMessage(Ollivanders2.chatColor + "The shadowy echo of the spell " + prior.getSpellName()
					+ " emits from " + target.getName() + "'s wand.");
		}

		target.sendMessage(Ollivanders2.chatColor + "The shadowy echo of the spell " + prior.getSpellName()
				+ " emits from your wand.");
	}
}
