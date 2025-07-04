package common;

public class ResponseConst {
    public static final String CANNOT_CREATE_QUEUE = """
            correct example of request:
            queue create <queue name> <HH:MM> to
            <HH:MM> <DD.MM.YYYY>""";

    public static final byte QUEUES_LIMIT_FOR_CHAT = 5;
    public static final String QUEUES_LIMIT_MSG = "max queues for chat: " + QUEUES_LIMIT_FOR_CHAT;
    public static final String INCORRECT_TIME = "your time is incorrect.";
    public static final String QUEUE_DOES_NOT_EXIST = "queue does not exist.";
    public static final String POSITIVE_POSITION = "Position must be a positive number.";


    public static final String ADMIN_NOW = " is admin now.";
    public static final String CANNOT_DO_IT = " you cannot do it.";
}
