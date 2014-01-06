package Spell;

import java.util.List;

import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;
import me.cakenggt.Ollivanders.StationarySpellObj;

/**
 * Spell which causes any stationary spell objects to flair with 
 * an intensity equal to your level
 * @author lownes
 *
 */
public class APARECIUM extends SpellProjectile implements Spell{

	public APARECIUM(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		List<StationarySpellObj> stationaries = p.checkForStationary(location);
		for (StationarySpellObj stationary : stationaries){
			//stationary.flair(Math.sqrt(p.getSpellNum(player, name))/rightWand);
			int level = (int) usesModifier;
			if (level > 10){
				level = 10;
			}
			stationary.flair(level);
		}
	}
	
}