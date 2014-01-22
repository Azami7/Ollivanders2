package Spell;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.Spells;
import me.cakenggt.Ollivanders.Transfiguration;

/**Transfiguration spell for a Dragon. OllivandersPlayerListener.draconiforsBlockChange()
 * keeps any transfigured dragons from destroying terrain.
 * @author lownes
 *
 */
public class DRACONIFORS extends Transfiguration implements Spell{

	public DRACONIFORS(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		simpleTransfigure(EntityType.ENDER_DRAGON, null);
	}
	
}