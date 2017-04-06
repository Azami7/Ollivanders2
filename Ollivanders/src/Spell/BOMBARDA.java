package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

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
	
	public BOMBARDA(Ollivanders2 p, Player player, Spells name, Double rightWand){
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