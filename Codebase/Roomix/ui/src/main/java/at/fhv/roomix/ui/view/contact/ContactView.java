package at.fhv.roomix.ui.view.contact;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

/**
 * Roomix
 * at.fhv.roomix.ui.view.contact
 * ContactView
 * 14/04/2018 Oliver
 * <p>
 * Enter Description here
 */
public class ContactView implements FxmlView<ContactViewModel> {

    @FXML
    private AnchorPane editView;
    @FXML
    private AnchorPane contentView;

    @InjectViewModel
    private ContactViewModel viewModel;

    public void initialize() {
        editView.visibleProperty().bind(viewModel.editViewVisibleProperty());
        contentView.visibleProperty().bind(viewModel.contentViewVisibleProperty());
    }
}
