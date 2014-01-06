package Spell;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.Spells;
import me.cakenggt.Ollivanders.Transfiguration;

/**
 * Produces a second item or livingEntity exactly like the first item
 * @author lownes
 *
 */
public class GEMINIO extends Transfiguration implements Spell{

	public GEMINIO(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		if (!hasTransfigured()){
			move();
			for (Entity e : player.getWorld().getEntities()){
				if (e.getType() != EntityType.PLAYER && e.getLocation().distance(location)<1 && !(e instanceof LivingEntity)){
					if (Math.random() < usesModifier/10){
						if (e.getType() == EntityType.DROPPED_ITEM){
							transfigureEntity(null, EntityType.DROPPED_ITEM, ((Item)e).getItemStack());
						}
						else{
							transfigureEntity(null, e.getType(), null);
						}
						return;
					}
					else{
						kill = true;
					}
				}
			}
		}
		else{
			if (lifeTicks > 160){
				kill = true;
				endTransfigure();
			}
			else{
				lifeTicks ++;
			}
		}
	}

}