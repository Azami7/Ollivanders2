package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

public class TERGEO extends SpellProjectile implements Spell{

	boolean move;
	
	public TERGEO(Ollivanders2 plugin, Player player, Spells name,
                  Double rightWand) {
		super(plugin, player, name, rightWand);
		move = true;
	}

	public void checkEffect() {
		if (move){
			move();
			if (getBlock().getType() == Material.WATER || getBlock().getType() == Material.STATIONARY_WATER){
				Block block = location.getBlock();
				block.setType(Material.AIR);
				changed.add(block);
				kill = false;
				move = false;
				lifeTicks = (int)(-(usesModifier*1200));
			}
			for (SpellProjectile proj : p.getProjectiles()){
				if (proj.name == Spells.AGUAMENTI && proj.location.getWorld() == location.getWorld()){
					if (proj.location.distance(location) < 1){
						proj.revert();
						proj.kill();
					}
				}
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
			if (mat == Material.AIR){
				block.setType(Material.STATIONARY_WATER);
			}
		}
	}
	
}