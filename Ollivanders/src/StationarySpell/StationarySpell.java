package StationarySpell;

import me.cakenggt.Ollivanders.Ollivanders;

/**
 * Interface for stationary spells
 * @author lownes
 *
 */
public interface StationarySpell{
	/**
	 * This is the stationary spell's effect. age() must be called
	 * in this if you want the spell to age and die eventually.
	 * @param p The plugin, so that it can access the list of stationary and projectile spells
	 */
	public void checkEffect(Ollivanders p);
}