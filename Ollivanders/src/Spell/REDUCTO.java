package Spell;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Creates an explosion of magnitude depending on the spell level which
 * destroys blocks and sets fires.
 * @author lownes
 *
 */
public class REDUCTO extends SpellProjectile implements Spell{
	
	public REDUCTO(Ollivanders p, Player player, Spells name, Double rightWand){
		super(p, player, name, rightWand);
	}


	public void checkEffect() {
		move();
		if (super.getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER){
			super.location.getWorld().createExplosion(super.location, (float)(usesModifier*0.4));
		}
	}
}