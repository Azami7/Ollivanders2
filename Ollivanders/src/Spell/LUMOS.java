package Spell;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

/**
 * Gives nightvision for an amount of time depending on the player's
 * spell level.
 * @author lownes
 *
 */
public class LUMOS extends SpellProjectile implements Spell{

	public LUMOS(Ollivanders plugin, Player player, Spells name,
			Integer rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, (int) (usesModifier*1200), 1), true);
		kill();
	}
	
}