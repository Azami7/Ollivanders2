package net.pottercraft.ollivanders2.book;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.pottercraft.ollivanders2.book.events.OllivandersBookLearningPotionEvent;
import net.pottercraft.ollivanders2.book.events.OllivandersBookLearningSpellEvent;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.player.O2Player;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * All magic books.
 *
 * <p>When bookLearning is enabled, reading a book will increase the reader's spell level by 1 to a maximum of 10. This can
 * be used for classes, creating lessons, or other in-game magic learning.</p>
 *
 * <p>When bookLEarning is enabled, every Ollivanders2 spell must be in a WrittenBook with lore or NBT set up correctly
 * or players will not be able to learn them.</p>
 *
 * @see <a href = "https://github.com/Azami7/Ollivanders2/wiki/Configuration#book-learning">https://github.com/Azami7/Ollivanders2/wiki/Configuration#book-learning</a>
 */
public final class O2Books implements Listener {
    /**
     * Collection of all the books available by title
     */
    private final Map<String, O2BookType> O2BookMap = new HashMap<>();

    /**
     * Reference to the plugin
     */
    Ollivanders2 p;

    /**
     * Common functions
     */
    Ollivanders2Common common;

    /**
     * The book text for each spell and potion
     */
    BookTexts spellText;

    /**
     * Constructor
     *
     * @param plugin the MC plugin
     */
    public O2Books(@NotNull Ollivanders2 plugin) {
        p = plugin;
        common = new Ollivanders2Common(plugin);

        spellText = new BookTexts(plugin);

        p.getServer().getPluginManager().registerEvents(this, p);
    }

    /**
     * Load all books on plugin enable.
     * <p>
     * This needs to be run after spells and potions are loaded or the text for these will not be loaded.
     */
    public void onEnable() {
        // in case the plugin was disabled and then re-enabled, reset the book map
        O2BookMap.clear();

        // add all books
        addBooks();
        common.printLogMessage("Added " + O2BookMap.keySet().size() + " books.", null, null, false);

        // add all spell texts for quick book item creation
        spellText.onEnable();
    }

    /**
     * Process book read events when bookLearning is enabled.
     *
     * @param event the player interact event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBookRead(@NotNull PlayerInteractEvent event) {
        // only run this if bookLearning is enabled
        if (!Ollivanders2.bookLearning)
            return;

        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
            Player player = event.getPlayer();

            ItemStack heldItem = player.getInventory().getItemInMainHand();
            if (heldItem.getType() == Material.WRITTEN_BOOK) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        readBook(player, heldItem);
                    }
                }.runTaskLater(p, Ollivanders2Common.ticksPerSecond * 2);
            }
        }
    }

    /**
     * Read a book.
     *
     * @param player the player reading the book
     * @param book   the book being read
     */
    private void readBook(@NotNull Player player, @NotNull ItemStack book) {
        common.printDebugMessage("O2Books: " + player.getDisplayName() + " reading a book and book learning is enabled.", null, null, false);

        // reading a book, if it is a spell book we want to let the player "learn" the spell.
        ItemMeta meta = book.getItemMeta();
        if (meta == null)
            return;

        readNBT(meta, player);
    }

    /**
     * Add all books in the O2BookType enum to the O2BooksMap.
     */
    private void addBooks() {
        common.printDebugMessage("O2Books: adding all books...", null, null, false);

        for (O2BookType bookType : O2BookType.values()) {
            O2Book book = getO2BookByType(bookType);

            if (book != null)
                O2BookMap.put(book.getTitle(), bookType);
        }
    }

    /**
     * Get an O2Book by book type.
     *
     * @param bookType the book to be returned
     * @return the BookItem if found bookType was found, null otherwise.
     */
    @Nullable
    private O2Book getO2BookByType(@NotNull O2BookType bookType) {
        Class<?> bookClass = bookType.getClassName();

        O2Book book = null;

        try {
            book = (O2Book) bookClass.getConstructor(Ollivanders2.class).newInstance(p);
        }
        catch (Exception e) {
            common.printDebugMessage("O2Books: exception trying to create new instance of " + bookType, e, null, true);
        }

        return book;
    }

    /**
     * Get a book item of this book.
     *
     * @param title the title of the book
     * @return a book item version of this book if it exists, null otherwise.
     */
    @Nullable
    public ItemStack getBookByTitle(@NotNull String title) {
        O2BookType match = getBookTypeByTitle(title);

        if (match == null)
            return null;

        return getBookByType(match);
    }

    /**
     * Get a book item of this book.
     *
     * @param bookType the type of book
     * @return a book item version of this book
     */
    @Nullable
    public ItemStack getBookByType(@NotNull O2BookType bookType) {
        O2Book o2book = getO2BookByType(bookType);

        if (o2book != null)
            return o2book.getBookItem();

        return null;
    }

