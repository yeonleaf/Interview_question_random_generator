public class ClientFaultException extends RuntimeException {
    private String possible_cause;
    private String location;

    private String msg;

    public ClientFaultException(String possible_cause, String location, String msg) {
        this.possible_cause = possible_cause;
        this.location = location;
        this.msg = msg;
    }
}
