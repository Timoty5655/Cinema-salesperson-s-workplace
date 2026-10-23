/**
 * "Сторонній" банківський шлюз. Імітує бібліотеку, яку ми НЕ можемо змінювати:
 * інші назви методів, суми в копійках (int), результат - числовий код.
 */
public class LegacyBankGateway {

    private String lastError = "";

    /** @return 0 - успіх, інакше код помилки */
    public int makeTransaction(int amountKop, String cardMask) {
        if (amountKop <= 0) {
            lastError = "BAD_AMOUNT";
            return 1;
        }
        if (amountKop > 5_000_000) {
            lastError = "LIMIT_EXCEEDED";
            return 2;
        }
        lastError = "";
        return 0;
    }

    /** Комісія шлюзу в копійках (2%). */
    public int calcCommissionKop(int amountKop) {
        return amountKop * 2 / 100;
    }

    public String getLastError() {
        return lastError;
    }
}
