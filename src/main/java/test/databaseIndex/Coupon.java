package test.databaseIndex;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Coupon {
    @Id
    @GeneratedValue
    @Column(name = "coupon_id")
    private Long id;

    private int count;

    @Version
    private Integer version;

    @Builder
    public Coupon(int count) {
        this.count = count;
    }

    public void issue() {
        if (count <= 0) {
            throw new IllegalArgumentException("수량 부족");
        }
        count -= 1;
    }
}
