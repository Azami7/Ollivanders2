package Spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;
import me.cakenggt.Ollivanders.StationarySpellObj;

/**
 * Lengthens the duration of shield spells
 * @author lownes
 *
 */
public class FIANTO_DURI extends SpellProjectile implements Spell{

	public FIANTO_DURI(Ollivanders plugin, Player player, Spells name,
			Integer rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		List <StationarySpellObj> inside = new ArrayList<StationarySpellObj>();
		for (StationarySpellObj spell : p.getStationary()){
			if (spell.isInside(location)){
				inside.add(spell);
				kill();
			}
		}
		int addedAmount = (int)((usesModifier*1200)/inside.size());
		for (StationarySpellObj spell : inside){
			spell.duration += addedAmount;
			spell.flair(10);
		}
	}

}