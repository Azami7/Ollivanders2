package Spell;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;
import me.cakenggt.Ollivanders.StationarySpells;

/** Spawns a colloportus stationaryspellobj
 * @author lownes
 *
 */
public class COLLOPORTUS extends SpellProjectile implements Spell{

	public COLLOPORTUS(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER){
			int duration = (int)(usesModifier*1200);
			StationarySpell.COLLOPORTUS total = new StationarySpell.COLLOPORTUS(player, location, StationarySpells.COLLOPORTUS, 5, duration);
			total.flair(10);
			p.addStationary(total);
			kill();
		}
	}
	
}