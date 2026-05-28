package common;

import io.github.cdimascio.dotenv.Dotenv;

public final class AppConfig {

    private AppConfig() {}

    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    private static String getEnvValue(String key) {
        String value = dotenv.get(key);
        return value != null ? value : System.getenv(key);
    }

    public static final class QueueConfig {

        public static final int MAX_QUEUE_COUNT =
                Integer.parseInt(getEnvValue("MAX_QUEUE_COUNT"));

    }

}
