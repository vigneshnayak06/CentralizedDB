package Constants;

public enum Datatype {
    INT("int"),
    BOOL("boolean"),
    DOUBLE("double"),
    STRING("string");

    public final String type;
    Datatype(String type){
        this.type = type;
    }
}
