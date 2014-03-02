package Spell;

import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Openable;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

/**Opens a trapdoor or door.
 * @author lownes
 *
 */
public class DISSENDIUM extends SpellProjectile implements Spell {

	private double lifeTime;
	private boolean move;
	private int openTime;
	private boolean open;

	public DISSENDIUM(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
		lifeTime = usesModifier*16;
		move = true;
		openTime = 160;
		open = true;
	}

	@Override
	public void checkEffect() {
		if (move){
			move();
			if (getBlock().getState().getData() instanceof Openable){
				kill = false;
				move = false;
				open = ((Openable)getBlock().getState().getData()).isOpen();
			}
		}
		else{
			openTime --;
			BlockState state = getBlock().getState();
			Openable oble = (Openable)state.getData();
			if (openTime > 0){
				oble.setOpen(!open);
				state.setData((MaterialData) oble);
				state.update();
			}
			else{
				oble.setOpen(open);
				state.setData((MaterialData) oble);
				state.update();
				kill();
			}
		}
		if (lifeTicks > lifeTime){
			kill();
		}
	}

}
