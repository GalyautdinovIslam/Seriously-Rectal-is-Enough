package ru.itis.slakeeper.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.slakeeper.models.Sla;

public interface SlaRepository extends JpaRepository<Sla, Long> {
}
