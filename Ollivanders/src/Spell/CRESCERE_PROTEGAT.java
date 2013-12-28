package Spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;
import me.cakenggt.Ollivanders.StationarySpellObj;

/**
 * Grows a stationarySpellObject's radius. Only the player who created the stationarySpellObject can change it's radius.
 * @author lownes
 *
 */
public class CRESCERE_PROTEGAT extends SpellProjectile implements Spell{

	public CRESCERE_PROTEGAT(Ollivanders plugin, Player player, Spells name,
			Integer rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		List <StationarySpellObj> inside = new ArrayList<StationarySpellObj>();
		for (StationarySpellObj spell : p.getStationary()){
			if (spell.isInside(location) && spell.radius < (int)usesModifier){
				inside.add(spell);
				kill();
			}
		}
		//int limit = (int)(usesModifier/inside.size());
		int limit = (int)usesModifier;
		for (StationarySpellObj spell : inside){
			if (spell.radius < limit && spell.player.equals(player.getDisplayName())){
				spell.radius ++;
				spell.flair(10);
			}
		}
	}
	
}