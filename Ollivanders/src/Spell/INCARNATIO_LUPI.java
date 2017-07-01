package Spell;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Effects;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Created by Azami7 on 6/28/17. Imported from iarepandemonium/Ollivanders.
 *
 * @author lownes
 * @author Azami7
 */
public class INCARNATIO_LUPI extends IncarnatioSuper
{
   public INCARNATIO_LUPI(Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      effect = new Effect.INCARNATIO_LUPI(player, Effects.INCARNATIO_LUPI, 1);
   }
}
