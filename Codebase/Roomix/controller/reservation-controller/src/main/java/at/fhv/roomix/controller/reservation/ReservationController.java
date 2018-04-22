package at.fhv.roomix.controller.reservation;

import at.fhv.roomix.controller.common.exceptions.ArgumentFaultException;
import at.fhv.roomix.controller.common.exceptions.SessionFaultException;
import at.fhv.roomix.controller.common.exceptions.ValidationFault;
import at.fhv.roomix.controller.contact.model.ContactPojo;
import at.fhv.roomix.controller.reservation.model.*;
import at.fhv.roomix.domain.guest.model.*;
import at.fhv.roomix.domain.session.ISessionDomain;
import at.fhv.roomix.domain.session.SessionFactory;
import at.fhv.roomix.persist.factory.*;
import at.fhv.roomix.persist.model.*;
import org.modelmapper.ModelMapper;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static at.fhv.roomix.controller.common.validator.Validator.validate;

/**
 * Roomix
 * at.fhv.roomix.controller.session
 * ReservationController
 * 18.04.2018 Robert S.
 * <p>
 * The Implementation for the ReservationController itself
 */

class ReservationController implements IReservationController {
    private final ISessionDomain sessionHandler = SessionFactory.getInstance();

    @Override
    public Collection<ReservationPojo> getAllReservation(long sessionId) throws SessionFaultException {
        if (!sessionHandler.isValidFor(sessionId, null)) throw new SessionFaultException();

        IAbstractDomainBuilder<ReservationDomain, ReservationEntity> reservationBuilder = ReservationDomainBuilder.getInstance();
        ModelMapper modelMapper = new ModelMapper();
        HashSet<ReservationDomain> reservationDomainSet = new HashSet<>(reservationBuilder.getAll());
        HashSet<ReservationPojo> reservationPojoSet = new HashSet<>();
        reservationDomainSet.forEach(reservation -> {
            ReservationPojo reservationPojo = modelMapper.map(reservation, ReservationPojo.class);
            CommentPojo commentPojo = new CommentPojo();
            commentPojo.setComment(reservation.getReservationComment());
            reservationPojo.setComment(commentPojo);
            reservationPojoSet.add(reservationPojo);
        });
        return reservationPojoSet;
    }

    @Override
    public Collection<ReservationPojo> getSearchedReservation(long sessionId, String query) throws SessionFaultException {
        if (!sessionHandler.isValidFor(sessionId, null)) throw new SessionFaultException();

        Collection<ReservationPojo> reservationPojoSet = getAllReservation(sessionId);

        String[] split = query.toLowerCase().split(" ");

        Set<ReservationPojo> resultSet = new HashSet<>(reservationPojoSet);
        for (String splitedQuery : split) {
            resultSet = resultSet.stream()
                    // TODO master oli, collection in einer collection suchen mit viel for und so?!
                    .filter(c -> c.getContractingParty().toString().toLowerCase().contains(splitedQuery) ||
                            c.getPersons().toString().toLowerCase().contains(splitedQuery) ||
                            c.getComment().toString().toLowerCase().contains(splitedQuery) ||
                            c.getUnits().toString().toLowerCase().contains(splitedQuery))
                    .collect(Collectors.toSet());
        }

        return resultSet;
    }

    @Override
    public PricePojo getPrice(long sessionId, ReservationUnitPojo reservationUnit, ContactPojo contractingParty) throws SessionFaultException {
        if (!sessionHandler.isValidFor(sessionId, null)) throw new SessionFaultException();
        if (contractingParty == null) {
            contractingParty = new ContactPojo();
            contractingParty.setContactId(0);
        }

        RoomCategoryPojo roomCategoryPojo = reservationUnit.getRoomCategory();
        IAbstractDomainBuilder<RoomCategoryDomain,RoomCategoryEntity> roomCategoryBuilder = RoomCategoryDomainBuilder.getInstance();
        ModelMapper modelMapper = new ModelMapper();
        GuestDomain guestDomain = modelMapper.map(contractingParty, GuestDomain.class);
        RoomCategoryDomain roomCategoryDomain = roomCategoryBuilder.get(roomCategoryPojo.getId());
        PricePojo pricePojo = new PricePojo();

        roomCategoryDomain.setCategoryMetaData(reservationUnit.getStartDate(),reservationUnit.getEndDate(), guestDomain);
        int price = roomCategoryDomain.getMetaData().getPricePerDay()*(int)Duration.ofDays(ChronoUnit.DAYS.between(reservationUnit.getStartDate(),reservationUnit.getEndDate())).toDays();
        pricePojo.setPrice(price);
        return pricePojo;
    }

