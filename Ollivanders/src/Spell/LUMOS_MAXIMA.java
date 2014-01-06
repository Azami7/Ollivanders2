package Spell;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

/**
 * Creates a glowstone at the reticule that goes away after a time.
 * @author lownes
 *
 */
public class LUMOS_MAXIMA extends SpellProjectile implements Spell{

	boolean lit;

	public LUMOS_MAXIMA(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
		lit = false;
	}

	public void checkEffect() {
		if (!lit){
			move();
			if (getBlock().getType() != Material.AIR){
				location.subtract(vector);
				location.getBlock().setType(Material.GLOWSTONE);
				p.getTempBlocks().add(getBlock());
				lit = true;
				lifeTicks = (int)(-(usesModifier*1200));
				kill = false;
			}
		}
		else{
			if (getBlock().getType() == Material.GLOWSTONE){
				location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 0);
				lifeTicks ++;
			}
			else{
				kill();
			}
		}
		if (lifeTicks >= 159){
			if (getBlock().getType() == Material.GLOWSTONE){
				location.getBlock().setType(Material.AIR);
			}
			p.getTempBlocks().remove(location.getBlock());
			kill = true;
		}
	}

}