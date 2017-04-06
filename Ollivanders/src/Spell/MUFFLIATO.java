package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.StationarySpells;

/**
 * Creates a muffliato stationary spell object. Only players within that
 * object can hear other players within it. Time duration depends on spell's
 * level.
 * @author lownes
 *
 */
public class MUFFLIATO extends SpellProjectile implements Spell{

	public MUFFLIATO(Ollivanders2 plugin, Player player, Spells name,
                     Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		if (super.getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER){
			int duration = (int)usesModifier*1200;
			StationarySpell.MUFFLIATO muffliato = new StationarySpell.MUFFLIATO(player, location, StationarySpells.MUFFLIATO, 5, duration);
			muffliato.flair(20);
			p.addStationary(muffliato);
			kill();
		}
	}
	
}