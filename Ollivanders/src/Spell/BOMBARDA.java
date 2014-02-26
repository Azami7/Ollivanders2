package Spell;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Creates an explosion at the target which scales with 
 * the player's level in the spell. Doesn't break blocks.
 * @author lownes
 *
 */
public class BOMBARDA extends SpellProjectile implements Spell{
	
	public BOMBARDA(Ollivanders p, Player player, Spells name, Double rightWand){
		super(p, player, name, rightWand);
	}


	public void checkEffect() {
		move();
		if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER){
			Location backLoc = super.location.clone().subtract(vector);
			backLoc.getWorld().createExplosion(backLoc.getX(), backLoc.getY(), backLoc.getZ(), (float)(0.8*usesModifier), false, false);
		}
	}
}