package Constants;

public enum Operation {
    EQUALS("="),
    LESS_THAN("<"),
    GREATER_THAN(">"),
    NOT_EQUALS("!="),
    LIKE("like");

    private final String operation;
    Operation(String operation){
        this.operation = operation;
    }
}
