package net.pottercraft.Ollivanders2.Spell;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Immobilizes a player for an amount of time depending on the player's spell level.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class IMMOBULUS extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public IMMOBULUS (O2SpellType type)
   {
      super(type);

      flavorText.add("The Freezing Charm");
      flavorText.add("\"[…] immobilising two pixies at once with a clever Freezing Charm and stuffing them back into their cage.\"");
      flavorText.add("The Freezing Charm is a spell which immobilises living targets.");
      text = "Renders entry unable to move for a time period.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param type
    * @param rightWand
    */
   public IMMOBULUS (Ollivanders2 plugin, Player player, O2SpellType type, Double rightWand)
   {
      super(plugin, player, type, rightWand);
   }

   public void checkEffect ()
   {
      move();
      List<LivingEntity> entities = getLivingEntities(2);
      for (LivingEntity entity : entities)
      {
         int modifier = (int) usesModifier;
         PotionEffect slow = new PotionEffect(PotionEffectType.SLOW, modifier * 20, 10);
         entity.addPotionEffect(slow);
         kill();
      }
   }
}