package at.fhv.roomix.ui.view.reservation.edit.unit;

import at.fhv.roomix.controller.reservation.model.ArrangementPojo;
import at.fhv.roomix.controller.reservation.model.PricePojo;
import at.fhv.roomix.controller.reservation.model.ReservationUnitPojo;
import at.fhv.roomix.controller.reservation.model.RoomCategoryPojo;
import at.fhv.roomix.ui.common.StringResourceResolver;
import at.fhv.roomix.ui.common.validator.DateValidator;
import at.fhv.roomix.ui.dataprovider.ReservationUnitProvider;
import at.fhv.roomix.ui.view.reservation.edit.ReservationEditScope;
import at.fhv.roomix.ui.view.reservation.edit.SubscribeAbleViewModel;
import de.saxsys.mvvmfx.InjectResourceBundle;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.utils.itemlist.ListTransformation;
import de.saxsys.mvvmfx.utils.validation.CompositeValidator;
import de.saxsys.mvvmfx.utils.validation.FunctionBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ValidationMessage;
import de.saxsys.mvvmfx.utils.validation.Validator;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.ResourceBundle;


/**
 * Roomix
 * at.fhv.roomix.ui.view.reservation.edit.unit
 * UnitViewModel
 * 21/04/2018 Oliver
 * <p>
 * Enter Description here
 */
public class UnitViewModel extends SubscribeAbleViewModel<ReservationUnitPojo> {

    private final ReservationUnitProvider provider;
    private final ObservableList<CategoryItemViewModel> roomCategories;

    @InjectResourceBundle
    private ResourceBundle resourceBundle;
    @InjectScope
    private ReservationEditScope scope;

    private StringProperty duration = new SimpleStringProperty();
    private CompositeValidator formValidator = new CompositeValidator();
    private StringProperty arrivalTime = new SimpleStringProperty();
    private ObservableList<PacketsItemViewModel> articleList;
    private ObservableList<XYChart.Series<Number, String>> availableRooms = FXCollections.observableArrayList();
    private StringProperty currCategoryPrice = new SimpleStringProperty();

    public UnitViewModel() {
        provider = new ReservationUnitProvider();

        ListTransformation<RoomCategoryPojo, CategoryItemViewModel> transRoomCategory
                = new ListTransformation<>(provider.getPossibleCategories(), CategoryItemViewModel::new);
        roomCategories = transRoomCategory.getTargetList();

        ListTransformation<ArrangementPojo, PacketsItemViewModel> transArticle
                = new ListTransformation<>(provider.getPossibleArrangements(), PacketsItemViewModel::new);
        articleList = transArticle.getTargetList();
    }

    ObjectProperty<LocalDate> arrivalDateProperty() {
        return pojoWrapper.field("arrivalDate",
                ReservationUnitPojo::getStartDate, ReservationUnitPojo::setStartDate);
    }

    ObjectProperty<LocalDate> departureDateProperty() {
        return pojoWrapper.field("departureDate",
                ReservationUnitPojo::getEndDate, ReservationUnitPojo::setEndDate);
    }

    ObjectProperty<RoomCategoryPojo> categoryProperty() {
        return pojoWrapper.field("roomCategory",
                ReservationUnitPojo::getRoomCategory, ReservationUnitPojo::setRoomCategory);
    }

    ObjectProperty<PricePojo> priceProperty() {
        return pojoWrapper.field("price",
                ReservationUnitPojo::getPrice, ReservationUnitPojo::setPrice);
    }

    private ObjectProperty<LocalTime> arrivalTime() {
        return pojoWrapper.field("arrivalTime",
                ReservationUnitPojo::getArrivalTime, ReservationUnitPojo::setArrivalTime);
    }

    StringProperty arrivalTimeProperty() {
        return arrivalTime;
    }

    ReadOnlyStringProperty durationProperty() {
        return duration;
    }

