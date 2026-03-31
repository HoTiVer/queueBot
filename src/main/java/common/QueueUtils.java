package common;

import Entity.Queue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class QueueUtils {

    public static boolean validateCorrectQueueTime(List<Queue> queues,
                                             LocalTime startTime,
                                             LocalTime endTime,
                                             LocalDate selectedDate) {
        for (Queue queue : queues) {
            if (!queue.getStartDate().equals(selectedDate)) {
                continue;
            }

            LocalTime existingStart = queue.getStartTime();
            LocalTime existingEnd = queue.getEndTime();

            if (!(endTime.isBefore(existingStart) || startTime.isAfter(existingEnd))) {
                return false;
            }
        }
        return true;
    }

    public static String getQueueName(String text){
        String[] parts = text.trim().split("\\s+");
        if (parts.length < 3) {
            throw new IllegalArgumentException("Input must contain at least 3 words.");
        }
        StringBuilder middle = new StringBuilder();
        for (int i = 1; i < parts.length - 1; i++) {
            middle.append(parts[i]);
            if (i < parts.length - 2) {
                middle.append(" ");
            }
        }

        return middle.toString();
    }

    public static int getQueuePosition(String text){
        String[] parts = text.trim().split("\\s+");
        if (parts.length < 2) {
            throw new IllegalArgumentException();
        }
        return Integer.parseInt(parts[parts.length - 1]);
    }

    public static String getNameWithoutPos(String text){
        if (text != null && !text.isBlank()) {
            String[] words = text.trim().split("\\s+");
            return words[words.length - 1];
        }
        return "";
    }
}
