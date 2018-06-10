package at.fhv.roomix.controller.mapping;

import at.fhv.roomix.common.MapType;
import at.fhv.roomix.common.Mapper;
import at.fhv.roomix.common.MappingException;
import at.fhv.roomix.controller.model.*;
import at.fhv.roomix.domain.reservation.Arrangement;
import at.fhv.roomix.domain.reservation.ReservationUnit;
import at.fhv.roomix.domain.room.Room;
import at.fhv.roomix.domain.room.RoomCategory;
import at.fhv.roomix.persist.builder.accessbuilder.ArrangementBuilder;
import at.fhv.roomix.persist.builder.accessbuilder.RoomCategoryBuilder;
import at.fhv.roomix.persist.exception.BuilderLoadException;

import java.time.LocalDate;
import java.util.*;

/**
 * Roomix
 * at.fhv.roomix.implement.mapping
 * UnitMapping
 * 12/05/2018 Oliver
 * <p>
 *
 */
public class UnitMapping implements MapType<ReservationUnit, ReservationUnitPojo> {

    static {
        Mapper.getInstance().addMapType(new CategoryMapping(), RoomCategory.class, RoomCategoryPojo.class);
    }

    @Override
    public void mapReverse(ReservationUnitPojo source, ReservationUnit destination, Mapper mapper) throws MappingException {
        RoomCategory roomCategory = null;
        try {
            roomCategory = RoomCategoryBuilder.getRoomCategory(source.getRoomCategory().getId());
            destination.setCategory(roomCategory);
            destination.setStartDate(source.getStartDate());
            destination.setEndDate(source.getEndDate());
            destination.setPrice(source.getPrice() != null ? source.getPrice().getPrice() : 0);
            destination.setArrivalTime(source.getArrivalTime());
            destination.getArrangements().clear();
            for (ArrangementPojo pojo : source.getArrangements()) {
                Arrangement arrangement = ArrangementBuilder.getArrangement(pojo.getId());
                destination.getArrangements().add(arrangement);
            }
        } catch (BuilderLoadException e) {
            throw new MappingException("Cant Load:" + e.getMessage());
        }
    }

    @Override
    public void map(ReservationUnit source, ReservationUnitPojo destination, Mapper mapper) throws MappingException {
        destination.setArrivalTime(source.getArrivalTime());
        destination.setEndDate(source.getEndDate());
        destination.setStartDate(source.getStartDate());
        destination.setId(source.getId());
        destination.setRoomCategory(mapper.map(source.getCategory(), RoomCategoryPojo.class));

        destination.setArrangements(new HashSet<>());
        for (Arrangement arrangement : source.getArrangements()) {
            ArrangementPojo arrangementPojo = new ArrangementPojo();
            arrangementPojo.setDescription(arrangement.getDescription());
            arrangementPojo.setId(arrangement.getId());
            PricePojo pricePojo = new PricePojo();
            pricePojo.setPrice(arrangement.getPrice());
            arrangementPojo.setPrice(pricePojo);
            destination.getArrangements().add(arrangementPojo);
        }

        HashMap<LocalDate, Room> assignedRooms = source.getAssignedRooms();
        LinkedList<RoomPojo> rooms = new LinkedList<>();
        assignedRooms.entrySet().stream()
                .sorted(Comparator.comparing(localDateRoomEntry -> localDateRoomEntry.getKey().toEpochDay()))
                .forEach(entry -> {
                    RoomPojo last = null;
                    if (!rooms.isEmpty())
                        last = rooms.getLast();
                    if (last == null || !last.getRoomNo().equals(Integer.toString(entry.getValue().getId()))) {
                        RoomPojo roomPojo = new RoomPojo();
                        roomPojo.setRoomNo(Integer.toString(entry.getValue().getId()));
                        roomPojo.setStartDate(entry.getKey());
                        roomPojo.setEndDate(entry.getKey());
                        rooms.addLast(roomPojo);
                        return;
                    }
                    last.setEndDate(entry.getKey());
                });
        if (rooms.isEmpty()) {
            RoomPojo roomPojo = new RoomPojo();
            roomPojo.setStartDate(source.getStartDate());
            roomPojo.setEndDate(source.getEndDate());
            rooms.add(roomPojo);
        }

        destination.setAssignedRooms(rooms);
        destination.setCheckedIn(source.isEditAble());

        PricePojo pricePojo = new PricePojo();
        pricePojo.setPrice(source.getPrice());
        destination.setPrice(pricePojo);
    }
}
