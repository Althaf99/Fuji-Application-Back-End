package com.project.fujicraft_management_system.Stock;

import jakarta.persistence.*;
import lombok.Data;

@Table(name = "stock",
        uniqueConstraints = @UniqueConstraint(columnNames = {"itemName", "itemColor"}))
@Entity
@Data
public class Stock {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;


    private String itemName;

    @Column
    private int quantity;

    @Column
    private String itemColor;
}
