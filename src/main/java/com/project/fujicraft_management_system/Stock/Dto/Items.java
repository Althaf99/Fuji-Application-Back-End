package com.project.fujicraft_management_system.Stock.Dto;

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
    private String  itemName;

    private List<Item> item;

    public static class Item {
        private String itemColor;
        private String quantity;

        public String getItemColor() {
            return itemColor;
        }

        public void setItemColor(String itemColor) {
            this.itemColor = itemColor;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public List<Item> getItem() {
        return item;
    }

    public void setItem(List<Item> item) {
        this.item = item;
    }
}
