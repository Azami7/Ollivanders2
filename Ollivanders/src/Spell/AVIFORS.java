package Spell;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.Spells;
import me.cakenggt.Ollivanders.Transfiguration;

/**Transfigures entity into a bat.
 * @author lownes
 *
 */
public class AVIFORS extends Transfiguration implements Spell{

	public AVIFORS(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		simpleTransfigure(EntityType.BAT, null);
	}
	
}