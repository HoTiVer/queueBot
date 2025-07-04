package Entity;

import common.MemberStatus;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "member")
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_name")
    String userName;

    @Column(name = "position")
    Integer position;

    @Column(name = "queue_id")
    Long queueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "queue_id", insertable=false, updatable=false)
    Queue queue;

    @Column(name = "member_status")
    @Enumerated(value = EnumType.STRING)
    MemberStatus memberStatus;
}
