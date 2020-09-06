package order;

public enum Status {
    QUEUE("Queue"),
    IN_PROCESS("In progress"),
    DONE("Done");

    private final String description;

    Status(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
