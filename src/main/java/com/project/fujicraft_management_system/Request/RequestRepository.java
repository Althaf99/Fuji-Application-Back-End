package com.project.fujicraft_management_system.Request;

import com.project.fujicraft_management_system.Request.dto.MergedItemDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request,Integer>, JpaSpecificationExecutor<Request> {
    Optional<Request> findByPo(String po);
    Optional<Request> findByPoAndItemNameAndItemColor(String po, String itemName,String itemColor);

    @Query("SELECT new com.project.fujicraft_management_system.Request.dto.MergedItemDetails( " +
            "COALESCE(pb.itemName, sb.itemName), " +
            "COALESCE(pb.itemColor, sb.itemColor), " +
            "pb.quantity, " +
            "sb.quantity, " +
            "iname.cavity, " +
            "iname.weightPerPiece, " +
            "iname.cycleTime) " +
            "FROM Request pb " +
            "FULL JOIN Stock sb ON pb.itemName = sb.itemName AND pb.itemColor = sb.itemColor " +
            "LEFT JOIN ItemNames iname ON COALESCE(pb.itemName, sb.itemName) = iname.itemName")
    List<MergedItemDetails> getMergedItemDetails();






}