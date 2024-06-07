package com.project.fujicraft_management_system.Invoice;

import com.project.fujicraft_management_system.Invoice.dto.Excess;
import com.project.fujicraft_management_system.Invoice.dto.InvoiceDto;
import com.project.fujicraft_management_system.Invoice.dto.Items;
import com.project.fujicraft_management_system.Invoice.dto.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class InvoiceController {

    @Autowired
    InvoiceService invoiceService;



    @GetMapping("/invoiceList")
    private List<Invoice> getInvoice(@RequestParam(required = false) String itemName, @RequestParam(required = false) String itemColor, @RequestParam(required = false) String po, @RequestParam(required = false) String poDate,@RequestParam(required = false) String invoiceDate,@RequestParam(required = false) Integer invoiceNo,@RequestParam(required = false) String startDate, @RequestParam(required = false) String
            endDate){
        return invoiceService.getInvoice( itemName, itemColor, po, poDate,invoiceDate, invoiceNo , startDate, endDate);
    }

    @GetMapping("/invoice/{invoiceNo}")
    private List<Invoice> getInvoiceByInvoiceNo(@PathVariable("invoiceNo") Integer invoiceNo){
        return invoiceService.getInvoiceByInvoiceNo(invoiceNo);
    }

    @GetMapping("/excess")
    private List<Excess> getExcess(@RequestParam(required = false) String itemName, @RequestParam(required = false) String itemColor, @RequestParam(required = false) String excessDeliveredDate,@RequestParam(required = false) String startDate, @RequestParam(required = false) String
            endDate){
        return invoiceService.getExcess( itemName, itemColor,excessDeliveredDate, startDate, endDate);
    }


    @DeleteMapping("/invoice/{id}")
    private ResponseEntity<Object> deleteInvoice(@PathVariable("id") int id){
        return invoiceService.deleteInvoiceItem(id);
    }

    @DeleteMapping("/excess/{id}")
    private ResponseEntity<Object> deleteExcess(@PathVariable("id") int id){
        return invoiceService.deleteExcessItem(id);
    }

    @PutMapping("/invoice/{id}")
    private ResponseEntity<Object> updateInvoice(@PathVariable("id") int id,@RequestBody Invoice invoice){
        return invoiceService.updateInvoiceItem(id,invoice);
    }

    @PutMapping("/excess/{id}")
    private ResponseEntity<Object> updateExcess(@PathVariable("id") int id,@RequestBody Excess excess){
        return invoiceService.updateExcessItem(id,excess);
    }

    @PutMapping("/AddInvoice/{po}/{invoiceDate}/{invoiceNo}")
    private ResponseEntity<Object> addInvoiceNumber(@PathVariable("po") String po,@PathVariable("invoiceDate") String invoiceDate,@PathVariable("invoiceNo") int invoiceNo){
        return invoiceService.addInvoiceNumber(po,invoiceDate,invoiceNo);
    }


}
