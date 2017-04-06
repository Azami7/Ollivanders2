package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.Transfiguration;

/**Transfigures into a silverfish or turns a block into a monster egg
 * @author lownes
 *
 */
public class ENTOMORPHIS extends Transfiguration implements Spell{

	public ENTOMORPHIS(Ollivanders2 plugin, Player player, Spells name,
                       Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	@SuppressWarnings("deprecation")
	public void checkEffect() {
		if (!hasTransfigured()){
			move();
			for (Entity entity : getCloseEntities(1)){
				transfigureEntity(entity, EntityType.SILVERFISH, null);
				return;
			}
			Block block = getBlock();
			Material type = block.getType();
			if (type == Material.COBBLESTONE || type == Material.STONE || type == Material.SMOOTH_BRICK){
				block.setType(Material.MONSTER_EGGS);
				if (type == Material.STONE){block.setData((byte) 0);}
				else if (type == Material.COBBLESTONE){block.setData((byte) 1);}
				else if (type == Material.SMOOTH_BRICK){block.setData((byte) (block.getData()+2));}
				kill();
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