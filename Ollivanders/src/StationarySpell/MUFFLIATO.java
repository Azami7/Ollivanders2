package StationarySpell;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.StationarySpellObj;
import me.cakenggt.Ollivanders.StationarySpells;

/**
 * Only players within this can hear other conversation from other
 * players within. Duration depending on spell's level.
 * @author lownes
 *
 */
public class MUFFLIATO extends StationarySpellObj implements StationarySpell{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4767315283996504871L;

	public MUFFLIATO(Player player, Location location, StationarySpells name,
			Integer radius, Integer duration) {
		super(player, location, name, radius, duration);
	}

	public void checkEffect(Ollivanders p) {
		age();
	}
	
}