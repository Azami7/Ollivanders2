package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

/**
 * Gives a target blindness
 *
 * @author lownes
 * @author Azami7
 */
public final class OBSCURO extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public OBSCURO ()
   {
      super();

      spellType = O2SpellType.OBSCURO;

      flavorText = new ArrayList<String>() {{
         add("A black blindfold appeared over Phineas Nigellus' clever, dark eyes, causing him to bump into the frame and shriek with pain.");
      }};

      text = "Obscuro will blind the target.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public OBSCURO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.OBSCURO;
      setUsesModifier();
   }

   @Override
   public void checkEffect ()
   {
      move();
      for (LivingEntity live : getLivingEntities(2))
      {
         PotionEffect blind = new PotionEffect(PotionEffectType.BLINDNESS, (int) (usesModifier * 1200), 0);
         live.addPotionEffect(blind);
         kill();
         return;
      }
   }
}
