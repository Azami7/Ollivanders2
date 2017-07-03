package Spell;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Effects;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Created by Azami7 on 6/28/17.
 *
 * Turn target player in to a llama.
 *
 * @see IncarnatioSuper
 * @author Azami7
 */
public class INCARNATIO_LAMA extends IncarnatioSuper
{
   public INCARNATIO_LAMA(Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      effect = new Effect.INCARNATIO_LAMA(player, Effects.INCARNATIO_LAMA, 1);
   }
}
