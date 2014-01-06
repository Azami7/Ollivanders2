package Spell;

import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;
import me.cakenggt.Ollivanders.StationarySpells;

public class PROTEGO extends SpellProjectile implements Spell{

	public PROTEGO(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		StationarySpell.PROTEGO protego = new StationarySpell.PROTEGO(player, location, StationarySpells.PROTEGO, 5, 12000);
		protego.flair(2);
		p.addStationary(protego);
		kill();
	}
	
}