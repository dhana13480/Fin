package org.finra.rmcs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;


@Builder
@Data
@Entity
@Table(name = "invoice_detail_file", schema = "modeled")
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDetailFileEntity implements Serializable {
  private static final long serialVersionUID = 2830633145541057828L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private UUID id;

  @Column(name = "invoice_number")
  private String invoiceNumber;

  @Column(name = "bu_code")
  private String buCode;

  @Column(name = "file_type")
  private String fileType;

  @Column(name = "file_name")
  private String fileName;

  @Column(name = "file_path")
  private String filepath;

  @Column(name = "sub_partition")
  private String subPartition;

  @Column(name = "version")
  private Integer version;

  @Column(name = "created_date")
  private Timestamp createdDate;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "updated_date")
  private Timestamp updatedDate;

  @Column(name = "updated_by")
  private String updatedBy;
}
