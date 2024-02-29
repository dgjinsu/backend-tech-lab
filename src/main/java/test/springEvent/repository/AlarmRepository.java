package test.springEvent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import test.springEvent.entity.Alarm;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {
}
