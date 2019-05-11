package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

/**
 * Bubble head charm gives the player water breathing for a length of time depending on the player's spell level.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class EBUBLIO extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public EBUBLIO ()
   {
      super();

      spellType = O2SpellType.EBUBLIO;

      flavorText = new ArrayList<String>() {{
         add("The Bubble-Head Charm");
         add("Fleur Delacour, though she demonstrated excellent use of the Bubble-Head Charm, was attacked by grindylows as she approached her goal, and failed to retrieve her hostage.");
         add("Cedric Diggory, who also used the Bubble-Head Charm, was first to return with his hostage, though he returned one minute outside the time limit of an hour.");
      }};

      text = "Gives target player the ability to breathe underwater.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public EBUBLIO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.EBUBLIO;

      // set up usage modifier, has to be done here to get the uses for this specific spell
      setUsesModifier();
   }

   /**
    * Give the caster water-breathing
    */
   @Override
   public void checkEffect ()
   {
      player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, (int) (usesModifier * 1200), 1), true);

      kill();
   }
}