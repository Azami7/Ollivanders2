package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

/**
 * Forgetfulness Potion - causes memory loss affecting spell or potion skills.
 *
 * <p>This potion causes the drinker to lose experience with either a randomly selected spell
 * or a randomly selected potion. The amount of skill lost is random (1-20 levels), and there
 * is a 50% chance it will affect spell skills vs. potion skills. The skill loss can significantly
 * impact the player's ability to cast spells or brew potions effectively.</p>
 *
 * <p>If the player has no known spells and the spell skill loss is selected (or vice versa
 * for potions), no effect is applied.</p>
 *
 * @see <a href="http://harrypotter.wikia.com/wiki/Forgetfulness_Potion">Harry Potter Wiki - Forgetfulness Potion</a>
 *
 * @author Azami7
 * @since 2.2.7
 */
public final class FORGETFULNESS_POTION extends O2Potion {
    /**
     * Constructor for Forgetfulness Potion.
     *
     * <p>Initializes the potion with its ingredients (Mistletoe Berries, Valerian Sprigs, Lethe
     * River Water, and Standard Potion Ingredients), description text, flavor text, and potion
     * color. Sets up the recipe for brewing this potion that causes skill loss in the drinker.</p>
     *
     * @param plugin a callback to the plugin instance
     */
    public FORGETFULNESS_POTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.FORGETFULNESS_POTION;

        ingredients.put(O2ItemType.MISTLETOE_BERRIES, 4);
        ingredients.put(O2ItemType.VALERIAN_SPRIGS, 2);
        ingredients.put(O2ItemType.LETHE_RIVER_WATER, 2);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 2);

        text = "The Forgetfulness Potion is a potion which causes a degree of memory loss in the drinker.";
        flavorText.add("Hermione Granger: \"What are the three most crucial ingredients in a Forgetfulness Potion?\"\nRon Weasley: \"I forgot.\"");

        potionColor = Color.fromRGB(195, 71, 0);
    }

    /**
     * Drink the Forgetfulness Potion and suffer skill loss.
     *
     * <p>Causes the player to lose experience with a randomly selected skill:</p>
     * <ul>
     * <li>50% chance: A random known spell's skill count is reduced by 1-20 levels</li>
     * <li>50% chance: A random known potion's skill count is reduced by 1-20 levels</li>
     * </ul>
     *
     * <p>The amount of skill lost is determined randomly (1-20 levels). If the selected skill
     * type (spell or potion) has no known skills, no effect is applied. The skill loss can
     * significantly impact the player's ability to successfully cast spells or brew potions.</p>
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        O2Player o2p = p.getO2Player(player);
        if (o2p == null) {
            player.sendMessage(Ollivanders2.chatColor + "Nothing seems to happen.");
            return;
        }

        int coinToss = Math.abs(Ollivanders2Common.random.nextInt() % 2);

        int memLoss = Math.abs(Ollivanders2Common.random.nextInt() % 20) + 1;

        String lostSkill = "";

        if (coinToss > 0) {
            Map<O2SpellType, Integer> knownSpells = o2p.getKnownSpells();
            if (!knownSpells.isEmpty()) {
                Set<O2SpellType> keySet = knownSpells.keySet();
                ArrayList<O2SpellType> listOfSpells = new ArrayList<>(keySet);
                int index = Math.abs(Ollivanders2Common.random.nextInt() % listOfSpells.size());

                // spell to affect
                O2SpellType spell = listOfSpells.get(index);

                // decrease their skill level
                int curLevel = o2p.getSpellCount(spell);
                o2p.setSpellCount(spell, curLevel - memLoss);
                lostSkill = spell.toString();
            }
        }
        else {
            Map<O2PotionType, Integer> knownPotions = o2p.getKnownPotions();
            if (!knownPotions.isEmpty()) {
                Set<O2PotionType> keySet = knownPotions.keySet();
                ArrayList<O2PotionType> listOfPotions = new ArrayList<>(keySet);
                int index = Math.abs(Ollivanders2Common.random.nextInt() % listOfPotions.size());

                // potion to affect
                O2PotionType potion = listOfPotions.get(index);

                // decrease their skill level
                int curLevel = o2p.getPotionCount(potion);
                o2p.setPotionCount(potion, curLevel - memLoss);
                lostSkill = potion.toString();
            }
        }

        common.printDebugMessage("Forgetfulness Potion: " + player.getName() + " lost " + memLoss + " experience with " + lostSkill, null, null, false);

        player.sendMessage(Ollivanders2.chatColor + "It feels like you've forgotten something.");
    }
}