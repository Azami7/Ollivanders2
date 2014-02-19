package Spell;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Spell which places a block of water against the targeted block.
 * @author lownes
 *
 */
public class AGUAMENTI extends SpellProjectile implements Spell{

	boolean move;

	public AGUAMENTI(Ollivanders p, Player player, Spells name, Double rightWand){
		super(p, player, name, rightWand);
		move = true;
	}

	public void checkEffect() {
		if (move){
			move();
			if (getBlock().getType() != Material.AIR){
				Block block = location.subtract(super.vector).getBlock();
				block.setType(Material.WATER);
				changed.add(block);
				kill = false;
				move = false;
				lifeTicks = (int)(-(usesModifier*1200));
			}
		}
		else{
			lifeTicks ++;
		}
		if (lifeTicks >= 159){
			revert();
			kill();
		}
	}
	
	public void revert(){
		for (Block block : changed){
			Material mat = block.getType();
			if (mat == Material.WATER || mat == Material.STATIONARY_WATER){
				block.setType(Material.AIR);
			}
		}
	}
}