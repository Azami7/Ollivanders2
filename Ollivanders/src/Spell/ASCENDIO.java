package Spell;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.SpellProjectile;
import me.cakenggt.Ollivanders.Spells;

/**Shoots caster high into air
 * @author lownes
 *
 */
public class ASCENDIO extends SpellProjectile implements Spell{

	public ASCENDIO(Ollivanders plugin, Player player, Spells name,
			Double rightWand) {
		super(plugin, player, name, rightWand);
	}

	public void checkEffect() {
		double up = usesModifier*0.8;
		if (up > 4){
			up = 4;
		}
		Vector vec = new Vector(0, up, 0);
		player.setVelocity(player.getVelocity().add(vec));
		kill();
	}
	
}