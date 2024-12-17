package org.finra.rmcs.entity;

import java.io.Serializable;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesItemKey implements Serializable {

  @Column(name = "revenue_stream_name")
  private String revenueStream;

  @Column(name = "sales_item_id")
  private String salesItemId;
}
