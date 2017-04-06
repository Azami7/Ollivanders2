package Spell;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.StationarySpells;

public class PROTEGO_HORRIBILIS extends SpellProjectile implements Spell{

	public PROTEGO_HORRIBILIS(Ollivanders2 plugin, Player player, Spells name,
                              Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER){
			int duration = (int)(usesModifier*1200);
			StationarySpell.PROTEGO_HORRIBILIS total = new StationarySpell.PROTEGO_HORRIBILIS(player, location, StationarySpells.PROTEGO_HORRIBILIS, 5, duration);
			total.flair(10);
			p.addStationary(total);
			kill();
		}
	}
	
}