package Spell;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;
import me.cakenggt.Ollivanders.StationarySpells;

public class PROTEGO_HORRIBILIS extends SpellProjectile implements Spell{

	public PROTEGO_HORRIBILIS(Ollivanders plugin, Player player, Spells name,
			Integer rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER){
			int duration = (int)(usesModifier*1200);
			StationarySpell.PROTEGO_HORRIBILIS total = new StationarySpell.PROTEGO_HORRIBILIS(player, location, StationarySpells.PROTEGO_HORRIBILIS, 5, duration);
			total.flair(10);
			p.addStationary(total);
		}
	}
	
}