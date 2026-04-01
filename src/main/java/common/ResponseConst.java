package common;

public class ResponseConst {
    public static final String CANNOT_CREATE_QUEUE = """
            correct example of request:
            queue create <queue name> <HH:MM> to
            <HH:MM> <DD.MM.YYYY>""";

    public static final String INCORRECT_TIME = "Queue with this fast join time is already exists.";
    public static final String QUEUE_DOES_NOT_EXIST = "queue does not exist.";
    public static final String POSITIVE_POSITION = "Position must be a positive number.";

    public static final String ADMIN_NOW = " is admin now.";
    public static final String CANNOT_DO_IT = " you cannot do it.";
}
