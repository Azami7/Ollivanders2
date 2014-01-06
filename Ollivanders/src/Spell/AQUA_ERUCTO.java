package Spell;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Spell shoots a block of water at a target, extinguishing fire
 * @author lownes
 *
 */
public class AQUA_ERUCTO extends SpellProjectile implements Spell{

	private boolean isWater;
	private double lifeTime;
	
	public AQUA_ERUCTO(Ollivanders p, Player player, Spells name, Double rightWand){
		super(p, player, name, rightWand);
		lifeTime = usesModifier*16;
		isWater = false;
	}

	public void checkEffect() {
		if (isWater){
			super.getBlock().setType(Material.AIR);
			isWater = false;
		}
		move();
		if ((super.getBlock().getType() == Material.AIR || super.getBlock().getType() == Material.FIRE) && lifeTicks <= lifeTime){
			super.getBlock().setType(Material.WATER);
			isWater = true;
		}
		if (lifeTicks > lifeTime){
			kill();
		}
	}
}