@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(OfferNotFoundException.class)
    public ResponseEntity<String> handleNotFound(OfferNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(OfferExpiredException.class)
    public ResponseEntity<String> handleExpired(OfferExpiredException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
