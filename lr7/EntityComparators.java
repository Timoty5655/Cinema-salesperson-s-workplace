import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/** Компаратори для сутностей. Порядок задано ззовні класів, тому їх може бути скільки завгодно. */
public final class EntityComparators {

    private EntityComparators() {
    }

    /**
     * Collator з українською локаллю: "Є", "І", "Ї", "Ґ" стають на свої місця в алфавіті,
     * а не за кодом Unicode (String.compareTo поставив би "Їжак" після "Я").
     * Strength SECONDARY — регістр не впливає на порядок.
     */
    private static final Collator UA_COLLATOR = Collator.getInstance(Locale.of("uk", "UA"));

    static {
        UA_COLLATOR.setStrength(Collator.SECONDARY);
    }

    /** За іменем; сутності без імені — в кінці (інакше Collator кинув би NullPointerException). */
    public static final Comparator<BaseEntity> BY_NAME = byName();

    /**
     * Той самий порядок, але з типом Comparator<T>: до Comparator<BaseEntity> не можна дописати
     * thenComparing(Movie::getReleaseYear), бо компілятор знає лише поля BaseEntity.
     */
    public static <T extends BaseEntity> Comparator<T> byName() {
        return Comparator.comparing(BaseEntity::getName, Comparator.nullsLast(UA_COLLATOR));
    }

    /** Спочатку за іменем, при збігу — за роком виходу (ремейки мають ту саму назву). */
    public static final Comparator<Movie> MOVIE_BY_NAME_AND_YEAR =
            EntityComparators.<Movie>byName().thenComparingInt(Movie::getReleaseYear);

    /** Сеанси: за іменем, при збігу — за часом початку. */
    public static final Comparator<Screening> SCREENING_BY_NAME_AND_TIME =
            EntityComparators.<Screening>byName().thenComparing(Screening::getStartTime);

    /** Той самий порядок, написаний вручну — для порівняння з thenComparing (див. README). */
    public static final Comparator<Movie> MOVIE_BY_NAME_AND_YEAR_MANUAL = new Comparator<>() {
        @Override
        public int compare(Movie a, Movie b) {
            int byName = BY_NAME.compare(a, b);
            if (byName != 0) {
                return byName;
            }
            return Integer.compare(a.getReleaseYear(), b.getReleaseYear()); // не a - b: переповнення
        }
    };
}
