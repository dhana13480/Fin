package org.finra.rmcs.repo;

import java.util.List;
import org.finra.rmcs.entity.AccountingEquationFFViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountingEquationFFViewRepo extends JpaRepository<AccountingEquationFFViewEntity, String> {

  @Query(
      nativeQuery = true,
      value =
          "select  'CRDRG' as revenue_stream,  replace(customer_id, '-CRG', '') customer_id , beginning_balance , \n"
              + "ending_balance , calculated_balance , variance \n"
              + "from modeled.accounting_equation_ff_view aefv ")
  List<AccountingEquationFFViewEntity> getAccountEquationReportData4FF();
}
