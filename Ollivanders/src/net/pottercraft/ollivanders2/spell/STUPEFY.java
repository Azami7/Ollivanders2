package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Blinds and slows the target entity for a duration depending on the spell's level.
 *
 * @author lownes
 * @author Azami7
 */
public final class STUPEFY extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public STUPEFY ()
   {
      super();

      spellType = O2SpellType.STUPEFY;

      flavorText = new ArrayList<String>() {{
         add("The Stunning Spell");
         add("\"Stunning is one of the most useful spells in your arsenal. It's sort of a wizard's bread and butter, really.\" -Harry Potter");
      }};

      text = "Stupefy will stun an opponent for a duration.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public STUPEFY (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.STUPEFY;
      setUsesModifier();
   }

   @Override
   public void checkEffect ()
   {
      move();
      List<LivingEntity> entities = getLivingEntities(2);
      for (LivingEntity entity : entities)
      {
         if (entity.getUniqueId() == player.getUniqueId())
            continue;

         int modifier = (int) usesModifier;
         PotionEffect blindness = new PotionEffect(PotionEffectType.BLINDNESS, modifier * 20, modifier);
         PotionEffect slowness = new PotionEffect(PotionEffectType.SLOW, modifier * 20, modifier);
         entity.addPotionEffect(blindness);
         entity.addPotionEffect(slowness);
         kill = true;
      }
   }
}