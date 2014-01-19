package Spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;
import me.cakenggt.Ollivanders.StationarySpellObj;

public class ALOHOMORA extends SpellProjectile implements Spell{

	public ALOHOMORA(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		List <StationarySpellObj> inside = new ArrayList<StationarySpellObj>();
		for (StationarySpellObj spell : p.getStationary()){
			if (spell instanceof StationarySpell.COLLOPORTUS){
				if (spell.isInside(location)){
					inside.add(spell);
					kill();
				}
			}
		}
		int subAmount = (int)((usesModifier*1200)/inside.size());
		for (StationarySpellObj spell : inside){
			spell.age(subAmount);
			spell.flair(10);
		}
	}

}