package net.pottercraft.ollivanders2.spell;

import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;

import net.pottercraft.ollivanders2.Ollivanders2;

import java.util.ArrayList;

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

      spellType = O2SpellType.LACARNUM_INFLAMARI;

      flavorText = new ArrayList<String>() {{
         add("Some of the new incantations, such as ‘lacarnum inflamari’ must have sounded more dramatic onscreen – although by the time you’ve managed to say ‘lacarnum inflamari’, you’ve surely lost precious seconds in which the Devil’s Snare might have throttled you. But that’s showbiz.");
         add("She whipped out her wand, waved it, muttered something, and sent a jet of the same bluebell flames she had used on Snape at the plant. In a matter of seconds, the two boys felt it loosening its grip as it cringed away from the light and warmth.");
         add("Bluebell Flames");
         add("Cold Flames");
      }};

      text = "Lacarnum Inflamarae will shoot a fire charge out of the tip of your wand. This fire charge is not a spell, and thus can pass through normal anti-spell barriers.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public LACARNUM_INFLAMARI (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.LACARNUM_INFLAMARI;
      setUsesModifier();
   }

   @Override
   public void checkEffect ()
   {
      move();
      kill();
      player.launchProjectile(SmallFireball.class, vector);
   }
}
