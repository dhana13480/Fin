package org.finra.rmcs.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "sales_item_catlog_vw", schema = "rmcs")
@Data
@IdClass(SalesItemKey.class)
public class SalesItemEntity implements Serializable {

  private static final long serialVersionUID = -6006855776016657532L;

  @Id
  @Column(name = "revenue_stream_name")
  private String revenueStream;

  @Id
  @Column(name = "sales_item_id")
  private String salesItemId;

  @Column(name = "sales_item_name")
  private String salesItemName;

  @Column(name = "sales_item_name_desc")
  private String salesItemNameDesc;

  @Column(name = "active")
  private boolean active;

  @Column(name = "created_date")
  private LocalDateTime createdDate;
}
