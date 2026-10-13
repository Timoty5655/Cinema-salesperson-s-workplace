/**
 * Власний checked-виняток: порушення доменного правила кінотеатру
 * (кількість квитків > 0, ціна > 0, знижка в межах 0..100 %).
 */
public class InvalidTicketDataException extends Exception {

    // додаткові поля: яке саме поле некоректне і яке значення його спричинило
    private final String fieldName;
    private final double invalidValue;

    public InvalidTicketDataException(String message, String fieldName, double invalidValue) {
        super(message);
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public double getInvalidValue() {
        return invalidValue;
    }
}
