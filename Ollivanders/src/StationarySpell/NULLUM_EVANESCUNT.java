package StationarySpell;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.StationarySpellObj;
import me.cakenggt.Ollivanders.StationarySpells;

/**
 * Anti-disapparition spell. Players can't apparate out of it.
 * @author lownes
 *
 */
public class NULLUM_EVANESCUNT extends StationarySpellObj implements StationarySpell{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2882828727179919497L;

	public NULLUM_EVANESCUNT(Player player, Location location,
			StationarySpells name, Integer radius, Integer duration) {
		super(player, location, name, radius, duration);
	}

	public void checkEffect(Ollivanders p) {
		age();
	}
	
}