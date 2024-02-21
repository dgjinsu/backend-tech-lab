package test.databaseIndex;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    @Query("select c.id from Coupon c")
    Long findId();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Coupon> findById(Long id);
}
