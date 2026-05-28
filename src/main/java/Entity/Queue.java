package Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "queue")
public class Queue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "queue_name")
    String queueName;
    @Column(name = "chat_id")
    Long chatId;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "queue", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    List<Member> members;

    @Column(name = "start_time")
    LocalTime startTime;

    @Column(name = "end_time")
    LocalTime endTime;

    @Column(name = "start_date")
    LocalDate startDate;

    public  Queue(String queueName, Long chatId,
                  LocalTime startTime, LocalTime endTime,
                  LocalDate startDate) {
        this.queueName = queueName;
        this.chatId = chatId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDate = startDate;
    }
}
