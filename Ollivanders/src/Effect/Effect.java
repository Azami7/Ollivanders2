package Effect;

import org.bukkit.entity.Player;

import me.cakenggt.Ollivanders.Ollivanders;

/**
 * Interface for effects
 * @author lownes
 *
 */
public interface Effect{
	/**
	 * This is the effect's action. age() must be called
	 * in this if you want the effect to age and die eventually.
	 * @param p The plugin, so that it can access the list of stationary and projectile spells
	 */
	public void checkEffect(Ollivanders p, Player owner);
}