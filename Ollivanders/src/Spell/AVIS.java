package Spell;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.Transfiguration;

/**
 * Transfigures bats from air
 * @author lownes
 *
 */
public class AVIS extends Transfiguration implements Spell{

	public AVIS(Ollivanders2 plugin, Player player, Spells name,
                Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		if (!hasTransfigured()){
			move();
			transfigureEntity(null, EntityType.BAT, null);
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