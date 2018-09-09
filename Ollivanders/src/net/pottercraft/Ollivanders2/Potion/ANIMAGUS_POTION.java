package net.pottercraft.Ollivanders2.Potion;

import net.pottercraft.Ollivanders2.Effect.ANIMAGUS_EFFECT;
import net.pottercraft.Ollivanders2.Effect.O2EffectType;
import net.pottercraft.Ollivanders2.Player.O2Player;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

/**
 * Consumed after successfully casting the Animagus incantation, this will turn a player in to an Animagus.
 *
 * @author Azami7
 */
public final class ANIMAGUS_POTION extends O2Potion
{
   public ANIMAGUS_POTION (Ollivanders2 plugin, O2PotionType potionType)
   {

      super(plugin, potionType);

      ingredients.put(IngredientType.MANDRAKE_LEAF, 1);
      ingredients.put(IngredientType.DEW_DROP, 2);
      ingredients.put(IngredientType.DEATHS_HEAD_MOTH_CHRYSALIS, 1);
      ingredients.put(IngredientType.STANDARD_POTION_INGREDIENT, 3);

      name = "Animagus Potion";
      text = "An Animagus is a wizard who elects to turn into an animal. This potion, if brewed and consumed correctly, " +
            "will disguisePlayer the drinker in to their animal form. Thereafter, the Animagus can disguisePlayer without the " +
            "potion, however it will take considerable practice to change forms consistently at will.";
      flavorText.add("\"You know that I can disguise myself most effectively.\" -Peter Pettigrew");
      flavorText.add("\"Normally, I have a very sweet disposition as a dog; in fact, more than once, James suggested I make the change permanent. The tail I could live with...but the fleas, they're murder.\" -Sirius Black");

      potionColor = Color.fromRGB(102, 0, 0);
   }

   @Override
   public void drink (O2Player o2p, Player player)
   {
      if (o2p.isAnimagus())
      {
         // they are already an Animagus so this has no effect
         return;
      }

      if (!Ollivanders2.libsDisguisesEnabled || !player.getWorld().isThundering())
      {
         // potion only works in a thunderstorm
         player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor"))
               + "Nothing seems to happen.");
         return;
      }

      if (p.players.playerEffects.hasEffect(o2p.getID(), O2EffectType.ANIMAGUS_INCANTATION))
      {
         o2p.setIsAnimagus();

         ANIMAGUS_EFFECT animagusEffect = new ANIMAGUS_EFFECT(p, O2EffectType.ANIMAGUS_EFFECT, 5, player.getUniqueId());
         p.players.playerEffects.addEffect(animagusEffect);

         player.sendMessage(Ollivanders2.chatColor + "You feel transformed.");
      }
   }
}