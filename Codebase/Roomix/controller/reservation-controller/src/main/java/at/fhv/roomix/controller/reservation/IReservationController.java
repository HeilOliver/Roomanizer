package at.fhv.roomix.controller.reservation;

import at.fhv.roomix.controller.reservation.exeption.FaultException;
import at.fhv.roomix.controller.reservation.exeption.ValidationFault;
import at.fhv.roomix.controller.reservation.model.ContactPojo;

import java.util.Collection;

/**
 * Roomix
 * at.fhv.roomix.controller.reservation
 * IReservationController
 * 25/03/2018 OliverH
 * <p>
 * Enter Description here
 */
public interface IReservationController {

    void newContact(long sessionId, ContactPojo contactPojo) throws FaultException;

    Collection<ContactPojo> getAllContacts(long sessionId) throws FaultException;

    void updateContact(long sessionId, ContactPojo contactPojo) throws FaultException;
}