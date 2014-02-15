package Spell;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;
import me.cakenggt.Ollivanders.StationarySpells;

/**
 * Creates a spongify StationarySpellObj
 * @author lownes
 *
 */
public class SPONGIFY extends SpellProjectile implements Spell{

	public SPONGIFY(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER){
			int duration = (int)(usesModifier*1200);
			StationarySpell.SPONGIFY sponge = new StationarySpell.SPONGIFY(player, location, StationarySpells.SPONGIFY, 5, duration);
			sponge.flair(10);
			p.addStationary(sponge);
			kill();
		}
	}
	
}