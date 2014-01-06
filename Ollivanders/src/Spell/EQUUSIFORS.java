package Spell;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.Spells;
import me.cakenggt.Ollivanders.Transfiguration;

/**
 * Transfigures an item into a horse.
 * @author lownes
 *
 */
public class EQUUSIFORS extends Transfiguration implements Spell{

	public EQUUSIFORS(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		simpleTransfigure(EntityType.HORSE, null);
	}
	
}