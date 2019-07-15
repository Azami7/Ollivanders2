package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2API;
import net.pottercraft.Ollivanders2.Effect.MUTED_SPEECH;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Silences a player for a duration depending on the spell's level. The target player can only use nonverbal spells.
 *
 * @version Ollivanders2
 * @author Azami7
 */
public final class SILENCIO extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public SILENCIO ()
   {
      super();

      spellType = O2SpellType.SILENCIO;

      flavorText = new ArrayList<String>() {{
         add("The raven continued to open and close its sharp beak, but no sound came out.");
         add("The Silencing Charm");
      }};

      text = "Mutes the target for a time.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public SILENCIO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.SILENCIO;
      setUsesModifier();
   }

   @Override
   protected void doCheckEffect ()
   {
      List<LivingEntity> living = getLivingEntities(1.5);
      for (LivingEntity live : living)
      {
         if (live.getUniqueId() == player.getUniqueId())
            continue;

         if (live instanceof Player)
         {
            Player player = (Player) live;
            int dur = (int) (usesModifier * 1200);

            MUTED_SPEECH effect = new MUTED_SPEECH(p, dur, player.getUniqueId());
            Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

            kill();
            return;
         }
      }

      if (hasHitTarget())
      {
         kill();
      }
   }
}