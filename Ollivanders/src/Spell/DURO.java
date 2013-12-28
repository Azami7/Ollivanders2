package Spell;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.Spells;
import me.cakenggt.Ollivanders.Transfiguration;

/**
 * Turns an entity to stone. If the stone is broken, the entity will not respawn at the end of the transfiguration.
 * @author lownes
 *
 */
public class DURO extends Transfiguration implements Spell{
	
	public DURO(Ollivanders plugin, Player player, Spells name,
			Integer rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		if (!hasTransfigured()){
			move();
			for (Entity e : player.getWorld().getEntities()){
				if (e.getType() != EntityType.PLAYER && e.getLocation().distance(location)<1){
					location = e.getLocation();
					location.getBlock().setType(Material.STONE);
					transfigureEntity(e, null, null);
				}
			}
		}
		else{
			if (lifeTicks > 160){
				kill = true;
				if (location.getBlock().getType() == Material.STONE){
					location.getBlock().setType(Material.AIR);
					endTransfigure();
				}
			}
			else{
				lifeTicks ++;
				if (location.getBlock().getType() != Material.STONE){
					kill = true;
				}
			}
		}
	}
	
}