package Order;

public enum Status {
    QUEUE("Queue"),
    IN_PROCESS("Process"),
    DONE("Done");

    private final String description;

    Status(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
