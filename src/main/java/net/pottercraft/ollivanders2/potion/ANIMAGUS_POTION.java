package net.pottercraft.ollivanders2.potion;

import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.ANIMAGUS_EFFECT;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.player.O2Player;

/**
 * Consumed after successfully casting the Animagus incantation, this will turn
 * a player in to an Animagus.
 *
 * @author Azami7
 */
public final class ANIMAGUS_POTION extends O2Potion {
	/**
	 * Constructor
	 *
	 * @param plugin a callback to the plugin
	 */
	public ANIMAGUS_POTION(@NotNull Ollivanders2 plugin) {
		super(plugin);

		potionType = O2PotionType.ANIMAGUS_POTION;

		ingredients.put(O2ItemType.MANDRAKE_LEAF, 1);
		ingredients.put(O2ItemType.DEW_DROP, 2);
		ingredients.put(O2ItemType.DEATHS_HEAD_MOTH_CHRYSALIS, 1);
		ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 3);

		text = "An Animagus is a wizard who elects to turn into an animal. This potion, if brewed and consumed correctly, "
				+ "will disguisePlayer the drinker in to their animal form. Thereafter, the Animagus can disguisePlayer without the "
				+ "potion, however it will take considerable practice to change forms consistently at will.";
		flavorText.add("\"You know that I can disguise myself most effectively.\" -Peter Pettigrew");
		flavorText.add(
				"\"Normally, I have a very sweet disposition as a dog; in fact, more than once, James suggested I make the change permanent. The tail I could live with...but the fleas, they're murder.\" -Sirius Black");

		potionColor = Color.fromRGB(102, 0, 0);
	}

	/**
	 * Drink this potion and do effects
	 *
	 * @param player the player who drank the potion
	 */
	@Override
	public void drink(@NotNull Player player) {
		O2Player o2p = p.getO2Player(player);

		if (o2p == null || !Ollivanders2.libsDisguisesEnabled) {
			player.sendMessage(Ollivanders2.chatColor + "Nothing seems to happen.");
			return;
		}

		if (o2p.isAnimagus()) {
			if (Ollivanders2API.getPlayers().playerEffects.hasEffect(o2p.getID(), O2EffectType.ANIMAGUS_INCANTATION))
				Ollivanders2API.getPlayers().playerEffects.removeEffect(o2p.getID(), O2EffectType.ANIMAGUS_INCANTATION);

			player.sendMessage(Ollivanders2.chatColor + "You taste something vaguely familiar.");
			return;
		}

		if (!player.getWorld().isThundering() && Ollivanders2.useStrictAnimagusConditions) {
			// potion only works in a thunderstorm
			player.sendMessage(Ollivanders2.chatColor + "Nothing seems to happen.");
			return;
		}

		if (Ollivanders2API.getPlayers().playerEffects.hasEffect(o2p.getID(), O2EffectType.ANIMAGUS_INCANTATION)) {
			o2p.setIsAnimagus();
			Ollivanders2API.getPlayers().playerEffects.removeEffect(o2p.getID(), O2EffectType.ANIMAGUS_INCANTATION);

			ANIMAGUS_EFFECT animagusEffect = new ANIMAGUS_EFFECT(p, 5, player.getUniqueId());
			Ollivanders2API.getPlayers().playerEffects.addEffect(animagusEffect);

			player.sendMessage(Ollivanders2.chatColor + "You feel transformed.");
		}
	}
}