    private void calculateDuration() {
        if (arrivalDateProperty().get() == null ||
                departureDateProperty().get() == null) {
            duration.setValue("?");
            return;
        }
        Duration days = Duration.ofDays(ChronoUnit.DAYS.between(arrivalDateProperty().get(), departureDateProperty().get()));
        duration.setValue(days.toDays() + " " + StringResourceResolver.getStaticResolve(resourceBundle, "reservation.days"));

        provider.loadCategories(arrivalDateProperty().get(), departureDateProperty().get(), scope.getContractingParty());
    }

    public void initialize() {
        arrivalDateProperty().addListener((observable) -> calculateDuration());
        departureDateProperty().addListener((observable) -> calculateDuration());

        arrivalTime.addListener(((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            String trimmedString = newValue.trim();

            if (trimmedString.isEmpty()) return;
            if (!trimmedString.matches("^(\\d)+$")) {
                arrivalTime.setValue(oldValue);
                return;
            }

            int i = Integer.parseInt(trimmedString);
            if (i > 24 || i < 0) {
                arrivalTime.setValue(oldValue);
                return;
            }
            LocalTime parse;
            if (i < 10) {
                parse = LocalTime.parse("0" + i + ":00:00");
            } else {
                parse = LocalTime.parse(i + ":00:00");
            }
            arrivalTime().setValue(parse);
        }));

        Validator arrivalDateValidator = new DateValidator(arrivalDateProperty());
        Validator departureDateValidator = new DateValidator(departureDateProperty());
        Validator arrivalTimeValidator = new FunctionBasedValidator<>(
                arrivalTime(), Objects::nonNull, ValidationMessage.error(""));

        formValidator = new CompositeValidator(
                arrivalDateValidator,
                departureDateValidator,
                arrivalTimeValidator
        );

        isValid = formValidator.getValidationStatus().validProperty();

        categoryProperty().addListener(((observable, oldValue, newValue) -> {
            availableRooms.clear();
            currCategoryPrice.setValue("");
            if (newValue == null) return;
            XYChart.Series<Number, String> series = new XYChart.Series<>();
            series.getData().add(new XYChart.Data<>(newValue.getFree(),
                    StringResourceResolver.getStaticResolve(resourceBundle, "reservation.edit.unit.freerooms")));
            series.getData().add(new XYChart.Data<>(newValue.getQuota(),
                    StringResourceResolver.getStaticResolve(resourceBundle, "reservation.edit.unit.quotarooms")));
            series.getData().add(new XYChart.Data<>(newValue.getOccupied(),
                    StringResourceResolver.getStaticResolve(resourceBundle, "reservation.edit.unit.occupiedrooms")));
            series.getData().add(new XYChart.Data<>(newValue.getUnconfirmedReservation(),
                    StringResourceResolver.getStaticResolve(resourceBundle, "reservation.edit.unit.unconfirmedrooms")));
            availableRooms.add(series);
            currCategoryPrice.setValue(Float.toString(newValue.getPricePerDay() / 100));
        }));


    }

    @Override
    protected void afterSubscribe(boolean isNew) {
        provider.clear();
        provider.loadArrangements();
    }

    ReadOnlyBooleanProperty getContactInLoad() {
        return provider.inLoadCategoriesProperty();
    }

    ReadOnlyBooleanProperty getArrangementsInLoad() {
        return provider.inLoadArrangementsProperty();
    }

    ObservableList<PacketsItemViewModel> getArticleList() {
        return articleList;
    }

    ObservableList<CategoryItemViewModel> getRoomCategories() {
        return roomCategories;
    }

    ObservableList<XYChart.Series<Number, String>> getAvailableRooms() {
        return availableRooms;
    }

    public ReadOnlyStringProperty currCategoryPriceProperty() {
        return currCategoryPrice;
    }

    void onCommit() {
        commit();
        provider.calculatePrice((price) -> {
            priceProperty().setValue(price);
            commit();
        }, currModel.get(), scope.getContractingParty());
    }
}
