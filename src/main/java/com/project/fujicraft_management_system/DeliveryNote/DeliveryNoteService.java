package com.project.fujicraft_management_system.DeliveryNote;

import com.project.fujicraft_management_system.DeliveryNote.dto.DeliveryNoteDto;
import com.project.fujicraft_management_system.Invoice.InvoiceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DeliveryNoteService {
    @Autowired
    DeliveryNoteRepository deliveryNoteRepository;

    @Autowired
    InvoiceService invoiceService;


    public List<DeliveryNote> saveDeliveryNote(DeliveryNoteDto deliveryNoteDto) {
        List<DeliveryNote> deliveryNote = new ArrayList<DeliveryNote>();
        deliveryNoteDto.getItems().forEach(d -> {
            d.getItem().forEach(i -> {
                DeliveryNote deliveryNoteObj = new DeliveryNote();
                deliveryNoteObj.setDeliveryDate(deliveryNoteDto.getDeliveryDate());
                deliveryNoteObj.setItemName(d.getItemName());
                deliveryNoteObj.setQuantity(i.getQuantity());
                deliveryNoteObj.setItemColor(i.getItemColor());
                deliveryNoteObj.setDescription(i.getDescription());
                deliveryNote.add(deliveryNoteObj);
            });
        });
        deliveryNoteRepository.saveAll(deliveryNote);
        invoiceService.createInvoiceDeliveryNote(deliveryNoteDto);
        return deliveryNote;
    }

    public List<DeliveryNote> getDeliveryNote(String itemName, String itemColor, String deliveryDate,String startDate,String endDate) throws UnsupportedEncodingException {
        List<DeliveryNote> deliveryNotes = new ArrayList<>();
        deliveryNoteRepository.findAll(Specification.where(itemNameEquals(itemName)).and(itemColorEquals(itemColor)).and(deliveryDateEquals(deliveryDate)).and(deliveryDateBetween(startDate,endDate))).forEach(updated -> deliveryNotes.add((DeliveryNote) updated));
        return deliveryNotes;
    }

    private Specification<DeliveryNote> itemNameEquals(final String itemName) {

        return StringUtils.isEmpty(itemName) ? null : (root, query, builder) -> builder.equal(root.get("itemName"), itemName);
    }



    public ResponseEntity<Object> updateInvoice(int id, DeliveryNote deliveryNote) {
        Optional<DeliveryNote> deliveryNote1 = deliveryNoteRepository.findById(id);
        DeliveryNote deliveryNoteObj = deliveryNote1.get();
        if (deliveryNoteObj != null) {
            deliveryNoteObj.setItemName(deliveryNote.getItemName());
            deliveryNoteObj.setItemColor(deliveryNote.getItemColor());
            deliveryNoteObj.setQuantity(deliveryNote.getQuantity());
            deliveryNoteObj.setDescription(deliveryNote.getDescription());
            deliveryNoteObj.setDeliveryDate(deliveryNote.getDeliveryDate());
            deliveryNoteObj.setId(id);
            return new ResponseEntity<>(deliveryNoteRepository.save(deliveryNoteObj), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Object> deleteDeliveryNote(int id) {
        try {
            //check if employee exist in database
            Optional<DeliveryNote> deliveryNote1 = deliveryNoteRepository.findById(id);
            DeliveryNote deliveryNote = deliveryNote1.get();

            if (deliveryNote1 != null) {
                deliveryNoteRepository.deleteById(deliveryNote.getId());
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private List<DeliveryNote> getDeliveryBody(String deliveryDate) {


        List<DeliveryNote> deliveryNotes = new ArrayList<>();
        deliveryNoteRepository.findAll(Specification.where(deliveryDateEquals(deliveryDate))).forEach(updated -> deliveryNotes.add((DeliveryNote) updated));
        return deliveryNotes;
    }
    private Specification<DeliveryNote> itemColorEquals(final String itemColor) {

        return StringUtils.isEmpty(itemColor) ? null : (root, query, builder) -> builder.equal(root.get("itemColor"), itemColor);
    }

    private Specification<DeliveryNote> deliveryDateEquals(final String deliveryDate) {
        if(!StringUtils.isEmpty(deliveryDate)){
            LocalDate localDate = LocalDate.parse(deliveryDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            return StringUtils.isEmpty(deliveryDate) ? null : (root, query, builder) -> builder.equal(root.get("deliveryDate"), localDate);

        }
        return null;
    }
    public Specification<DeliveryNote> deliveryDateBetween(String startDate, String endDate) {
        if(!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate) ){
            LocalDate localDateStart = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            LocalDate localDateEnd = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            return StringUtils.isEmpty(startDate) ||  StringUtils.isEmpty(endDate)  ? null : (root, query, builder) -> builder.between(root.get("deliveryDate"), localDateStart,localDateEnd);
        }
        return null;
    }

}


