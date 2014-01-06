package Spell;

import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.Spells;
import me.cakenggt.Ollivanders.Transfiguration;

/**
 * Vanishes an entity. The entity will reappear after a certain time.
 * @author lownes
 *
 */
public class EVANESCO extends Transfiguration implements Spell{

	public EVANESCO(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		simpleTransfigure(null, null);
	}
	
}