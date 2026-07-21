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
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.player.O2Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages all Ollivanders2 magic books and handles book learning mechanics.
 *
 * <p>When bookLearning is enabled, reading a book raises the reader's level in each of its spells and potions by 1,
 * up to a maximum of 25. This can be used for classes, creating lessons, or other in-game magic learning.</p>
 *
 * <p>When bookLearning is enabled, every Ollivanders2 spell must be in a WrittenBook with lore or NBT set up correctly
 * or players will not be able to learn them.</p>
 *
 * @author Azami7
 * @see <a href="https://github.com/Azami7/Ollivanders2/wiki/Configuration#book-learning">Ollivanders2 Wiki - Book Learning</a>
 */
public final class O2Books implements Listener {
    /**
     * All books, keyed by title.
     */
    private final Map<String, O2BookType> O2BookMap = new HashMap<>();

    Ollivanders2 p;

    Ollivanders2Common common;

    /**
     * The book text for each spell and potion.
     */
    BookTexts spellText;

    /**
     * Registers this manager as a listener so it can handle book-learning events.
     *
     * @param plugin the Ollivanders2 plugin instance
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
        // reset in case the plugin was disabled and re-enabled
        O2BookMap.clear();

        addBooks();
        common.printLogMessage("Added " + O2BookMap.keySet().size() + " books.", null, null, false);

        spellText.onEnable();
    }

    /**
     * Plugin disable hook; no cleanup needed since book data is read-only.
     */
    public void onDisable() {}

    /**
     * Process book read events when bookLearning is enabled.
     *
     * @param event the player interact event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBookRead(@NotNull PlayerInteractEvent event) {
        if (!Ollivanders2.bookLearning)
            return;

        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
            Player player = event.getPlayer();

            ItemStack heldItem = player.getInventory().getItemInMainHand();
            if (heldItem.getType() == Material.WRITTEN_BOOK) {
                // defer the read so the game and other plugins finish handling the interaction first, then only learn
                // from it if it wasn't cancelled
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (event.useItemInHand() != Event.Result.DENY) {
                            readBook(player, heldItem);
                        }
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

        ItemMeta meta = book.getItemMeta();
        if (meta == null)
            return;

        readNBT(meta, player);
    }

    /**
     * Populate the book map from all book types, keyed by title.
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
     * Create an O2Book instance for the given book type.
     *
     * @param bookType the book type to instantiate
     * @return the book instance, or null if it could not be created
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
     * Returns a book item for the book with the given title.
     *
     * @param title the title of the book to retrieve
     * @return an ItemStack representing the book, or null if not found
     */
    @Nullable
    public ItemStack getBookByTitle(@NotNull String title) {
        O2BookType match = getBookTypeByTitle(title);

        if (match == null)
            return null;

        return getBookByType(match);
    }

    /**
     * Returns a book item for the given book type.
     *
     * @param bookType the type of book to retrieve
     * @return an ItemStack representing the book, or null if creation failed
     */
    @Nullable
    public ItemStack getBookByType(@NotNull O2BookType bookType) {
        O2Book o2book = getO2BookByType(bookType);

        if (o2book != null)
            return o2book.getBookItem();

        return null;
    }

    /**
     * Find a book type by title, matching case-insensitively on any title that starts with the given text.
     *
     * @param title the book title or a leading portion of it
     * @return the first matching book type, or null if none matches
     */
    @Nullable
    public O2BookType getBookTypeByTitle(@NotNull String title) {
        if (title.isEmpty())
            return null;

        String searchFor = title.toLowerCase();
        O2BookType match = null;

        // iterate rather than a direct map lookup to support case-insensitive, prefix matching
        for (String key : O2BookMap.keySet()) {
            String bookTitle = key.toLowerCase();

            if (bookTitle.startsWith(searchFor)) {
                match = O2BookMap.get(key);
                break;
            }
        }

        return match;
    }

    /**
     * Returns a list of ItemStacks for all available Ollivanders2 books.
     *
     * @return a list of book ItemStacks for all book types, empty if none found
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
     * Learn every spell and potion recorded in the book's NBT tags.
     *
     * @param itemMeta the book's item metadata
     * @param player   the player reading the book
     */
    private void readNBT(@NotNull ItemMeta itemMeta, @NotNull Player player) {
        if (!Ollivanders2.bookLearning)
            return;

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (container.has(O2Book.o2BookSpellsKey, PersistentDataType.STRING)) {
            String spells = container.get(O2Book.o2BookSpellsKey, PersistentDataType.STRING);
            if (spells != null) {
                for (O2SpellType spellType : parseSpells(spells)) {
                    doBookLearningSpell(player, spellType);
                }
            }
        }

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

        // defer so other plugins can cancel the event first; only increment if it wasn't cancelled
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

        // defer so other plugins can cancel the event first; only increment if it wasn't cancelled
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
                // skip unknown names (spell removed or renamed) so old book items still parse
                continue;
            }

            spells.add(spellType);
        }

        return spells;
    }

    /**
     * Raise the player's level in a spell by one, up to a max of 25; by two if they have IMPROVED_BOOK_LEARNING.
     *
     * @param o2p       the player
     * @param spellType the spell
     */
    private static void incrementSpell(@NotNull O2Player o2p, @NotNull O2SpellType spellType) {
        int spellLevel = o2p.getSpellCount(spellType);

        if (spellLevel < 25) {
            o2p.incrementSpellCount(spellType);

            // IMPROVED_BOOK_LEARNING grants a second level per read
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
                // skip unknown names (potion removed or renamed) so old book items still parse
                continue;
            }

            potions.add(potionType);
        }

        return potions;
    }

    /**
     * Raise the player's level in a potion by one, up to a max of 25; by two if they have IMPROVED_BOOK_LEARNING.
     *
     * @param o2p        the player
     * @param potionType the potion
     */
    private static void incrementPotion(@NotNull O2Player o2p, @NotNull O2PotionType potionType) {
        int potionLevel = o2p.getPotionCount(potionType);

        if (potionLevel < 25) {
            o2p.incrementPotionCount(potionType);

            // IMPROVED_BOOK_LEARNING grants a second level per read
            if (Ollivanders2API.getPlayers().playerEffects.hasEffect(o2p.getID(), O2EffectType.IMPROVED_BOOK_LEARNING)) {
                o2p.incrementPotionCount(potionType);
            }
        }
    }

    /**
     * Returns all book titles, sorted alphabetically.
     *
     * @return a sorted list of all book titles
     */
    @NotNull
    public List<String> getAllBookTitles() {
        ArrayList<String> bookTitles = new ArrayList<>(O2BookMap.keySet());
        Collections.sort(bookTitles);

        return bookTitles;
    }

    /**
     * Handles the /olli books subcommands.
     * <p>
     * Supports: "allbooks" - gives all books, "list" - shows book list,
     * "give [player] [title]" - gives a book to a player, or directly retrieve a book by title.
     * </p>
     *
     * @param sender the command sender
     * @param args   the command arguments (args[1] is the subcommand)
     * @return always true
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
                return true;
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
     * Retrieves a book by joining command arguments and searching by title.
     * <p>
     * If the book is not found, sends an error message and usage info to the sender.
     * </p>
     *
     * @param args   the command arguments to join as a book title
     * @param sender the command sender to notify on error
     * @return an ItemStack for the book if found, null otherwise
     */
    @Nullable
    private ItemStack getBookFromArgs(@NotNull String[] args, @NotNull CommandSender sender) {
        String title = String.join(" ", args);

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