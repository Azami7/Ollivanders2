package StationarySpell;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.StationarySpellObj;
import me.cakenggt.Ollivanders.StationarySpells;

/**
 * Negates fall damage.
 * @author lownes
 *
 */
public class SPONGIFY extends StationarySpellObj implements StationarySpell{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5874904743113396382L;

	public SPONGIFY(Player player, Location location, StationarySpells name,
			Integer radius, Integer duration) {
		super(player, location, name, radius, duration);
	}

	public void checkEffect(Ollivanders p) {
		age();
		/*
		if (getBlock().getType() == Material.REDSTONE_WIRE){
			if (duration > 1){
				getBlock().setData((byte)15, false);
			}
			else{
				getBlock().setData((byte)0, true);
			}
		}
		*/
	}
	
}