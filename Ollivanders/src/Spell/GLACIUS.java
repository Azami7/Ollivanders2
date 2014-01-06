package Spell;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

public class GLACIUS extends SpellProjectile implements Spell{

	public GLACIUS(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		Block center = getBlock();
		Material type = center.getType();
		double radius = usesModifier/2;
		if (type != Material.AIR){
			for (Block block : getBlocksInRadius(location, radius)){
				Material changeType = block.getType();
				if (changeType == Material.FIRE){
					block.setType(Material.AIR);
				}
				else if (changeType == Material.WATER || changeType == Material.STATIONARY_WATER){
					block.setType(Material.ICE);
				}
				else if (changeType == Material.LAVA || changeType == Material.STATIONARY_LAVA){
					block.setType(Material.OBSIDIAN);
				}
				else if (changeType == Material.ICE){
					block.setType(Material.PACKED_ICE);
				}
			}
			kill();
		}
	}

}