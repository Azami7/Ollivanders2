package Spell;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.Transfiguration;

public class DUCKLIFORS extends Transfiguration implements Spell{

	public DUCKLIFORS(Ollivanders2 plugin, Player player, Spells name,
                      Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		simpleTransfigure(EntityType.CHICKEN, null);
	}
	
}