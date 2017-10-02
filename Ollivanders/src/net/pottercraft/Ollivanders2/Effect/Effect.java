package net.pottercraft.Ollivanders2.Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

/**
 * Interface for effects
 *
 * @author lownes
 */
public interface Effect
{
   /**
    * This is the effect's action. age() must be called
    * in this if you want the effect to age and die eventually.
    *
    * @param p The plugin, so that it can access the list of stationary and projectile spells
    */
   void checkEffect (Ollivanders2 p, Player owner);
}