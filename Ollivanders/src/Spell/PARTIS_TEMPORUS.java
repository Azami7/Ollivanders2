package Spell;

import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;
import me.cakenggt.Ollivanders.StationarySpellObj;

/**
 * Temporarily disables a stationary spell's effects if it is your spell.
 * @author lownes
 *
 */
public class PARTIS_TEMPORUS extends SpellProjectile implements Spell{

	public boolean move;
	
	public PARTIS_TEMPORUS(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
		move = true;
	}

	public void checkEffect() {
		if (move){
			move();
		}
		else{
			lifeTicks ++;
		}
		for (StationarySpellObj spell : p.getStationary()){
			if (spell.isInside(location) && spell.player.equals(player)){
				spell.active = false;
				spell.flair(10);
				move = false;
			}
		}
		if (lifeTicks > 160){
			for (StationarySpellObj spell : p.getStationary()){
				if (spell.isInside(location)){
					spell.active = true;
					spell.flair(10);
				}
			}
			kill = true;
		}
	}
	
}