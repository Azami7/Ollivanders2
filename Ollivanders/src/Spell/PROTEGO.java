package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.StationarySpells;

public class PROTEGO extends SpellProjectile implements Spell{

	public PROTEGO(Ollivanders2 plugin, Player player, Spells name,
                   Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		StationarySpell.PROTEGO protego = new StationarySpell.PROTEGO(player, location, StationarySpells.PROTEGO, 5, 12000);
		protego.flair(2);
		p.addStationary(protego);
		kill();
	}
	
}