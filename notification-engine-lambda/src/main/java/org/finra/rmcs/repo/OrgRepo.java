package org.finra.rmcs.repo;

import java.util.List;
import java.util.Set;
import org.finra.rmcs.entity.Org;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
@Component
public interface OrgRepo extends JpaRepository<Org, String> {

  List<Org> findBySetIdAndCustomerIdIn(@Param("buUnit") String buUnit, @Param("customerIds") Set<String> customerIds);
}
