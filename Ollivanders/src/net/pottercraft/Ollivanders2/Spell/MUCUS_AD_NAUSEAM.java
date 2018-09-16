package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.Ollivanders2.Effect.O2EffectType;
import net.pottercraft.Ollivanders2.Player.O2Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Adds a MUCUS_AD_NAUSEAM oeffect to the player
 *
 * @author lownes
 * @author Azami7
 */
public final class MUCUS_AD_NAUSEAM extends DarkArts
{
   public O2SpellType spellType = O2SpellType.MUCUS_AD_NAUSEAM;

   protected ArrayList<String> flavorText = new ArrayList<String>() {{
      add("The Curse of the Bogies");
   }};

   protected String text = "Mucus Ad Nauseam will cause your opponent to drip with slime.";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public MUCUS_AD_NAUSEAM () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public MUCUS_AD_NAUSEAM (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
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
            o2p.addEffect(new net.pottercraft.Ollivanders2.Effect.MUCUS_AD_NAUSEAM(p, O2EffectType.MUCUS_AD_NAUSEAM, dur, player));
            p.setO2Player(player, o2p);
            kill();
         }
      }
   }
}