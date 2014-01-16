package Spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

/**Herbivicus causes crops in a radius to grow.
 * @author lownes
 *
 */
public class HERBIVICUS extends SpellProjectile implements Spell{

	public HERBIVICUS(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		double radius = usesModifier;
		if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER && getBlock().getType() != Material.LAVA && getBlock().getType() != Material.STATIONARY_LAVA){
			for (Block block : getBlocksInRadius(location, radius)){
				//if (block.getType() == Material.CROPS || block.getType() == Material.CARROT || block.getType() == Material.MELON || block.getType() == Material.NETHER_WARTS || block.getType() == Material.POTATO || block.getType() == Material.PUMPKIN_STEM){
				if (block.getType() == Material.CROPS){	
					//System.out.println("Found Crop!");
					List<CropState> stateList = new ArrayList<CropState>();
					stateList.add(CropState.SEEDED);
					stateList.add(CropState.GERMINATED);
					stateList.add(CropState.VERY_SMALL);
					stateList.add(CropState.SMALL);
					stateList.add(CropState.MEDIUM);
					stateList.add(CropState.TALL);
					stateList.add(CropState.VERY_TALL);
					stateList.add(CropState.RIPE);
					int currentState = stateList.indexOf(((Crops)block.getState().getData()).getState());
					int newState = currentState+1;
					if (newState > 7){
						newState = 7;
					}
					BlockState blockState = block.getState();
					MaterialData blockData = blockState.getData();
					CropState newCropState = stateList.get(newState);
					//System.out.println(currentState);
					//System.out.println(newState);
					//System.out.println("currentState " + ((Crops)block.getState().getData()).getState());
					//System.out.println("newCropState " + newCropState);
					((Crops)blockData).setState(newCropState);
					//System.out.println("1 " + ((Crops)blockData).getState());
					blockState.setData(blockData);
					blockState.update();
					//System.out.println("2 " + ((Crops)blockState.getData()).getState());
				}
			}
			kill();
		}
	}
	
}