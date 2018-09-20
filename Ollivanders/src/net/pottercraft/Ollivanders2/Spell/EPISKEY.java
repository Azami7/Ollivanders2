package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

/**
 * Gives an entity a healing effect for usesModifier seconds
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class EPISKEY extends Healing
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public EPISKEY ()
   {
      super();

      spellType = O2SpellType.EPISKEY;

      flavorText = new ArrayList<String>() {{
         add("\"Episkey,\" said Tonks. Harry's nose felt very hot, then very cold. He raised a hand and felt it gingerly. It seemed to be mended.");
         add("A minor healing spell.");
      }};

      text = "Episkey will heal minor injuries.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public EPISKEY (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.EPISKEY;
      setUsesModifier();
   }

   public void checkEffect ()
   {
      move();
      for (LivingEntity live : getLivingEntities(2))
      {
         live.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (int) (usesModifier * 20), 0), true);
         kill();
         return;
      }
   }
}