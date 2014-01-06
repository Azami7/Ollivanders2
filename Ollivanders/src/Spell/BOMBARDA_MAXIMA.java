package Spell;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Creates an explosion at the target location twice as 
 * powerful as bombarda. Doesn't break blocks.
 * @author lownes
 *
 */
public class BOMBARDA_MAXIMA extends SpellProjectile implements Spell{

	public BOMBARDA_MAXIMA(Ollivanders p, Player player, Spells name, Double rightWand){
		super(p, player, name, rightWand);
	}


	public void checkEffect() {
		move();
		if (super.getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER){
			super.location.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), (float)(1.6*usesModifier), false, false);
		}
	}
}