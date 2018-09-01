package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Effect.O2EffectType;
import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Player.O2Player;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Babbling curse
 *
 * @since 2.2.7
 * @author Azami7
 */
public class LOQUELA_INEPTIAS extends Charms
{
   O2EffectType effect = O2EffectType.BABBLING_EFFECT;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public LOQUELA_INEPTIAS ()
   {
      text = "Causes your target to speak nonsense for a period of time.";
      flavorText.add("\"He usually picked Harry to help him with these reconstructions; so far, Harry had been forced to play a simple Transylvanian villager whom Lockhart had cured of a Babbling Curse, a yeti with a head cold, and a vampire who had been unable to eat anything except lettuce since Lockhart had dealt with him.\"");
      flavorText.add("The Babbling Curse");

      branch = O2MagicBranch.DARK_ARTS;
   }

   /**
    * Constructor
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public LOQUELA_INEPTIAS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      branch = O2MagicBranch.DARK_ARTS;
   }

   @Override
   public void checkEffect ()
   {
      move();
      List<LivingEntity> living = getLivingEntities(2);
      for (LivingEntity live : living)
      {
         if (live instanceof Player)
         {
            Player player = (Player) live;
            O2Player o2p = p.getO2Player(player);
            int dur = (int) (usesModifier * 1200);
            o2p.addEffect(new net.pottercraft.Ollivanders2.Effect.BABBLING_EFFECT(p, O2EffectType.BABBLING_EFFECT, dur, player));
            p.setO2Player(player, o2p);
            kill();
            return;
         }
      }
   }
}
