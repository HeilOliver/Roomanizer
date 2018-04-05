package at.fhv.roomix.ui.views.contact.list;

import at.fhv.roomix.ui.views.contact.ContactProvider;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Roomix
 * at.fhv.roomix.ui.views.contact.list
 * ContactDataToolbar
 * 27/03/2018 OliverH
 * <p>
 * Enter Description here
 */
public class ContactDataToolbarViewModel implements ViewModel {
    private final BooleanProperty contactSelected = new SimpleBooleanProperty();

    public ContactDataToolbarViewModel() {
        ContactProvider.getInstance().selectedContactProperty().addListener((observable, oldValue, newValue) -> {
            contactSelected.setValue(newValue != null);
        });
    }

    public BooleanProperty contactSelectedProperty() {
        return contactSelected;
    }
}