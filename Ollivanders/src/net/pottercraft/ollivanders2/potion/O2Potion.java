package net.pottercraft.ollivanders2.potion;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;

import net.pottercraft.ollivanders2.common.MagicLevel;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.O2MagicBranch;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Ollivanders2 magical potion.
 *
 * <p>O2Potions can have either or both of the following types of effects:<br>
 * PotionEffect - this is a standard Minecraft potion effect such as Night Vision and is set in the item metadata in the brew().<br>
 * Ollivanders Effect - effects related to the mechanics of the Ollivanders plugin. These are applied on the potion
 * drink action in OllivandersListener in the onPlayerDrink().</p>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Potion">Harry Potter Wiki - Potion</a>
 */
public abstract class O2Potion {
    /**
     * Reference to the plugin
     */
    Ollivanders2 p;

    /**
     * Common functions
     */
    Ollivanders2Common common;

    /**
     * The type this potion is.
     */
    protected O2PotionType potionType;

    /**
     * The ingredients list for this potion.
     */
    protected Map<O2ItemType, Integer> ingredients = new HashMap<>();

    /**
     * The description text for this potion in books.  Required or potion cannot be written in a book.
     */
    protected String text = "";

    /**
     * Flavor text for this potion in books, etc.  Optional.
     */
    protected ArrayList<String> flavorText = new ArrayList<>();

    /**
     * Color of this potion.
     */
    protected Color potionColor = Color.PURPLE;

    /**
     * The PotionEffect for this potion
     */
    PotionEffect minecraftPotionEffect = null;

    /**
     * The type of potion this is
     */
    Material potionMaterialType = Material.POTION;

    /**
     * The duration for this potion
     */
    protected int duration = 3600;

    /**
     * The modifier for this potion based on usage.
     */
    public double usesModifier = 1;

    /**
     * The success message to send when this potion is consumed
     */
    protected String potionSuccessMessage = "";

    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public O2Potion(@NotNull Ollivanders2 plugin) {
        p = plugin;
        potionType = O2PotionType.BABBLING_BEVERAGE;
        common = new Ollivanders2Common(p);
    }

    /**
     * Get the ingredients text for this potion
     *
     * @return the recipe text for this ingredient
     */
    @NotNull
    protected String getIngredientsText() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n\nIngredients:");

        for (Entry<O2ItemType, Integer> e : ingredients.entrySet()) {
            O2ItemType ingredientType = e.getKey();
            String name = Ollivanders2API.getItems().getItemNameByType(ingredientType);

            stringBuilder.append("\n").append(e.getValue().toString()).append(" ").append(name);
        }

