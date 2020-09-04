package org.museautomation.ui.editors.driver;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import org.museautomation.core.*;
import org.museautomation.selenium.*;
import org.museautomation.selenium.providers.*;
import org.museautomation.ui.extend.edit.*;
import org.museautomation.ui.extend.glyphs.*;
import org.museautomation.ui.extend.grid.*;
import org.slf4j.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
@SuppressWarnings("unused") // discovered via reflection
public class WebDriverProviderListEditor extends BaseResourceEditor
    {
    public WebDriverProviderListEditor()
        {
        _main = new BorderPane();
        _main.setPadding(new Insets(5));
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(5));
        grid.setVgap(5);
        grid.setHgap(5);

        ScrollPane scroller = new ScrollPane();
        scroller.setStyle("-fx-background-color:transparent;");
        scroller.setFitToWidth(true);
        scroller.setContent(grid);

        _main.setCenter(scroller);
        _rows = new GridPaneRows(grid);

        _add_button = new MenuButton("Add", Glyphs.create("FA:PLUS"));
        _add_button.setTooltip(new Tooltip("Select provider type"));
        _add_button.popupSideProperty().setValue(Side.BOTTOM);
        _add_button.setId(ADD_BUTTON_ID);
        _main.setTop(_add_button);
        BorderPane.setAlignment(_add_button, Pos.CENTER_LEFT);
        }

    @Override
    public void editResource(MuseProject project, MuseResource resource)
        {
        super.editResource(project, resource);
        _list = (WebDriverProviderList) resource;
        _list.addListener(_listener);

        // fill in add button options
        List<Class> provider_types = getProject().getClassLocator().getImplementors(WebDriverProvider.class);
        for (Class implementation : provider_types)
            {
            try
                {
                WebDriverProvider instance = (WebDriverProvider) implementation.getDeclaredConstructor().newInstance();
                MenuItem item = new MenuItem(instance.getName());
                item.setId(ADD_TYPE_ID_BASE + instance.getName());
                _add_button.getItems().add(item);
                item.setOnAction(event ->
                    {
                    try
                        {
                        WebDriverProvider new_provider = (WebDriverProvider) implementation.getDeclaredConstructor().newInstance();
                        new AddProviderAction(_list, new_provider).execute(getUndoStack());
                        }
                    catch (Exception e)
                        {
                        LOG.error(String.format("Unable to instantiate a %s. Does it have a public no-arg constructor?", implementation.getName()));
                        }
                    });
                }
            catch (Exception e)
                {
                LOG.error(String.format("Unable to instantiate a %s. Does it have a public no-arg constructor?", implementation.getName()));
                }
            }
        int row = 0;

        // build the providers
        for (WebDriverProvider provider : _list.getProviders())
            addRow(row++, provider);
        }

    private void addRow(int row_index, WebDriverProvider provider)
        {
        WebdriverProviderEditor editor;
        if (provider instanceof RemoteDriverProvider)
            editor = new RemoteDriverProviderEditor();
        else if (provider instanceof BaseLocalDriverProvider)
            editor = new LocalBinaryDriverProviderEditor();
        else
            editor = new UnEditableDriverProviderEditor();

        editor.edit(provider, getUndoStack());
        EditorRow row = new EditorRow(_rows, row_index);
        row.setEditor(editor);

        Button remove_button = Buttons.createRemove(DELETE_BUTTON_ID_BASE + row_index);
        row.setRemoveButton(remove_button);
        remove_button.setOnAction(event -> new RemoveProviderAction(_list, provider).execute(getUndoStack()));
        }

    @Override
    protected Parent getEditorArea()
        {
        return _main;
        }

    @Override
    public boolean canEdit(MuseResource resource)
        {
        return resource instanceof WebDriverProviderList;
        }

    @Override
    public ValidationStateSource getValidationStateSource()
        {
        return null;
        }

    @Override
    public void requestFocus()
        {
        if (_rows.size() > 0)
            ((EditorRow) _rows.getRow(0))._editor.getNode().requestFocus();
        else
            _add_button.requestFocus();
        }

    @Override
    public void dispose()
        {
        super.dispose();
        _list.removeListener(_listener);
        }

    private WebDriverProviderList _list;

    private BorderPane _main;
    private GridPaneRows _rows;
    private MenuButton _add_button;

    class EditorRow extends GridPaneRow
        {
        EditorRow(GridPaneRows rows, int row)
            {
            super(rows, row);
            _box = new VBox();
            _box.getStyleClass().add("editing-container");
            _box.setPadding(new Insets(5));
            GridPane.setHgrow(_box, Priority.ALWAYS);
            setNode(_box, 0);
            }

        public void setEditor(WebdriverProviderEditor editor)
            {
            _editor = editor;
            Label label = new Label(editor.getProvider().getName());
            Font font = label.getFont();
            label.setFont(Font.font(font.getName(), FontWeight.BOLD, font.getSize() + 2));
            _box.getChildren().add(label);
            _box.getChildren().add(_editor.getNode());
            }

        public void setRemoveButton(Button remove_button)
            {
            _remove_button = remove_button;
            GridPane.setMargin(_remove_button, new Insets(3, 0, 0, 5));
            GridPane.setValignment(_remove_button, VPos.TOP);
            setNode(_remove_button, 2);
            }

        WebdriverProviderEditor _editor;
        Button _remove_button;
        VBox _box;
        }

    WebDriverProviderList.ChangeListener _listener = new WebDriverProviderList.ChangeListener()
        {
        @Override
        public void providerAdded(int index, WebDriverProvider provider)
            {
            addRow(index, provider);
            }

        @Override
        public void providerRemoved(int index, WebDriverProvider provider)
            {
            _rows.remove(index);
            }
        };

    public final static String NEW_REMOTE_URL = "URL of remote provider (Selenium Grid)";

    public final static String ADD_BUTTON_ID = "wdple-add";
    public final static String ADD_TYPE_ID_BASE = "wdple-add-";
    public final static String DELETE_BUTTON_ID_BASE = "wdple-remove-";

    private final static Logger LOG = LoggerFactory.getLogger(WebDriverProviderListEditor.class);


    }
