package com.webharmony.core.utils.dev.helperapp.panels;

import com.webharmony.core.api.rest.controller.utils.request.RequestContext;
import com.webharmony.core.api.rest.model.view.ValidationRuleConfigurationModel;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.service.appresources.AppResourceService;
import com.webharmony.core.service.appresources.utils.CrudResourceInfoDto;
import com.webharmony.core.service.appresources.utils.CrudResourceInfoSimpleDto;
import com.webharmony.core.service.appviewmodels.ViewModelInfoDto;
import com.webharmony.core.service.appviewmodels.ViewModelService;
import com.webharmony.core.service.data.validation.Validator;
import com.webharmony.core.service.data.validation.ValidatorService;
import com.webharmony.core.service.data.validation.utils.NamedValidationInterface;
import com.webharmony.core.service.data.validation.utils.ValidationRule;
import com.webharmony.core.utils.CollectionUtils;
import com.webharmony.core.utils.JacksonUtils;
import com.webharmony.core.utils.dev.helperapp.utils.DevHelperButton;
import lombok.SneakyThrows;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class ResourcesAndViewModelsContentPanel extends AbstractContentPanel{
    public ResourcesAndViewModelsContentPanel(ContentPanelWrapper contentPanelWrapper) {
        super(contentPanelWrapper);
    }

    @Override
    protected void initContent() {

        Box tableBox = Box.createHorizontalBox();
        tableBox.add(createResourcesTable());
        tableBox.add(createViewModelsTable());


        Box box = Box.createVerticalBox();
        box.add(tableBox);
        box.add(createValidationButtonPanel());

        add(box);
    }

    private Component createValidationButtonPanel() {

        ValidatorService validatorService = ContextHolder.getContext().getBean(ValidatorService.class);

        Box box = Box.createHorizontalBox();

        DevHelperButton reloadButton = new DevHelperButton("Reload all validation rules");
        reloadButton.addActionListener(a -> validatorService.reloadAllValidators());
        box.add(reloadButton);

        DevHelperButton resetButton = new DevHelperButton("Reset all validation rules");
        resetButton.addActionListener(a -> validatorService.resetCustomValidationConfig());
        box.add(resetButton);

        return box;
    }

    private Component createResourcesTable() {
        String[][] data = getAppResourceService().getAllCrudResources().stream().map(CrudResourceInfoSimpleDto::getName).sorted().map(r -> new String[]{r}).toArray(String[][]::new);
        String[] columnNames = { "resourceName" };
        return setSizeOfTable(createScrollableTable(columnNames, data, this::openResourceById));
    }

    private Component createViewModelsTable() {
        String[][] data = getViewModelService().getAllViewModelInfos().stream().map(ViewModelInfoDto::getViewModelName).sorted().map(r -> new String[]{r}).toArray(String[][]::new);
        String[] columnNames = { "viewModelName" };
        return setSizeOfTable(createScrollableTable(columnNames, data, this::openViewModelById));
    }

    private static JScrollPane setSizeOfTable(JScrollPane scrollableTable) {
        scrollableTable.setPreferredSize(new Dimension(300, 300));
        return scrollableTable;
    }

    private ViewModelService getViewModelService() {
        return ContextHolder.getContext().getBean(ViewModelService.class);
    }

    private AppResourceService getAppResourceService() {
        return ContextHolder.getContext().getBean(AppResourceService.class);
    }

    private void openResourceById(String resourceId) {
        CrudResourceInfoDto resourceByName = getAppResourceService().getResourceByName(resourceId, RequestContext.empty(ContextHolder.getSpringContext()));
        getContentPanelWrapper().pushSubPanel(new ResourceDetailSubPanel(getContentPanelWrapper(), resourceByName));
    }

    private void openViewModelById(String viewModelId) {
        ViewModelInfoDto viewModelByName = getViewModelService().getViewModelByName(viewModelId);
        getContentPanelWrapper().pushSubPanel(new ViewModelDetailSubPanel(getContentPanelWrapper(), viewModelByName));
    }

    @SneakyThrows
    private static Class<?> loadClass(String className) {
        return Class.forName(className);
    }

    private static class ResourceDetailSubPanel extends AbstractContentSubPanel {

        private final transient CrudResourceInfoDto crudResourceInfoDto;

        protected ResourceDetailSubPanel(ContentPanelWrapper contentPanelWrapper, CrudResourceInfoDto crudResourceInfoDto) {
            super(contentPanelWrapper);
            this.crudResourceInfoDto = crudResourceInfoDto;
            this.refresh();
        }

        @Override
        protected void initContent() {
            if(this.crudResourceInfoDto == null)
                return;

            Box box = Box.createVerticalBox();
            box.add(createShowValidationRulesButton());
            box.add(createSchemaTextArea());

            add(box);
        }

        private JButton createShowValidationRulesButton() {
            final DevHelperButton button = new DevHelperButton("Show Validation Rules");
            button.addActionListener(e -> {
                Class<?> clazz = loadClass(crudResourceInfoDto.getComplexTypeSchema().getJavaType());
                getContentPanelWrapper().pushSubPanel(new ValidationRuleSubPanel(getContentPanelWrapper(), clazz));
            });
            return button;
        }

        @SneakyThrows
        private JPanel createSchemaTextArea() {
            String schema = JacksonUtils.createDefaultJsonMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(this.crudResourceInfoDto);
            return createRichReadOnlyTextAreaPanel("Schema", schema);
        }
    }

    private static class ViewModelDetailSubPanel extends AbstractContentSubPanel {

        private final transient ViewModelInfoDto viewModelInfoDto;

        protected ViewModelDetailSubPanel(ContentPanelWrapper contentPanelWrapper, ViewModelInfoDto viewModelInfoDto) {
            super(contentPanelWrapper);
            this.viewModelInfoDto = viewModelInfoDto;
            this.refresh();
        }

        @Override
        protected void initContent() {
            if(this.viewModelInfoDto == null)
                return;

            Box box = Box.createVerticalBox();
            box.add(createShowValidationRulesButton());
            box.add(createSchemaTextArea());

            add(box);
        }

        private JButton createShowValidationRulesButton() {
            final DevHelperButton button = new DevHelperButton("Show Validation Rules");
            button.addActionListener(e -> {
                Class<?> clazz = loadClass(viewModelInfoDto.getSchema().getJavaType());
                getContentPanelWrapper().pushSubPanel(new ValidationRuleSubPanel(getContentPanelWrapper(), clazz));
            });
            return button;
        }

        @SneakyThrows
        private JPanel createSchemaTextArea() {
            String schema = JacksonUtils.createDefaultJsonMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(this.viewModelInfoDto);
            return createRichReadOnlyTextAreaPanel("Schema", schema);
        }
    }

    private static class ValidationRuleSubPanel extends AbstractContentSubPanel {

        private final Class<?> javaClass;

        protected ValidationRuleSubPanel(ContentPanelWrapper contentPanelWrapper, Class<?> javaClass) {
            super(contentPanelWrapper);
            this.javaClass = javaClass;
            this.refresh();
        }

        @Override
        protected void initContent() {
            if(javaClass == null)
                return;

            Box box = Box.createVerticalBox();
            box.add(new Label(javaClass.getName()));
            box.add(getValidationRulesTable());

            add(box);
        }

        private Component getValidationRulesTable() {
            Validator<?> validator = ContextHolder.getContext().getBean(ValidatorService.class).getValidatorForClass(this.javaClass);
            List<? extends ValidationRule<?>> validationRules = CollectionUtils.emptyListIfNull(validator.getValidationRules());
            String[][] data = validationRules.stream().map(r -> new String[]{r.getPath(), r.getValidation().size()+""}).toArray(String[][]::new);
            String[] columnNames = { "path", "validations" };
            return setSizeOfTable(createScrollableTable(columnNames, data, this::openByPath));
        }


        private void openByPath(String path) {
            Validator<?> validator = ContextHolder.getContext().getBean(ValidatorService.class).getValidatorForClass(this.javaClass);
            List<? extends ValidationRule<?>> validationRules = validator.getValidationRules();
            ValidationRule<?> validationRule = validationRules.stream().filter(r -> Objects.toString(r.getPath()).equals(path)).findAny().orElseThrow();
            getContentPanelWrapper().pushSubPanel(new ValidationRulePathSubPanel(getContentPanelWrapper(), validationRule));
        }

    }

    private static class ValidationRulePathSubPanel extends AbstractContentSubPanel {

        private final transient ValidationRule<?> validationRule;

        protected ValidationRulePathSubPanel(ContentPanelWrapper contentPanelWrapper, ValidationRule<?> validationRule) {
            super(contentPanelWrapper);
            this.validationRule = validationRule;
            this.refresh();
        }

        @Override
        protected void initContent() {
            if(validationRule == null)
                return;

            Box box = Box.createVerticalBox();
            box.add(new JLabel(validationRule.getPath()));
            box.add(createValidationsTable());

            add(box);

        }

        private  Component createValidationsTable() {
            List<? extends NamedValidationInterface<?, ?>> validations = validationRule.getValidation();
            String[][] data = validations.stream().map(r -> new String[]{r.getName()}).toArray(String[][]::new);
            String[] columnNames = { "name" };
            return setSizeOfTable(createScrollableTable(columnNames, data, this::openByName));
        }

        private void openByName(String validationName) {
            final Class<?> rootType = this.validationRule.getRootType();
            final String path = this.validationRule.getPath();
            ValidationRuleConfigurationModel validationRuleConfigurationModel = ContextHolder.getContext().getBean(ValidatorService.class).loadConfigurationModel(rootType.getName(), path, validationName);
            getContentPanelWrapper().pushSubPanel(new ValidationRuleConfigurationSubPanel(getContentPanelWrapper(), validationRuleConfigurationModel));
        }
    }

    private static class ValidationRuleConfigurationSubPanel extends AbstractContentSubPanel {

        private transient ValidationRuleConfigurationModel validationRuleConfigurationModel;

        protected ValidationRuleConfigurationSubPanel(ContentPanelWrapper contentPanelWrapper, ValidationRuleConfigurationModel validationRuleConfigurationModel) {
            super(contentPanelWrapper);
            this.validationRuleConfigurationModel = validationRuleConfigurationModel;
            this.refresh();
        }

        @Override
        protected void initContent() {
            if(this.validationRuleConfigurationModel == null)
                return;

            Box box = Box.createVerticalBox();
            box.add(createKeyValueLabel("Root Type", validationRuleConfigurationModel.getRootType()));
            box.add(createKeyValueLabel("Path", validationRuleConfigurationModel.getPath()));
            box.add(createKeyValueLabel("Rule", validationRuleConfigurationModel.getValidationRuleName()));
            box.add(createIsActiveCheckBox());
            box.add(createReturnAlwaysInvalidCheckBox());

            add(box);
        }

        private Component createIsActiveCheckBox() {
            JCheckBox checkBox = new JCheckBox("Is Active");
            checkBox.setSelected(validationRuleConfigurationModel.getIsActive());
            checkBox.addActionListener(a -> {
                validationRuleConfigurationModel.setIsActive(checkBox.isSelected());
                updateModel();
            });
            return checkBox;
        }

        private Component createReturnAlwaysInvalidCheckBox() {
            JCheckBox checkBox = new JCheckBox("Always Return Invalid");
            checkBox.setSelected(validationRuleConfigurationModel.getAlwaysReturnInvalid());
            checkBox.addActionListener(a -> {
                validationRuleConfigurationModel.setAlwaysReturnInvalid(checkBox.isSelected());
                updateModel();
            });
            return checkBox;
        }

        private void updateModel() {
            final ValidatorService validatorService = ContextHolder.getContext().getBean(ValidatorService.class);
            validatorService.saveConfigurationModel(validationRuleConfigurationModel.getRootType(), validationRuleConfigurationModel.getPath(), validationRuleConfigurationModel.getValidationRuleName(), validationRuleConfigurationModel);
            this.validationRuleConfigurationModel = validatorService.loadConfigurationModel(validationRuleConfigurationModel.getRootType(), validationRuleConfigurationModel.getPath(), validationRuleConfigurationModel.getValidationRuleName());
            this.refresh();
        }
    }

}
