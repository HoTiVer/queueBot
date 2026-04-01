package Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import Entity.Member;
import Entity.Queue;
import Repository.MemberDao;
import Repository.QueueDao;
import common.AppConfig;
import common.QueueUtils;
import common.ResponseConst;
import common.MemberStatus;


public class QueueService {

    private final QueueDao queueDao;
    private final MemberDao memberDao;
    private final QueueUtils queueUtils;
    ZoneId zoneId = ZoneId.of("Europe/Kyiv");

    public QueueService(QueueDao queueDao, MemberDao memberDao,
                        QueueUtils queueUtils) {
        this.queueDao = queueDao;
        this.memberDao = memberDao;
        this.queueUtils = queueUtils;
    }

    public synchronized String joinQueue(Long chatId, String userName, String text) {
        int queuePosition = QueueUtils.getQueuePosition(text);
        if (queuePosition <= 0) {
            return ResponseConst.POSITIVE_POSITION;
        }
        String queueName = QueueUtils.getQueueName(text);

        Queue queue = queueDao.getQueueByChatIdAndName(chatId, queueName);

        if (queue == null) {
            return ResponseConst.QUEUE_DOES_NOT_EXIST;
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        if (today.isBefore(queue.getStartDate())){
            return "❌ day of joining the queue is: " + queue.getStartDate();
        }

        if (today.isEqual(queue.getStartDate()) && now.isBefore(queue.getStartTime())){
            return "❌ you can only join after: " + queue.getStartTime() + " " + queue.getStartDate();
        }

        for (var member : queue.getMembers()) {
            if (member.getPosition() == queuePosition) {
                if (!member.getUserName().equals(userName)) {
                    return userName + ",❌ member with this position is already exist";
                }
            }

            if (member.getUserName().equals(userName)) {
                if (member.getPosition() == queuePosition) {
                    return userName + ",❌ you are already in this position in the queue: " + queueName;
                }

                boolean positionTaken = false;
                for (var other : queue.getMembers()) {
                    if (other.getPosition() == queuePosition && !other.getUserName().equals(userName)) {
                        positionTaken = true;
                        break;
                    }
                }

                if (positionTaken) {
                    return userName + ",❌ this position is already taken.";
                }

                member.setPosition(queuePosition);
                memberDao.update(member);
                return userName + ",✅ your position has been updated to: " + queuePosition;
            }
        }

        Member member = Member.builder()
                .userName(userName)
                .position(queuePosition)
                .queueId(queue.getId())
                .memberStatus(MemberStatus.IN_PROCESS)
                .build();

        memberDao.save(member);

        return "✅ " + userName + " you successfully added to queue " + queueName
                + " as " + queuePosition + " member";
    }

    public String createQueue(Long chatId, String text) {
        List<String> words = Arrays.stream(text.trim().split("\\s+"))
                .filter(s -> !s.isBlank())
                .toList();

        int size = words.size();
        if (size < 6) {
            return "Incorrect format. Use: create <name> <startTime HH:MM> to <endTime HH:MM> <date DD.MM.YYYY>";
        }

        List<Queue> queues = queueDao.getChatQueues(chatId);
        if (queues.size() >= AppConfig.QueueConfig.MAX_QUEUE_COUNT)
            return "Queue limit reached";


        String queueName = String.join(" ", words.subList(1, size - 4));

        boolean nameExists = queues
                .stream()
                .anyMatch(q -> q.getQueueName().equalsIgnoreCase(queueName));
        if (nameExists) {
            return "Queue with name " + queueName + " already exist";
        }

        LocalTime startTime = LocalTime.parse(words.get(size - 4));
        LocalTime endTime = LocalTime.parse(words.get(size - 2));
        LocalDate date = LocalDate.parse(words.get(size - 1),
                DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        if (!QueueUtils.validateCorrectQueueTime(queues, startTime, endTime, date)) {
            return ResponseConst.INCORRECT_TIME;
        }

        Queue queue = new Queue(
                queueName,
                chatId,
                startTime,
                endTime,
                date
        );

        queueDao.save(queue);
        QueueNotificationService.getInstance().scheduleQueueStartNotification(queue);

        return "New queue " + queue.getQueueName() + " created";
    }

    public void deleteQueue(Long chatId, String text) {
        String queueName = text.substring(7);
        Queue queue = queueDao.getQueueByChatIdAndName(chatId, queueName);

        queueDao.delete(queue.getId());

        QueueNotificationService notificationService = QueueNotificationService.getInstance();
        notificationService.cancelQueueNotification(queue.getId());
    }

    //TODO make new logic
    public String leaveQueue(Long chatId, String userName, String text) {
        String queueName = text.substring(6);
        Queue queue = queueDao.getQueueByChatIdAndName(chatId, queueName);

        if (queue != null){
            long memberId = 0;
            var members = queue.getMembers();
            for (var member : members) {
                if (member.getUserName().equals(userName)){
                    memberId = member.getId();
                    break;
                }
            }
            if (memberId != 0) {
                memberDao.delete(memberId);
            }
            return "You have left the queue: " + queueName;
        }
        return "";
    }

    public synchronized String fastQueueJoin(Long chatId, String text, String userName){
        String response = "no queues for fast join";
        List<Queue> queues = queueDao.getChatQueues(chatId);

        for (var queue : queues){
            LocalTime currentTime = LocalTime.now(zoneId);
            if (currentTime.isAfter(queue.getStartTime())
                    && currentTime.isBefore(queue.getEndTime())
                    && queue.getStartDate().equals(LocalDate.now(zoneId))){

                String queueName = queue.getQueueName();

                String joinCommand = "join " + queueName + " " + text;
                response = joinQueue(chatId, userName, joinCommand);
            }
        }
        return response;
    }

    public synchronized String swapMembers(Long chatId, String text, String userName) {
        int queuePosition = QueueUtils.getQueuePosition(text);
        if (queuePosition <= 0){
            return ResponseConst.POSITIVE_POSITION;
        }
        String queueName = QueueUtils.getQueueName(text);

        Queue queue = queueDao.getQueueByChatIdAndName(chatId, queueName);

        if (queue == null){
            return ResponseConst.QUEUE_DOES_NOT_EXIST;
        }

        var members = queue.getMembers();

        Member currentMember = null;
        Member memberToSwap = null;

        for (var member : members){
            if (member.getUserName().equals(userName)){
                currentMember = member;
            }
            if (member.getPosition() == queuePosition){
                memberToSwap = member;
            }
        }

        if (currentMember == null){
            return "You are not in queue yet";
        }

        if (memberToSwap == null){

            currentMember.setPosition(queuePosition);
            memberDao.update(currentMember);

            return userName + " your new position in queue: " + queuePosition;
        }

        if (currentMember.getUserName().equals(memberToSwap.getUserName())){
            return "you cannot swap with yourself";
        }

        if (currentMember.getPosition() < memberToSwap.getPosition()){
            int cup = currentMember.getPosition();
            currentMember.setPosition(memberToSwap.getPosition());
            memberToSwap.setPosition(cup);

            memberDao.update(currentMember);
            memberDao.update(memberToSwap);

            return currentMember.getUserName() + "you have successfully switched places with " +
                    memberToSwap.getUserName();
        }

        return "you cannot swap";
    }

    public String updateMemberStatus(Long chatId, String text,
                                     String userName, MemberStatus newStatus) {

        String queueName = QueueUtils.getNameWithoutPos(text);
        if (queueName.isEmpty()){
            return "unknown command";
        }
        Queue queue = queueDao.getQueueByChatIdAndName(chatId, queueName);

        if (queue == null){
            return ResponseConst.QUEUE_DOES_NOT_EXIST;
        }

        Member memberForUpdate = null;

        for (var member : queue.getMembers()){
            if (member.getUserName().equals(userName)){
                memberForUpdate = member;
                break;
            }
        }

        if (memberForUpdate != null){
            memberForUpdate.setMemberStatus(newStatus);
            memberDao.update(memberForUpdate);
            return userName + " your new status is " + newStatus;
        }
        return "error, cannot update status";
    }

    public String rebuildQueue(Long chatId, String text) {
        String queueName = text.replaceFirst("(?i)^rebuild\\s+", "")
                .trim().replaceAll("\\s+", " ");

        if (queueName.isEmpty()) {
            return "unknown command";
        }
        Queue queue = queueDao.getQueueByChatIdAndName(chatId, queueName);

        if (queue == null) {
            return ResponseConst.QUEUE_DOES_NOT_EXIST;

        }

        List<Member> members = new ArrayList<>(queue.getMembers());

        Iterator<Member> iterator = members.iterator();
        while (iterator.hasNext()) {
            Member member = iterator.next();

            if (!member.getMemberStatus().equals(MemberStatus.IN_PROCESS)) {
                memberDao.delete(member.getId());
                iterator.remove();
            }
        }

        int pos = 1;
        for (Member member : members) {
            member.setPosition(pos++);
            memberDao.update(member);
        }

        queue.setMembers(members);
        queueDao.update(queue);

        return queueName + " is rearranged";
    }

    public String setNewQueueFastTime(Long chatId, String text) {
        List<String> textList = Arrays.stream(text.trim().split("\\s+"))
                .filter(s -> !s.isBlank())
                .toList();

        int size = textList.size();
        if (size < 6) return "Incorrect format. Use: settime <name> <startTime HH:MM> to <endTime HH:MM> <date DD.MM.YYYY>";

        try {
            LocalTime startTime = LocalTime.parse(textList.get(size - 4));
            LocalTime endTime = LocalTime.parse(textList.get(size - 2));
            LocalDate date = LocalDate.parse(textList.get(size - 1), DateTimeFormatter.ofPattern("dd.MM.yyyy"));

            String queueName = String.join(" ", textList.subList(1, size - 4));

            Queue queue = queueDao.getQueueByChatIdAndName(chatId, queueName);
            if (queue == null) return "Queue not found: " + queueName;

            List<Queue> queuesInChat = queueDao.getChatQueues(chatId).stream()
                    .filter(q -> !q.getId().equals(queue.getId()))
                    .collect(Collectors.toList());

            boolean isCorrectTime = QueueUtils.validateCorrectQueueTime(queuesInChat, startTime, endTime, date);
            if (!isCorrectTime) {
                return "Time conflict with another queue on " + date + ". Choose a different time.";
            }

            QueueNotificationService.getInstance().cancelQueueNotification(queue.getId());

            queue.setStartTime(startTime);
            queue.setEndTime(endTime);
            queue.setStartDate(date);
            queueDao.update(queue);

            QueueNotificationService.getInstance().scheduleQueueStartNotification(queue);

            return "Queue \"" + queue.getQueueName() + "\" time updated: " + startTime
                    + " - " + endTime + " on " + date;

        } catch (Exception e) {
            return "Error: invalid command format or date/time.\nUse: settime <name> <startTime HH:MM> to <endTime HH:MM> <date DD.MM.YYYY>";
        }
    }

    public String insertMember(Long chatId, String text) {
        Pattern pattern = Pattern.compile("insert\\s+(.+?)\\s+(@?\\S+)\\s+pos\\s+(\\d+)$");
        Matcher matcher = pattern.matcher(text.trim());

        if (!matcher.find()) {
            return "Invalid format. Use: insert Queue_Name @username pos position";
        }

        String queueName = matcher.group(1);
        String userName = matcher.group(2);
        int position;
        try {
            position = Integer.parseInt(matcher.group(3));
            if (position <= 0) {
                return ResponseConst.POSITIVE_POSITION;
            }
        } catch (NumberFormatException e) {
            return "Invalid position format.";
        }

        Queue queue = queueDao.getQueueByChatIdAndName(chatId, queueName);
        if (queue == null) {
            return "Queue \"" + queueName + "\" not found.";
        }

        List<Member> members = new ArrayList<>(queue.getMembers());

        boolean alreadyExists = members.stream()
                .anyMatch(m -> m.getUserName().equalsIgnoreCase(userName));
        if (alreadyExists) {
            return "@" + userName + " is already in this queue.";
        }

        int maxPosition = members.stream()
                .mapToInt(Member::getPosition)
                .max()
                .orElse(0);

        if (position > maxPosition + 1) {
            return "Position exceeds the current queue length. Max available: " + (maxPosition + 1);
        }

        for (Member member : members) {
            if (member.getPosition() >= position) {
                member.setPosition(member.getPosition() + 1);
                memberDao.update(member);
            }
        }

        Member newMember = Member.builder()
                .userName(userName)
                .position(position)
                .queueId(queue.getId())
                .memberStatus(MemberStatus.IN_PROCESS)
                .build();

        memberDao.save(newMember);

        return userName + " has been added to the queue \"" + queueName + "\" at position " + position + ". Other members have been shifted down.";
    }

    public String removeMember(Long chatId, String text) {
        Pattern pattern = Pattern.compile("remove\\s+(.+?)\\s+(@?\\S+)$");
        Matcher matcher = pattern.matcher(text.trim());

        if (!matcher.find()) {
            return "Invalid format. Use: remove Queue_Name @username";
        }

        String queueName = matcher.group(1);
        String userName = matcher.group(2);
        Queue queue = queueDao.getQueueByChatIdAndName(chatId, queueName);

        if (queue == null){
            return ResponseConst.QUEUE_DOES_NOT_EXIST;
        }

        List<Member> members = new ArrayList<>(queue.getMembers());
        Optional<Member> memberToRemoveOpt = members.stream()
                .filter(m -> m.getUserName().equalsIgnoreCase(userName))
                .findFirst();

        if (memberToRemoveOpt.isEmpty()) {
            return "User " + userName + " is not in the queue \"" + queueName + "\".";
        }

        Member memberToRemove = memberToRemoveOpt.get();
        int removedPosition = memberToRemove.getPosition();

        try {
            memberDao.delete(memberToRemove.getId());

            members.stream()
                    .filter(m -> m.getPosition() > removedPosition)
                    .forEach(m -> {
                        m.setPosition(m.getPosition() - 1);
                        memberDao.update(m);
                    });

            return "User " + userName + " has been removed from the queue \"" + queueName +
                    "\". Positions have been updated.";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Failed to remove user " + userName + ".";
        }
    }
}
