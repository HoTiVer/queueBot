package Service;

import Entity.Member;
import Entity.Queue;
import Repository.QueueDao;
import common.ResponseConst;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class QueueQueryService {

    private final QueueDao queueDao;

    public QueueQueryService(QueueDao queueDao) {
        this.queueDao = queueDao;
    }

    public String getChatQueuesNamesAndMiniInfo(Long chatId) {
        String response = "there are no queues yet";
        List<Queue> queues = queueDao.getChatQueues(chatId);

        StringBuilder result = new StringBuilder();
        int counter = 0;

        for (var queue : queues){
            result.append(++counter).append(")").append(queue.getQueueName()).append("\n");
            result.append("Fast join time: ");
            result.append(queue.getStartTime());
            result.append(" to ");
            result.append(queue.getEndTime());
            result.append("\n");
            result.append("Date: ");
            result.append(queue.getStartDate());
            result.append("\n");
        }

        if (!result.toString().isEmpty()){
            response = result.toString();
        }

        return response;
    }

    public String getQueueInfo(Long chatId, String text) {
        String queueName = text.substring(5);
        Queue queue;
        try {
            queue = queueDao.getQueueByChatIdAndName(chatId, queueName);
        } catch (Exception e){
            return ResponseConst.QUEUE_DOES_NOT_EXIST;
        }

        if (queue == null){
            return ResponseConst.QUEUE_DOES_NOT_EXIST;
        }

        StringBuilder builder = new StringBuilder();

        builder.append(queueName).append(": ").append("\n");
        builder.append("Fast join time: ")
                .append(queue.getStartTime())
                .append(" to ")
                .append(queue.getEndTime())
                .append("\n")
                .append("Date: ")
                .append(queue.getStartDate())
                .append("\n");

        List<Member> sortedMembers = new ArrayList<>(queue.getMembers());

        if (sortedMembers.isEmpty()){
            builder.append("queue is null");
        }

        sortedMembers.sort(Comparator.comparingInt(Member::getPosition));

        for (var member : sortedMembers){
            builder.append(member.getPosition()).append(")")
                    .append(member.getUserName()).append(" ")
                    .append(member.getMemberStatus().getStatus()).append("\n");
        }

        builder.append("\n");

        builder.append("To mark as completed: complete ").append(queueName);
        builder.append("\n");
        builder.append("To retake: retake ").append(queueName);


        return builder.toString();
    }
}
