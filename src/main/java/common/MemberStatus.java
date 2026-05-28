package common;

import lombok.Getter;

@Getter
public enum MemberStatus {
    IN_PROCESS("in progress"),
    RETAKE("retake"),
    COMPLETE("complete");

    private final String status;
    MemberStatus(String status) {
        this.status = status;
    }

}
