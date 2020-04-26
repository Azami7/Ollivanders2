package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.pottercraft.ollivanders2.Ollivanders2;

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

      spellType = O2SpellType.BRACKIUM_EMENDO;

      flavorText = new ArrayList<String>() {{
         add("Bone-Healing Spell");
         add("\"Lie back, Harry. It's a simple charm I've used countless times --\" - Gilderoy Lockhard");
         add("As Harry got to his feet, he felt strangely lopsided. Taking a deep breath he looked down at his right side. What he saw nearly made him pass out again. Poking out of the end of his robes was what looked like a thick, fleshcoloured rubber glove. He tried to move his fingers. Nothing happened. Lockhart hadn't mended Harry's bones. He had removed them.");
      }};

      text = "A healing spell when used on a player. When used on a skeleton or wither, it damages them.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public BRACKIUM_EMENDO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.BRACKIUM_EMENDO;
      setUsesModifier();
   }

   public void checkEffect ()
   {
      move();
      List<LivingEntity> entities = getLivingEntities(1.5);
      for (LivingEntity entity : entities)
      {
         if (entity.getUniqueId() == player.getUniqueId())
            continue;

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