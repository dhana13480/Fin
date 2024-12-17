package org.finra.rmcs.repo;

import java.util.List;
import org.finra.rmcs.entity.AccountingEquationInvoiceViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountingEquationInvoiceViewRepo extends JpaRepository<AccountingEquationInvoiceViewEntity, String> {
  @Query(
      nativeQuery = true,
      value =
          "select revenue_stream , org_id , customer_id ,\n"
              + "invoice_id , \n"
              + "invoice_date  , invoice_amount , invoice_balance ,\n"
              + "calculated_invoice_balance  , variance \n"
              + "from modeled.accounting_equation_invoice_view aeiv order by revenue_stream asc, org_id")
  List<AccountingEquationInvoiceViewEntity> getAccountEquationReportData4Invoices();
}
