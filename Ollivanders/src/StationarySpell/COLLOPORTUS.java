package StationarySpell;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.StationarySpellObj;
import me.cakenggt.Ollivanders.StationarySpells;

/**Prevents any block events in an area
 * @author lownes
 *
 */
public class COLLOPORTUS extends StationarySpellObj implements StationarySpell{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7170355497305049461L;

	public COLLOPORTUS(Player player, Location location, StationarySpells name,
			Integer radius, Integer duration) {
		super(player, location, name, radius, duration);
	}

	public void checkEffect(Ollivanders p) {
		
	}
	
}