    @Override
    public Collection<RoomCategoryPojo> getSearchedCategory(long sessionId, LocalDate startDate, LocalDate endDate, ContactPojo contractingParty) throws SessionFaultException {
        if (!sessionHandler.isValidFor(sessionId, null)) throw new SessionFaultException();
        if (contractingParty == null) {
            contractingParty = new ContactPojo();
            contractingParty.setContactId(0);
        }
        IAbstractDomainBuilder<RoomCategoryDomain,RoomCategoryEntity> roomCategoryBuilder = RoomCategoryDomainBuilder.getInstance();
        ModelMapper modelMapper = new ModelMapper();
        GuestDomain guestDomain = modelMapper.map(contractingParty, GuestDomain.class);
        HashSet<RoomCategoryDomain> roomCategoryDomainSet = new HashSet<>(roomCategoryBuilder.getAll());
        HashSet<RoomCategoryPojo> roomCategoryset = new HashSet<>();

        for (RoomCategoryDomain roomCategoryDomain : roomCategoryDomainSet) {
            roomCategoryDomain.setCategoryMetaData(startDate,endDate, guestDomain);
            RoomCategoryPojo roomCategoryPojo = new RoomCategoryPojo();
            roomCategoryPojo.setDescription(roomCategoryDomain.getCategoryDescription());
            roomCategoryPojo.setQuota(roomCategoryDomain.getMetaData().getContingent());
            roomCategoryPojo.setOccupied(roomCategoryDomain.getMetaData().getNumberOfOccupiedRooms());
            roomCategoryPojo.setUnconfirmedReservation(roomCategoryDomain.getMetaData().getNumberOfUnconfirmedReservations());
            roomCategoryPojo.setConfirmedReservation(roomCategoryDomain.getMetaData().getNumberOfConfirmedReservations());
            roomCategoryPojo.setFree(roomCategoryDomain.getMetaData().getTotalNumberOfRooms()-
                                     roomCategoryDomain.getMetaData().getNumberOfConfirmedReservations()-
                                     roomCategoryDomain.getMetaData().getNumberOfOccupiedRooms()-
                                     roomCategoryDomain.getMetaData().getNumberOfUnconfirmedReservations());
            roomCategoryset.add(roomCategoryPojo);
        }

        return roomCategoryset;
    }


    @Override
    public Collection<RoomCategoryPojo> getAllCategory(long sessionId) throws SessionFaultException {
        if (!sessionHandler.isValidFor(sessionId, null)) throw new SessionFaultException();

        IAbstractDomainBuilder<RoomCategoryDomain, RoomCategoryEntity> roomCategoryBuilder = RoomCategoryDomainBuilder.getInstance();
        ModelMapper modelMapper = new ModelMapper();
        HashSet<RoomCategoryDomain> roomCategoryDomainSet = new HashSet<>(roomCategoryBuilder.getAll());
        HashSet<RoomCategoryPojo> roomCategoryPojoSet = new HashSet<>();

        roomCategoryDomainSet.forEach(roomCategory -> roomCategoryPojoSet.add(modelMapper.map(roomCategory, RoomCategoryPojo.class)));

        return roomCategoryPojoSet;
    }

    @Override
    public Collection<ArrangementPojo> getAllArrangement(long sessionId) throws SessionFaultException {
        if (!sessionHandler.isValidFor(sessionId, null)) throw new SessionFaultException();
        IAbstractDomainBuilder<ArrangementDomain, ArrangementEntity> arrangementBuilder = ArrangementDomainBuilder.getInstance();
        ModelMapper modelMapper = new ModelMapper();
        HashSet<ArrangementDomain> arrangementDomainSet = new HashSet<>(arrangementBuilder.getAll());
        HashSet<ArrangementPojo> arrangementPojoSet = new HashSet<>();

        arrangementDomainSet.forEach(arrangement -> {
                ArrangementPojo arrangementPojo = modelMapper.map(arrangement, ArrangementPojo.class);
                PricePojo price = new PricePojo();
                price.setPrice(arrangement.getArrangementPrice());
                DiscountPojo discount = new DiscountPojo();
                discount.setDiscount(arrangement.getDiscount());
                arrangementPojo.setDiscount(discount);
                arrangementPojo.setPrice(price);
                arrangementPojoSet.add(arrangementPojo);
        });
        return arrangementPojoSet;
    }

    @Override
    public void updateReservation(long sessionId, ReservationPojo reservationPojo) throws SessionFaultException, ValidationFault, ArgumentFaultException {

        if (reservationPojo == null) throw new ArgumentFaultException();
        validate(reservationPojo);
        if (!sessionHandler.isValidFor(sessionId, null)) throw new SessionFaultException();

        IAbstractDomainBuilder<ReservationDomain, ReservationEntity> reservationBuilder = ReservationDomainBuilder.getInstance();
        ModelMapper modelMapper = new ModelMapper();

        ReservationDomain reservationDomain = modelMapper.map(reservationPojo, ReservationDomain.class);

        reservationBuilder.set(reservationDomain);
    }
}
