package net.pottercraft.Ollivanders2.Spell;

/**
 * Interface for all spells
 *
 * @author lownes
 */
public interface Spell
{
   /**
    * This is the spell's effect. move() must be called in this if you want the projectile to move.
    */
   public void checkEffect ();
}