package com.project.fujicraft_management_system.Common;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
public class Common {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;


    @Column
    private String[] itemNames;
    private String[] poNumbers;
    private String[] colors;
}
