package com.project.fujicraft_management_system.DeliveryNote;

import com.project.fujicraft_management_system.DeliveryNote.dto.DeliveryNoteDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class DeliveryNoteController {

    @Autowired
    DeliveryNoteService deliveryNoteService;

    @PostMapping("/deliveryNote")
    private List<DeliveryNote> saveDelivery(@RequestBody DeliveryNoteDto deliveryNoteDto ){
        return deliveryNoteService.saveDeliveryNote(deliveryNoteDto);
    }

    @GetMapping("/deliveryNote")
    private List<DeliveryNote> getDeliveryNote(@RequestParam(required = false) String itemName, @RequestParam(required = false) String itemColor, @RequestParam(required = false) String deliveryDate, @RequestParam(required = false) String startDate, @RequestParam(required = false) String
            endDate) throws UnsupportedEncodingException {
        return deliveryNoteService.getDeliveryNote( itemName, itemColor, deliveryDate,startDate,endDate);
    }

    @PutMapping("/deliveryNote/{id}")
    private ResponseEntity<Object> updateInvoice(@PathVariable("id") int id, @RequestBody DeliveryNote deliveryNote){
        return deliveryNoteService.updateInvoice(id,deliveryNote);
    }

    @DeleteMapping("/deliveryNote/{id}")
    private ResponseEntity<Object> deleteRequest(@PathVariable("id") int id){
        return deliveryNoteService.deleteDeliveryNote(id);
    }

}
