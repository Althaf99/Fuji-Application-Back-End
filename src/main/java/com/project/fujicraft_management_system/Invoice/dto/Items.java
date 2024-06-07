package com.project.fujicraft_management_system.Invoice.dto;

import com.project.fujicraft_management_system.Request.dto.Item;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Items {
    private String itemName;

    private List<Item> item = new ArrayList<>();

}
