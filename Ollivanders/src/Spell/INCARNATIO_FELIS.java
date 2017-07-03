package Spell;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Effects;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Created by Azami7 on 6/28/17.
 *
 * Turn target player in to an ocelot.
 *
 * @see IncarnatioSuper
 * @version Ollivanders2
 * @author lownes
 */
public class INCARNATIO_FELIS extends IncarnatioSuper
{
   public INCARNATIO_FELIS(Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      effect = new Effect.INCARNATIO_FELIS(player, Effects.INCARNATIO_FELIS, 1);
   }
}
