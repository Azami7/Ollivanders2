package net.pottercraft.Ollivanders2.Spell;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Does direct damage to a living entity according to your level in the spell.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class AVADA_KEDAVRA extends DarkArts
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public AVADA_KEDAVRA ()
   {
      super();

      flavorText.add("The Killing Curse");
      flavorText.add("There was a flash of blinding green light and a rushing sound, as though a vast, invisible something was soaring through the air — instantaneously the spider rolled over onto its back, unmarked, but unmistakably dead");
      flavorText.add("\"Yes, the last and worst. Avada Kedavra. ...the Killing Curse.\" -Bartemius Crouch Jr (disguised as Alastor Moody)");
      text = "Cause direct damage to a living thing, possibly killing it.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public AVADA_KEDAVRA (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      moveEffectData = Material.MELON_BLOCK;
   }

   public void checkEffect ()
   {
      move();
      List<LivingEntity> entities = getLivingEntities(1);
      if (entities.size() > 0)
      {
         LivingEntity entity = entities.get(0);
         entity.damage(usesModifier * 2, player);
         kill = true;
         return;
      }
   }
}