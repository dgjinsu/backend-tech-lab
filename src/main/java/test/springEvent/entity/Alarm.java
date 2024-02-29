package test.springEvent.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Alarm {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private LocalDateTime createdAt;

    @Builder
    public Alarm(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
