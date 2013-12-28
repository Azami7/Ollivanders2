package Spell;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;
import me.cakenggt.Ollivanders.StationarySpells;

/**
 * Creates a muffliato stationary spell object. Only players within that
 * object can hear other players within it. Time duration depends on spell's
 * level.
 * @author lownes
 *
 */
public class MUFFLIATO extends SpellProjectile implements Spell{

	public MUFFLIATO(Ollivanders plugin, Player player, Spells name,
			Integer rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		if (super.getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER){
			int duration = (int)usesModifier*1200;
			StationarySpell.MUFFLIATO muffliato = new StationarySpell.MUFFLIATO(player, location, StationarySpells.HORCRUX, 5, duration);
			muffliato.flair(20);
			p.addStationary(muffliato);
		}
	}
	
}