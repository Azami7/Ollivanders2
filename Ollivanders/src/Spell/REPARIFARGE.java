package Spell;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;
import me.cakenggt.Ollivanders.Transfiguration;

public class REPARIFARGE extends SpellProjectile implements Spell{

	public REPARIFARGE(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		List<Entity> entities = this.getCloseEntities(1);
		for (Entity entity : entities){
			for (SpellProjectile proj : p.getProjectiles()){
				if (proj instanceof Transfiguration){
					if (entity.getEntityId() == ((Transfiguration) proj).getToID()){
						proj.lifeTicks = proj.lifeTicks + (int)(usesModifier*1200) + 160;
					}
				}
			}
		}
	}
	
}