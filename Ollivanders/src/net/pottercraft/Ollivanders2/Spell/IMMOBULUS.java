package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
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
   public O2SpellType spellType = O2SpellType.IMMOBULUS;

   protected ArrayList<String> flavorText = new ArrayList<String>() {{
      add("The Freezing Charm");
      add("\"[…] immobilising two pixies at once with a clever Freezing Charm and stuffing them back into their cage.\"");
      add("The Freezing Charm is a spell which immobilises living targets.");
   }};

   protected String text = "Renders entry unable to move for a time period.";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public IMMOBULUS () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public IMMOBULUS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
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