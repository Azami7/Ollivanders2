package Spell;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Spell which places a block of water against the targeted block.
 * @author lownes
 *
 */
public class AGUAMENTI extends SpellProjectile implements Spell{

	public AGUAMENTI(Ollivanders p, Player player, Spells name, Integer rightWand){
		super(p, player, name, rightWand);
	}

	public void checkEffect() {
		move();
		if (getBlock().getType() != Material.AIR){
			location.subtract(super.vector).getBlock().setType(Material.WATER);
		}
	}
}