    /**
     * Get a book type by book title
     *
     * @param title the book title to search for
     * @return the book type, if found, null otherwise
     */
    @Nullable
    public O2BookType getBookTypeByTitle(String title) {
        if (title.isEmpty())
            return null;

        String searchFor = title.toLowerCase();
        O2BookType match = null;

        // Iterate through all keys rather than a direct lookup so that we can:
        // - allow case-insensitive lookup
        // - allow partial match for lazy typing
        for (String key : O2BookMap.keySet()) {
            String bookTitle = key.toLowerCase();

            if (bookTitle.startsWith(searchFor)) {
                match = O2BookMap.get(key);
            }
        }

        return match;
    }

    /**
     * Gets all Ollivanders2 books.
     *
     * @return a ArrayList of all O2Book objects.
     */
    @NotNull
    public List<ItemStack> getAllBooks() {
        ArrayList<ItemStack> bookStack = new ArrayList<>();

        for (O2BookType bookType : O2BookType.values()) {
            O2Book o2book = getO2BookByType(bookType);

            if (o2book != null) {
                bookStack.add(o2book.getBookItem());
            }
        }

        return bookStack;
    }

    /**
     * Read the NBT tags for a magic book
     *
     * @param itemMeta the item meta
     * @param player   the player reading the book
     */
    private void readNBT(@NotNull ItemMeta itemMeta, @NotNull Player player) {
        if (!Ollivanders2.bookLearning)
            return;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        // read spells
        if (container.has(O2Book.o2BookSpellsKey, PersistentDataType.STRING)) {
            String spells = container.get(O2Book.o2BookSpellsKey, PersistentDataType.STRING);
            if (spells != null) {
                for (O2SpellType spellType : parseSpells(spells)) {
                    doBookLearningSpell(player, spellType);
                }
            }
        }

        // read potions
        if (container.has(O2Book.o2BookPotionsKey, PersistentDataType.STRING)) {
            String potions = container.get(O2Book.o2BookPotionsKey, PersistentDataType.STRING);
            if (potions != null) {
                for (O2PotionType potionType : parsePotions(potions)) {
                    doBookLearningPotion(player, potionType);
                }
            }
        }
    }

