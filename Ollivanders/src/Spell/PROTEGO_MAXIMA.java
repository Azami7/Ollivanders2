package Spell;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;
import me.cakenggt.Ollivanders.StationarySpells;

/**
 * Makes a spell projectile that creates a sheid that hurts any entities within 0.5 meters of the spell wall.
 * @author lownes
 *
 */
public class PROTEGO_MAXIMA extends SpellProjectile implements Spell{

	public PROTEGO_MAXIMA(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER){
			int duration = (int)(usesModifier*1200);
			double damage = usesModifier/10;
			StationarySpell.PROTEGO_MAXIMA max = new StationarySpell.PROTEGO_MAXIMA(player, location, StationarySpells.PROTEGO_MAXIMA, 5, duration, damage);
			max.flair(10);
			p.addStationary(max);
		}
	}
	
}