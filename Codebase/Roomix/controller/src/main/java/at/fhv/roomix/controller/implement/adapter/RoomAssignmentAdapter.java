package at.fhv.roomix.controller.implement.adapter;

import at.fhv.roomix.domain.implement.IReservationUnit;
import at.fhv.roomix.domain.implement.IRoom;
import at.fhv.roomix.domain.implement.IRoomAssignment;
import at.fhv.roomix.domain.reservation.RoomAssignment;
import at.fhv.roomix.domain.room.Room;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Roomix
 * at.fhv.roomix.controller.implement.adapter
 * ${FILE_NAME}
 * 19/05/2018 OliverHeil
 * <p>
 * Enter Description here
 */
public class RoomAssignmentAdapter implements IRoomAssignment {
    private final RoomAssignment assignment;

    public RoomAssignmentAdapter(RoomAssignment assignment) {
        if (assignment == null) throw new IllegalArgumentException();
        this.assignment = assignment;
    }

    public RoomAssignment getAssignment() {
        return assignment;
    }

    @Override
    public int getRoomAssignmentId() {
        return assignment.getId();
    }

    @Override
    public String getArrivalDate() {
        return assignment.getArrivalDate().toString();
    }

    @Override
    public String getDepartureDate() {
        return assignment.getDepartureDate().toString();
    }

    @Override
    public int getRoomId() {
        return assignment.getAssignedRoom().getId();
    }

    @Override
    public int getReservationUnitId() {
        return assignment.getUnit().getId();
    }

    @Override
    public void setArrivalDate(Date date) {
        assignment.setArrivalDate(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

    @Override
    public void setDepartureDate(Date date) {
        assignment.setDepartureDate(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

    @Override
    public void setReservationUnit(IReservationUnit reservationUnit) {
        if (!(reservationUnit instanceof ReservationUnitAdapter)) throw new IllegalArgumentException();
        assignment.setUnit(((ReservationUnitAdapter) reservationUnit).getUnit());
    }

    @Override
    public IRoom getRoom() {
        return new RoomAdapter(assignment.getAssignedRoom());
    }

    @Override
    public void setRoom(IRoom room) {
        if (!(room instanceof RoomAdapter)) throw new IllegalArgumentException();
        assignment.setAssignedRoom(((RoomAdapter) room).getRoom());
    }
}