        return stringBuilder.toString();
    }

    /**
     * Get the type of this potion.
     *
     * @return the type of potion
     */
    @NotNull
    public O2PotionType getPotionType() {
        return potionType;
    }

    /**
     * Get the name of this potion
     *
     * @return the name of the potion
     */
    @NotNull
    public String getName() {
        return potionType.getPotionName();
    }

    /**
     * Get the ingredients list for this potion
     *
     * @return a Map of the ingredients for this potion
     */
    @NotNull
    public Map<O2ItemType, Integer> getIngredients() {
        HashMap<O2ItemType, Integer> ingredientsCopy = new HashMap<>();
        ingredientsCopy.putAll(ingredients);

        return ingredientsCopy;
    }

    /**
     * Get the description text for this potion.  This can be used to write books, for lessons, or other in-game messages.
     * Description text is required for adding a spell to an Ollivanders2 book.
     *
     * @return the description text for this potion
     */
    @NotNull
    public String getText() {
        return text + getIngredientsText();
    }

    /**
     * Get the flavor text for this potion.  This can be used to make books, lessons, and other descriptions of spells more interesting.
     * Flavor text is optional.
     *
     * @return the flavor text for this potion.
     */
    @Nullable
    public String getFlavorText() {
        if (flavorText.isEmpty())
            return null;
        else {
            int index = Math.abs(Ollivanders2Common.random.nextInt() % flavorText.size());
            return flavorText.get(index);
        }
    }

    /**
     * Get the branch of magic for this potion
     *
     * @return the branch of magic for this potion
     */
    @NotNull
    public O2MagicBranch getMagicBranch() {
        return O2MagicBranch.POTIONS;
    }

    /**
     * Get the level of magic for this potion
     *
     * @return the level of magic for this potion
     */
    @NotNull
    public MagicLevel getLevel() {
        return potionType.getLevel();
    }

    /**
     * Get the potion success message used in drink()
     *
     * @return the potion success message
     */
    @NotNull
    public String getPotionSuccessMessage() {
        return potionSuccessMessage;
    }

    /**
     * Check the recipe for this potion against a set of ingredients.
     *
     * @param cauldronIngredients the ingredients found in the cauldron
     * @return true if the ingredient list matches this potion recipe exactly, false otherwise
     */
    public boolean checkRecipe(@NotNull Map<O2ItemType, Integer> cauldronIngredients) {
        common.printDebugMessage("Checking " + potionType.getPotionName() + " recipe", null, null, false);

        // are there the right number of ingredients?
        if (ingredients.size() != cauldronIngredients.size()) {
            common.printDebugMessage("   expected " + ingredients.size() + " ingredients, got " + cauldronIngredients.size(), null, null, false);
            return false;
        }

        for (Map.Entry<O2ItemType, Integer> e : ingredients.entrySet()) {
            O2ItemType ingredientType = e.getKey();
            Integer count = e.getValue();

            // is this ingredient in the recipe?
            if (!cauldronIngredients.containsKey(ingredientType)) {
                common.printDebugMessage("   cauldron does not contain " + ingredientType.getName(), null, null, false);
                return false;
            }

            // is the amount of the ingredient correct?
            if (cauldronIngredients.get(ingredientType).intValue() != count.intValue()) {
                common.printDebugMessage("   recipe needs " + count + " " + ingredientType.getName() + ", got " + cauldronIngredients.get(ingredientType), null, null, false);
                return false;
            }
        }

        common.printDebugMessage("   matches", null, null, false);
        return true;
    }

    /**
     * Brew this potion.
     *
     * @param brewer       the player brewing the potion
     * @param checkCanBrew should we enforce checking if the brewer can brew or not
     * @return an ItemStack with a single bottle of this potion
     */
    @NotNull
    public ItemStack brew(@NotNull Player brewer, boolean checkCanBrew) {
        ItemStack potion;

        if (checkCanBrew && !canBrew(brewer)) {
            brewer.sendMessage(Ollivanders2.chatColor + "You feel uncertain about how to make this potion.");
            potion = brewBadPotion();
        }
        else
            potion = createPotionItemStack(1);

        p.getO2Player(brewer).incrementPotionCount(potionType);

        return potion;
    }

    /**
     * Create an ItemStack for this potion with the specified amount.
     * <p>
     * Sets up the potion metadata including display name, color, custom effects,
     * and NBT tags for potion type identification.
     * </p>
     *
     * @param amount the number of potions in the ItemStack
     * @return an ItemStack containing the potion, or a bad potion if metadata creation fails
     */
    public ItemStack createPotionItemStack(int amount) {
        ItemStack potion = new ItemStack(potionMaterialType);
        potion.setAmount(amount);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();

        if (meta == null) {
            common.printDebugMessage("O2Potion.brew: item meta is null", null, null, true);
            potion = brewBadPotion();
        }
        else {
            meta.setDisplayName(potionType.getPotionName());
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(O2Potions.potionTypeKey, PersistentDataType.STRING, potionType.getPotionName());

            meta.setColor(potionColor);
            if (minecraftPotionEffect != null)
                meta.addCustomEffect(minecraftPotionEffect, true);

            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            potion.setItemMeta(meta);
        }

        return potion;
    }

    /**
     * Determine if a player can successfully brew this potion.
     * <p>
     * This takes in to consideration whether book learning is enabled, the player's experience with the potion, and the
     * difficulty of the potion.
     *
     * @param brewer the player brewing the potion
     * @return true if the player will be successful, false otherwise.
     */
    private boolean canBrew(@NotNull Player brewer) {
        if (p.getConfig().isSet("overrideBrewSuccessCheck") && p.getConfig().getBoolean("overrideBrewSuccessCheck"))
            return true;

        // When maxSpellLevel is on, potions are always successful
        if (Ollivanders2.maxSpellLevel)
            return true;

        boolean canBrew;

        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(brewer.getUniqueId());
        if (o2p == null) {
            common.printDebugMessage("O2Potion.canBrew: failed to find O2Player", null, null, true);
            return false;
        }

        int potionCount = o2p.getPotionCount(potionType);

        // do not allow them to brew if book learning is turned on and the brewer does not know this potion
        if (Ollivanders2.bookLearning && (potionCount < 1))
            canBrew = false;
        else {
            setUsesModifier(o2p);

            int rand = (Math.abs(Ollivanders2Common.random.nextInt()) % 100) + 1;
            canBrew = (usesModifier > rand);
        }

        return canBrew;
    }

    /**
     * Brew a random bad potion.
     *
     * @return a random bad potion
     */
    @NotNull
    public static ItemStack brewBadPotion() {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();

        if (meta == null)
            return potion;

        int rand = Math.abs(Ollivanders2Common.random.nextInt() % 10);
        String name = "Watery Potion";
        Color color = Color.BLUE;

        if (rand == 9) {
            name = "Slimy Potion";
            color = Color.GREEN;
        }
        else if (rand == 8) {
            name = "Lumpy Potion";
            color = Color.GRAY;
        }
        else if (rand == 7) {
            name = "Cloudy Potion";
            color = Color.WHITE;
        }
        else if (rand == 6) {
            name = "Smelly Potion";
            color = Color.ORANGE;
        }
        else if (rand == 5) {
            name = "Sticky Potion";
            color = Color.OLIVE;
        }
        // else rand < 5, use "Watery Potion" and Color.BLUE

        meta.setDisplayName(name);
        meta.setColor(color);

        rand = Math.abs(Ollivanders2Common.random.nextInt() % 10);

        int duration = 1200;
        int amplifier = 1;

        if (rand >= 5) {
            PotionEffect e;

            if (rand == 9)
                e = new PotionEffect(PotionEffectType.SLOWNESS, duration, amplifier);
            else if (rand == 8)
                e = new PotionEffect(PotionEffectType.BLINDNESS, duration, amplifier);
            else if (rand == 7)
                e = new PotionEffect(PotionEffectType.HUNGER, duration, amplifier);
            else if (rand == 6)
                e = new PotionEffect(PotionEffectType.NAUSEA, duration, amplifier);
            else
                e = new PotionEffect(PotionEffectType.WEAKNESS, duration, amplifier);
            meta.addCustomEffect(e, true);
        }
        // rand < 5, no effect

        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        potion.setItemMeta(meta);

        return potion;
    }

    /**
     * Drink this potion and do effects
     *
     * @param player the player who drank the potion
     */
    public abstract void drink(@NotNull Player player);

    /**
     * Sets the uses modifier that takes into account potion brew count and level, if years is enabled.
     *
     * @param o2p the player who is brewing the potion
     */
    protected void setUsesModifier(@NotNull O2Player o2p) {
        // if max skill is set, set usesModifier to max level
        if (Ollivanders2.maxSpellLevel)
            usesModifier = 200;
            // uses modifier is the number of times the spell has been cast
        else {
            usesModifier = o2p.getPotionCount(potionType);

            // if the caster is affected by HIGHER_SKILL, double their usesModifier
            if (Ollivanders2API.getPlayers().playerEffects.hasEffect(o2p.getID(), O2EffectType.HIGHER_SKILL))
                usesModifier *= 2;
        }

        // if years is enabled, potion brewing is affected by brewer's level
        if (Ollivanders2.useYears) {
            MagicLevel maxLevelForPlayer = o2p.getYear().getHighestLevelForYear();

            if (maxLevelForPlayer.ordinal() > (potionType.getLevel().ordinal() + 1))
                // double skill level when 2 or more levels above
                usesModifier *= 2;
            else if (maxLevelForPlayer.ordinal() > potionType.getLevel().ordinal())
                // 50% skill increase when 1 level above
                usesModifier *= 1.5;
                /*
                    maxLevelForPlayer.ordinal() == potionType.getLevel().ordinal())
                    no change to usesModifier
                 */
            else if ((maxLevelForPlayer.ordinal() + 1) < potionType.getLevel().ordinal())
                // 25% skill when 2 or more levels below
                usesModifier *= 0.25;
            else if (maxLevelForPlayer.ordinal() < potionType.getLevel().ordinal())
                // half skill when 1 level below
                usesModifier *= 0.5;
        }
    }
}