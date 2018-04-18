package at.fhv.roomix.ui.view.main;

import at.fhv.roomix.ui.view.main.menuitem.SideBarItem;
import at.fhv.roomix.ui.view.main.menuitem.SideBarItemViewModel;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import at.fhv.roomix.ui.common.StringResourceResolver;

import javax.inject.Inject;
import java.util.ResourceBundle;


/**
 * Roomix
 * at.fhv.roomix.ui.views
 * MainView
 * 24/03/2018 Oliver
 * <p>
 * Enter Description here
 */
public class MainView implements FxmlView<MainViewModel> {

    @InjectViewModel
    private MainViewModel viewModel;

    @FXML
    private AnchorPane contentPane;
    @FXML
    private Label lbl_header;
    @FXML
    private VBox topBox;

    private CachedViewModelCellFactory<SideBarItem, SideBarItemViewModel> cellFactory;
    @FXML
    private VBox bottomBox;

    @Inject
    private ResourceBundle resourceBundle;


    private void setChildren(Parent parent) {
        contentPane.getChildren().clear();
        if (parent == null) return;
        AnchorPane.setTopAnchor(parent, 0.0);
        AnchorPane.setBottomAnchor(parent, 0.0);
        AnchorPane.setLeftAnchor(parent, 0.0);
        AnchorPane.setRightAnchor(parent, 0.0);
        contentPane.getChildren().add(parent);
    }

    public void initialize() {
        cellFactory = CachedViewModelCellFactory.createForFxmlView(SideBarItem.class);

        viewModel.currentViewProperty().addListener(((observable, oldValue, newValue) -> {
            setChildren(newValue);
        }));
        setChildren(viewModel.currentViewProperty().get());

        viewModel.getTopItems().forEach((i) -> {
            Parent item = cellFactory.map(i).getView();
            topBox.getChildren().add(item);
        });

        viewModel.getBottomItems().forEach((i) -> {
            Parent item = cellFactory.map(i).getView();
            bottomBox.getChildren().add(item);
        });

        lbl_header.textProperty().bind(StringResourceResolver.getAnonymousProperty(resourceBundle, viewModel.headerProperty()));
    }

    @FXML
    private void onMenuButton_Click(MouseEvent mouseEvent) {
        viewModel.switchMenuState();
    }
}
