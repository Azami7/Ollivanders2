package Spell;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.StationarySpells;

/**
 * Makes an anti-disapparition spell. Players can't apparate out of it.
 * @author lownes
 *
 */
public class NULLUM_EVANESCUNT extends SpellProjectile implements Spell{

	public NULLUM_EVANESCUNT(Ollivanders2 plugin, Player player, Spells name,
                             Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER){
			int duration = (int)(usesModifier*1200);
			StationarySpell.NULLUM_EVANESCUNT nullum = new StationarySpell.NULLUM_EVANESCUNT(player, location, StationarySpells.NULLUM_EVANESCUNT, 5, duration);
			nullum.flair(10);
			p.addStationary(nullum);
			kill();
		}
	}
	
}