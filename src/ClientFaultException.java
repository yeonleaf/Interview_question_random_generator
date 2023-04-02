public class ClientFaultException extends RuntimeException {
    public ClientFaultException() {
    }

    public ClientFaultException(String message, Throwable cause) {
        super(message, cause);
    }
}
