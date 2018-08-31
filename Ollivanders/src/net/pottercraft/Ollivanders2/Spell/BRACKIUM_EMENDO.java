package net.pottercraft.Ollivanders2.Spell;

import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Healing spell that can be used to mend broken bones.  Can also be used to remove bones and can be used defensively
 * against skeletons and withers.
 *
 * @author lownes
 * @author Azami7
 */
public final class BRACKIUM_EMENDO extends Healing
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public BRACKIUM_EMENDO ()
   {
      super();

      flavorText.add("Bone-Healing Spell");
      flavorText.add("As Harry got to his feet, he felt strangely lopsided. Taking a deep breath he looked down at his right side. What he saw nearly made him pass out again. Poking out of the end of his robes was what looked like a thick, fleshcoloured rubber glove. He tried to move his fingers. Nothing happened. Lockhart hadn't mended Harry's bones. He had removed them.");
      text = "A healing spell when used on a player.  When used on a skeleton or wither, it damages them.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public BRACKIUM_EMENDO (Ollivanders2 plugin, Player player, O2SpellType name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      List<LivingEntity> entities = getLivingEntities(2);
      for (LivingEntity entity : entities)
      {
         EntityType type = entity.getType();
         if (type == EntityType.SKELETON || type == EntityType.WITHER_SKULL || type == EntityType.WITHER)
         {
            entity.damage(usesModifier * 2, player);
            kill();
            return;
         }
         else if (type == EntityType.PLAYER)
         {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, (int) (usesModifier * 1200), 1), true);
            kill();
         }
      }
   }
}