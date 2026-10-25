package org.apache.ofbiz.migration.humanres;

import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SequenceValueItemRepository extends JpaRepository<SequenceValueItem, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select item from SequenceValueItem item where item.seqName = :seqName")
    Optional<SequenceValueItem> findForUpdate(@Param("seqName") String seqName);
}
