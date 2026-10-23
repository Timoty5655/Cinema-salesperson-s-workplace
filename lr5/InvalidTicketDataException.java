/**
 * Власний checked-виняток з ЛР3 (потрібен, щоб папка lr4 була самодостатньою).
 */
public class InvalidTicketDataException extends Exception {

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
