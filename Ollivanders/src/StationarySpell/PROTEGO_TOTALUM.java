package StationarySpell;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;
import me.cakenggt.Ollivanders.StationarySpellObj;
import me.cakenggt.Ollivanders.StationarySpells;

/**
 * Doesn't let entities pass the boundary.
 * @author lownes
 *
 */
public class PROTEGO_TOTALUM extends StationarySpellObj implements StationarySpell{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5737434601454767984L;

	public PROTEGO_TOTALUM(Player player, Location location,
			StationarySpells name, Integer radius, Integer duration) {
		super(player, location, name, radius, duration);
	}

	public void checkEffect(Ollivanders p) {
		age();
		for (Entity entity : Bukkit.getServer().getWorld(location.getWorld()).getEntities()){
			if (!(entity instanceof Player)) {
				if (entity.getLocation().distance(location.toLocation()) < radius + 0.5
						&& entity.getLocation().distance(location.toLocation()) > radius - 0.5) {
					Location spellLoc = location.toLocation();
					Location eLoc = entity.getLocation();
					double distance = eLoc.distance(spellLoc);
					if (distance > radius - 0.5) {
						entity.setVelocity(eLoc.toVector()
								.subtract(spellLoc.toVector()).normalize()
								.multiply(0.5));
						flair(10);
					} else if (distance < radius + 0.5) {
						entity.setVelocity(spellLoc.toVector()
								.subtract(eLoc.toVector()).normalize()
								.multiply(0.5));
						flair(10);
					}
				}
			}
		}
	}
	
}