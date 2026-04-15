package fr.eni.bookhubbackend;

public class ErrorKeys {

    private ErrorKeys() {
    }

    public static final String BOOK_NOT_FOUND = "exception.book.not.found";
    public static final String BOOK_IS_AVAILABLE = "exception.book.is.available";
    public static final String USER_NOT_FOUND = "exception.user.not.found";
    public static final String RESERVATION_NOT_FOUND = "exception.reservation.not.found";
    public static final String NUMBER_MAXIMUM_RESERVATION = "exception.number.maximum.reservation";
    public static final String RESERVATION_ALREADY_BOOKED = "exception.reservation.already.booked";

}
