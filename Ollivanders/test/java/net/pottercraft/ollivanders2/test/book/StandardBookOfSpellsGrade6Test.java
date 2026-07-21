package net.pottercraft.ollivanders2.test.book;

import net.pottercraft.ollivanders2.book.STANDARD_BOOK_OF_SPELLS_GRADE_6;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit tests for {@link STANDARD_BOOK_OF_SPELLS_GRADE_6}.
 */
public class StandardBookOfSpellsGrade6Test extends BookTestSuper {
    @Override @BeforeEach
    void setUp() {
        book = new STANDARD_BOOK_OF_SPELLS_GRADE_6(testPlugin);
        meta = (BookMeta) book.getBookItem().getItemMeta();
    }
}