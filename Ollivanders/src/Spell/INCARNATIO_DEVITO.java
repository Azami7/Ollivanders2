package Spell;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Effects;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Created by Azami7 on 6/28/17.
 *
 * Turn target player in to a chicken.
 *
 * @see IncarnatioSuper
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public class INCARNATIO_DEVITO extends IncarnatioSuper
{
   public INCARNATIO_DEVITO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      effect = new Effect.INCARNATIO_DEVITO(player, Effects.INCARNATIO_DEVITO, 1);
   }
}