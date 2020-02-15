package org.museautomation.ui.step;

import com.google.common.base.Objects;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import org.museautomation.ui.step.actions.*;
import org.museautomation.ui.valuesource.*;
import org.museautomation.ui.valuesource.list.*;
import org.museautomation.ui.valuesource.map.*;
import org.museautomation.builtins.value.collection.*;
import org.museautomation.core.step.*;
import org.museautomation.core.step.descriptor.*;
import org.museautomation.core.step.events.*;
import org.museautomation.core.values.*;
import org.museautomation.core.values.descriptor.*;
import org.museautomation.ui.extend.actions.*;
import org.museautomation.ui.extend.components.*;
import org.museautomation.ui.extend.edit.*;
import org.museautomation.ui.extend.edit.metadata.*;
import org.museautomation.ui.extend.edit.stack.*;
import org.museautomation.ui.extend.edit.step.*;

import java.util.*;

/**
 * @author Christopher L Merrill (see LICENSE.txt for license details)
 */
public class DefaultStepEditor implements StackableEditor
    {
    public DefaultStepEditor(StepEditContext context)
        {
        _context = context;

        _grid = new GridPane();
        _grid.setPadding(new Insets(10, 10, 10, 20));
        _grid.setHgap(5);
        _grid.setVgap(5);

        _description = new TextField();
        _description.setId(DESCRIPTION_FIELD_ID);
        _description.setPromptText("enter a custom step description");
        _description.focusedProperty().addListener((observable, oldValue, newValue) ->
            {
            if (_step != null)
                {
                String description = _description.getText();
                if (description != null)
                    {
                    description = description.trim();
                    if (description.length() == 0)
                        description = null;
                    }
                Object original = _step.getMetadataField(StepConfiguration.META_DESCRIPTION);
                if (!Objects.equal(description, original))
                    new ChangeStepMetadataAction(_step, description).execute(_context.getUndo());
                }
            });

        _tags = new StepTagEditor(context);
        _metadata = new MetadataEditor();
        _metadata.setAddListener((name, value) -> new AddMetadataAction(_step, name, value).execute(_context.getUndo()));
        _metadata.setRemoveListener((name, value) -> new RemoveMetadataAction(_step, name).execute(_context.getUndo()));

        _type_chooser = new StepTypeEditor();
        _type_chooser.getButton().setId(TYPE_FIELD_ID);
        _type_chooser.setChangeHandler(new_type ->
            {
            CompoundAction action = new CompoundAction();
            action.addAction(new ChangeStepTypeAction(_step, new_type));
            action.addAction(new UpgradeStepToDescriptorComplianceAction(_step, _context.getProject()));
            action.execute(_context.getUndo());
            createParamEditors();
            layoutControls();
            });

        _step_type_short_description = new Text();
        _step_type_short_description.setId(HELP_TYPE_SHORT_DESCRIPTION);
        _step_description_tooltip = new Tooltip();
        Tooltip.install(_step_type_short_description, _step_description_tooltip);

        layoutControls();
        }

    private void layoutControls()
        {
        _grid.getChildren().clear();

        int row = 0;
        _grid.add(new Label("Description"), 0, row);
        _grid.add(_description, 1, row);
        GridPane.setHgrow(_description, Priority.ALWAYS);

        row = 1;
        _grid.add(new Label("Tags"), 0, row);
        _grid.add(_tags.getNode(), 1, row);

        row = 2;
        _grid.add(new Label("Attributes"), 0, row);
        _grid.add(_metadata.getNode(), 1, row);

        row = 3;
        _grid.add(new Label("Step Type"), 0, row);
        HBox line2 = new HBox();
        line2.setSpacing(5);
        _grid.add(line2, 1, row);
        line2.getChildren().add(_type_chooser.getButton());
        line2.getChildren().add(_step_type_short_description);

        row = 4;
        for (int i = 0; i < _editor_nodes.length; i++)
            {
            _grid.add(_name_nodes[i], 0, row);
            _grid.add(_editor_nodes[i], 1, row);
            row++;
            }

        if (_mode_switch_link != null)
            {
            GridPane.setHalignment(_mode_switch_link, HPos.RIGHT);
            _grid.add(_mode_switch_link, 1, row);
            }
        }

    public void addModeSwitchLink(Node link)
        {
        _mode_switch_link = link;
        layoutControls();
        }

    public void setStep(StepConfiguration step)
        {
        _step = step;
        _step.addChangeListener(new StepChangeObserver()
            {
            @Override
            public void typeChanged(TypeChangeEvent event, String old_type, String new_type)
                {
                StepDescriptor step_descriptor = _context.getProject().getStepDescriptors().get(new_type);
                setupStepTypeHelp(step_descriptor);
                }
            });

        Platform.runLater(this::activate);
        }

    /**
     * Upgrades the step
     */
    private void upgradeStep()
        {
        UpgradeStepToDescriptorComplianceAction upgrade_action = new UpgradeStepToDescriptorComplianceAction(_step, _context.getProject());
        if (upgrade_action.isUpgradeNeeded())
            upgrade_action.execute(_context.getUndo());
        }

    @Override
    public Node getNode()
        {
        return _grid;
        }

    @Override
    public void setStack(EditorStack stack)
        {
        _editor_stack = stack;
        }

    @Override
    public void requestFocus()
        {
        _description.requestFocus();
        }

    @Override
    public void activate()
        {
        upgradeStep();
        if (_step != null)
            {
            StepConfiguration step = _step;
            Object description = step.getMetadataField(StepConfiguration.META_DESCRIPTION);
            if (description == null)
                _description.setText(null);
            else
                _description.setText(description.toString());
            _tags.setStep(step);
            _step_meta_adapter = new StepMetadataAdapter(step);
            _metadata.setMetadata(_step_meta_adapter);
            _type_chooser.setProject(_context.getProject());
            _type_chooser.setType(step.getType());

            StepDescriptor step_descriptor = _context.getProject().getStepDescriptors().get(step);
            setupStepTypeHelp(step_descriptor);

            createParamEditors(step, step_descriptor);
            Platform.runLater(this::layoutControls);
            }
        }

    private void createParamEditors()
        {
        StepConfiguration step = _step;
        createParamEditors(step, _context.getProject().getStepDescriptors().get(step));
        }

    private void createParamEditors(StepConfiguration step, StepDescriptor step_descriptor)
        {
        SubsourceDescriptor[] descriptors = step_descriptor.getSubsourceDescriptors();
        _editor_nodes = new Node[descriptors.length];
        _name_nodes = new Node[descriptors.length];
        _validatables = new Validatable[descriptors.length];
        List<String> reserved_names = new ArrayList<>();
        ValueSourceMapEditor map_editor = null;

        for (int i = 0; i < descriptors.length; i++)
            {
            SubsourceDescriptor descriptor = descriptors[i];
            switch (descriptor.getType())
                {
                case Named:
                    FixedNameValueSourceEditor named_editor = new FixedNameValueSourceEditor(_context.getProject(), _context.getUndo(), descriptor, step);
                    named_editor.setSource(step.getSource(descriptor.getName()));
                    named_editor.setStack(_editor_stack);
                    _editor_nodes[i] = named_editor.getNode();
                    _name_nodes[i] = named_editor.getNameLabel();
                    _validatables[i] = named_editor;
                    reserved_names.add(descriptor.getName());
                    break;
                case Map:
                    map_editor = new ValueSourceMapEditor(_context.getProject(), _context.getUndo());
                    ValueSourceConfiguration fake_source = new ValueSourceConfiguration();
                    fake_source.setSourceMap(step.getSources());
                    map_editor.setSource(fake_source);
                    map_editor.setStack(_editor_stack);
                    _editor_nodes[i] = map_editor.getNode();
                    _name_nodes[i] = new Label(descriptor.getDisplayName());
                    GridPane.setValignment(_name_nodes[i], VPos.TOP);
                    GridPane.setMargin(_name_nodes[i], new Insets(5, 0, 0, 0));
                    _validatables[i] = map_editor;
                    break;
                case List:
                    ValueSourceListEditor list_editor = new ValueSourceListEditor(_context.getProject(), _context.getUndo());
                    ValueSourceConfiguration list = step.getSource(descriptor.getName());
                    if (list == null)
                        {
                        list = ValueSourceConfiguration.forType(ListSource.TYPE_ID);
                        step.addSource(descriptor.getName(), list);
                        }
                    list_editor.setSource(list);
                    list_editor.setStack(_editor_stack);
                    _editor_nodes[i] = list_editor.getNode();
                    _name_nodes[i] = new Label(descriptor.getDisplayName());
                    GridPane.setValignment(_name_nodes[i], VPos.TOP);
                    GridPane.setMargin(_name_nodes[i], new Insets(5, 0, 0, 0));
                    _validatables[i] = list_editor;
                    reserved_names.add(descriptor.getName());
                    break;
                }
            }

        if (map_editor != null)
            for (String name : reserved_names)
                map_editor.hideSourceNamed(name);
        }

    private void setupStepTypeHelp(StepDescriptor step_descriptor)
        {
        _step_type_short_description.setText(step_descriptor.getShortDescription());
        if (step_descriptor.getLongDescription() == null)
            _step_description_tooltip.setText(null);
        else
            _step_description_tooltip.setText(new WordWrapper(step_descriptor.getLongDescription()).wrapAfter(80));
        }

    @Override
    public boolean isValid()
        {
        for (Validatable validatable : _validatables)
            if (!validatable.isValid())
                return false;
        return true;
        }

    public void destroy()
	    {
	    _tags.setStep(null);
	    _step_meta_adapter.destroy();
	    }

    private final StepEditContext _context;
    private StepConfiguration _step;
    private EditorStack _editor_stack;

    private TextField _description;
    private StepTagEditor _tags;
    private StepMetadataAdapter _step_meta_adapter;
    private MetadataEditor _metadata;
    private final GridPane _grid;
    private final StepTypeEditor _type_chooser;
    private Node _mode_switch_link = null;
    private final Text _step_type_short_description;
    private final Tooltip _step_description_tooltip;
    private Node[] _editor_nodes = {};
    private Node[] _name_nodes = {};
    private Validatable[] _validatables = {};

    public static final String DESCRIPTION_FIELD_ID = "step_description";
    public static final String TYPE_FIELD_ID = "step_type";
    public static final String HELP_TYPE_SHORT_DESCRIPTION = "step_type_help_short";
    }


