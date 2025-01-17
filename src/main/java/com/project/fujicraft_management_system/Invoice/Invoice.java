package com.project.fujicraft_management_system.Invoice;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;


import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
public class Invoice {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "item_name")
    private String itemName;

    @Column
    private int quantity;

    @Column
    private double unitPrice;

    @Column
    private String itemColor;

    private String po;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate poDate;

    @Column
    private double total;

    @Column
    private double amount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate invoiceDate;

    private int invoiceNo;

}
