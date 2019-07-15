package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public abstract class RemovePotionEffectFromSelfSuper extends Charms
{
   /**
    * The potion effect. Set to luck by default.
    */
   ArrayList<PotionEffectType> potionEffectTypes = new ArrayList<>();

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public RemovePotionEffectFromSelfSuper ()
   {
      super();
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public RemovePotionEffectFromSelfSuper (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
   }

   /**
    * Remove the potion effect from the caster.
    */
   @Override
   public void checkEffect ()
   {
      if (!checkSpellAllowed())
      {
         kill();
         return;
      }

      for (PotionEffectType effectType : potionEffectTypes)
      {
         player.removePotionEffect(effectType);
      }

      kill();
   }
}
