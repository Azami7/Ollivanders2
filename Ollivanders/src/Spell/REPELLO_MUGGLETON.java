package Spell;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;
import me.cakenggt.Ollivanders.StationarySpells;

/**Adds a repello muggleton stationary spell object
 * @author lownes
 *
 */
public class REPELLO_MUGGLETON extends SpellProjectile implements Spell{

	public REPELLO_MUGGLETON(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER){
			int duration = (int)(usesModifier*1200);
			StationarySpell.REPELLO_MUGGLETON stat = new StationarySpell.REPELLO_MUGGLETON(player, location, StationarySpells.REPELLO_MUGGLETON, 5, duration);
			stat.flair(10);
			p.addStationary(stat);
			kill();
		}
	}
	
}