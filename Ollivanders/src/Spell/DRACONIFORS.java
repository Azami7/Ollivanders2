package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.Transfiguration;

/**Transfiguration spell for a Dragon. OllivandersPlayerListener.draconiforsBlockChange()
 * keeps any transfigured dragons from destroying terrain.
 * @author lownes
 *
 */
public class DRACONIFORS extends Transfiguration implements Spell{

	public DRACONIFORS(Ollivanders2 plugin, Player player, Spells name,
                       Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		simpleTransfigure(EntityType.ENDER_DRAGON, null);
	}
	
}