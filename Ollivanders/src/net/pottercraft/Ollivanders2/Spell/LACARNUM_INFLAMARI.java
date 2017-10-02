package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Shoots out a SmallFireball projectile.
 *
 * @author lownes
 * @author Azami7
 */
public final class LACARNUM_INFLAMARI extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public LACARNUM_INFLAMARI ()
   {
      super();

      flavorText.add("Some of the new incantations, such as ‘lacarnum inflamari’ must have sounded more dramatic onscreen – although by the time you’ve managed to say ‘lacarnum inflamari’, you’ve surely lost precious seconds in which the Devil’s Snare might have throttled you. But that’s showbiz.");
      flavorText.add("She whipped out her wand, waved it, muttered something, and sent a jet of the same bluebell flames she had used on Snape at the plant. In a matter of seconds, the two boys felt it loosening its grip as it cringed away from the light and warmth.");
      flavorText.add("Bluebell Flames");
      flavorText.add("Cold Flames");
      text = "Lacarnum Inflamarae will shoot a fire charge out of the tip of your wand. This fire charge is not a spell, and thus can pass through normal anti-spell barriers.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public LACARNUM_INFLAMARI (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      move();
      kill();
      player.launchProjectile(SmallFireball.class, vector);
   }
}