    /**
     * Do book learning for a spell
     *
     * @param player    the player doing the book learning
     * @param spellType the spell to learn
     */
    private void doBookLearningSpell(@NotNull Player player, @NotNull O2SpellType spellType) {
        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
        if (o2p == null)
            return;

        OllivandersBookLearningSpellEvent event = new OllivandersBookLearningSpellEvent(player, spellType);
        p.getServer().getPluginManager().callEvent(event);

        // check to see if the event was canceled
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!(event.isCancelled())) {
                    incrementSpell(o2p, spellType);
                }
            }
        }.runTaskLater(p, Ollivanders2Common.ticksPerSecond * 2);
    }

    /**
     * Do book learning for a potion
     *
     * @param player     the player learning the potion
     * @param potionType the potion to learn
     */
    private void doBookLearningPotion(@NotNull Player player, @NotNull O2PotionType potionType) {
        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
        if (o2p == null)
            return;

        OllivandersBookLearningPotionEvent event = new OllivandersBookLearningPotionEvent(player, potionType);
        p.getServer().getPluginManager().callEvent(event);

        // check to see if the event was canceled
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!(event.isCancelled())) {
                    incrementPotion(o2p, potionType);
                }
            }
        }.runTaskLater(p, Ollivanders2Common.ticksPerSecond * 2);
    }

    /**
     * Parse the spell types from a space-separated list of spell types.
     *
     * @param spellList the space-separated list
     * @return the spells found in the list
     */
    @NotNull
    private static ArrayList<O2SpellType> parseSpells(@NotNull String spellList) {
        ArrayList<O2SpellType> spells = new ArrayList<>();

        for (String s : spellList.split(" ")) {
            O2SpellType spellType;
            try {
                spellType = O2SpellType.valueOf(s);
            }
            catch (Exception e) {
                continue;
            }

            spells.add(spellType);
        }

        return spells;
    }

    /**
     * Increment the experience level of a spell for a player
     *
     * @param o2p       the player
     * @param spellType the spell
     */
    private static void incrementSpell(@NotNull O2Player o2p, @NotNull O2SpellType spellType) {
        int spellLevel = o2p.getSpellCount(spellType);

        // if spell count is less than 25, learn this spell
        if (spellLevel < 25) {
            o2p.incrementSpellCount(spellType);

            // if they have the improved learning effect, increment it again
            if (Ollivanders2API.getPlayers().playerEffects.hasEffect(o2p.getID(), O2EffectType.IMPROVED_BOOK_LEARNING)) {
                o2p.incrementSpellCount(spellType);
            }
        }
    }

    /**
     * Parse the potion types from a space-separated list of potion types.
     *
     * @param potionList the space-separated list
     * @return the potions found in the list
     */
    @NotNull
    private static ArrayList<O2PotionType> parsePotions(@NotNull String potionList) {
        ArrayList<O2PotionType> potions = new ArrayList<>();

        for (String s : potionList.split(" ")) {
            O2PotionType potionType;
            try {
                potionType = O2PotionType.valueOf(s);
            }
            catch (Exception e) {
                continue;
            }

            potions.add(potionType);
        }

        return potions;
    }

    /**
     * Increment the experience level of a potion for a player
     *
     * @param o2p        the player
     * @param potionType the spell
     */
    private static void incrementPotion(@NotNull O2Player o2p, @NotNull O2PotionType potionType) {
        int potionLevel = o2p.getPotionCount(potionType);

        // if spell count is less than 25, learn this spell
        if (potionLevel < 25) {
            o2p.incrementPotionCount(potionType);

            // if they have the improved learning effect, increment it again
            if (Ollivanders2API.getPlayers().playerEffects.hasEffect(o2p.getID(), O2EffectType.IMPROVED_BOOK_LEARNING)) {
                o2p.incrementPotionCount(potionType);
            }
        }
    }

    /**
     * Get all the titles for all loaded O2 books
     *
     * @return a list of the titles for all loaded books
     */
    @NotNull
    public List<String> getAllBookTitles() {
        ArrayList<String> bookTitles = new ArrayList<>(O2BookMap.keySet());
        Collections.sort(bookTitles);

        return bookTitles;
    }

    /**
     * Run the books subcommands
     *
     * @param sender the player that issued the command
     * @param args   the arguments to the book command
     * @return true if successful, false otherwise
     * @since 2.2.4
     */
    public boolean runCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            usageMessageBooks(sender);
            return true;
        }
        List<ItemStack> bookStack = new ArrayList<>();
        if (args[1].equalsIgnoreCase("allbooks")) {
            bookStack = getAllBooks();

            if (bookStack.isEmpty()) {
                sender.sendMessage(Ollivanders2.chatColor + "There are no Ollivanders2 books.");

                return true;
            }

            O2PlayerCommon.givePlayerKit((Player) sender, bookStack);
            return true;
        }
        else if (args[1].equalsIgnoreCase("list")) {
            // olli books list
            listAllBooks(sender);
            return true;
        }
        else if (args[1].equalsIgnoreCase("give")) {
            // olli books give <player> <book name>
            if (args.length < 4) {
                usageMessageBooks(sender);
            }

            //next arg is the target player
            String targetName = args[2];
            Player targetPlayer = p.getServer().getPlayer(targetName);
            if (targetPlayer == null) {
                sender.sendMessage(Ollivanders2.chatColor + "Did not find player \"" + targetName + "\".\n");

                return true;
            }
            else {
                common.printDebugMessage("O2Books:: player to give book to is " + targetName, null, null, false);
            }

            // args after "book give <player>" are book name
            String[] subArgs = Arrays.copyOfRange(args, 3, args.length);
            ItemStack bookItem = getBookFromArgs(subArgs, sender);

            if (bookItem == null) {
                return true;
            }

            bookStack.add(bookItem);

            O2PlayerCommon.givePlayerKit(targetPlayer, bookStack);
            return true;
        }
        else {
            if (sender instanceof Player) {
                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                ItemStack bookItem = getBookFromArgs(subArgs, sender);
                if (bookItem == null) {
                    sender.sendMessage(Ollivanders2.chatColor + "Unable to find that book. Are you sure you spelled the title correctly?");
                    return true;
                }

                bookStack.add(bookItem);

                O2PlayerCommon.givePlayerKit((Player) sender, bookStack);
                return true;
            }
        }

        usageMessageBooks(sender);
        return true;
    }

    /**
     * Get the book
     *
     * @param args   the arguments for the book command
     * @param sender the player that issued the command
     * @return true unless an error occurred
     */
    @Nullable
    private ItemStack getBookFromArgs(@NotNull String[] args, @NotNull CommandSender sender) {
        String title = Ollivanders2Common.stringArrayToString(args);

        ItemStack bookItem = getBookByTitle(title);

        if (bookItem == null) {
            sender.sendMessage(Ollivanders2.chatColor + "No book named \"" + title + "\".\n");
            usageMessageBooks(sender);
        }

        return bookItem;
    }

    /**
     * Usage message for book subcommands.
     *
     * @param sender the player that issued the command
     */
    private void usageMessageBooks(@NotNull CommandSender sender) {
        sender.sendMessage(Ollivanders2.chatColor
                + "Usage: /olli books"
                + "\nlist - gives a book that lists all available books"
                + "\nallbooks - gives all Ollivanders2 books, this may not fit in your inventory"
                + "\n<book title> - gives you the book with this title, if it exists"
                + "\ngive <player> <book title> - gives target player the book with this title, if it exists\n"
                + "\nExample: /ollivanders2 book standard book of spells grade 1");
    }

    /**
     * Show a list of all Ollivanders2 books
     *
     * @param sender the player to display the list to
     */
    private void listAllBooks(@NotNull CommandSender sender) {
        StringBuilder titleList = new StringBuilder();
        titleList.append("Book Titles:");

        for (String bookTitle : getAllBookTitles()) {
            titleList.append("\n").append(bookTitle);
        }

        sender.sendMessage(Ollivanders2.chatColor + titleList.toString());
    }
}