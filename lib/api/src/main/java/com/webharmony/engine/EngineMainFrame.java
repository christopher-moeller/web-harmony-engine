package com.webharmony.engine;

import com.webharmony.core.utils.StringUtils;
import com.webharmony.engine.creator.EngineProjectCreator;
import com.webharmony.engine.form.CreateProjectDto;
import com.webharmony.engine.form.EngineForm;
import com.webharmony.engine.utils.EngineRootConfiguration;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class EngineMainFrame extends JFrame {

    private final transient EngineRootConfiguration rootConfiguration;

    private final Box headerComponent;

    final EngineForm<CreateProjectDto> form;
    private final Panel centerComponent;


    public EngineMainFrame(EngineRootConfiguration rootConfiguration) {
        this.rootConfiguration = rootConfiguration;
        this.headerComponent = createHeaderComponent();
        this.form = createForm();
        this.centerComponent = createCenterComponent();
        setTitle("Harmony Web Engine");
        setSize(500, 400);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        init();
    }

    private EngineForm<CreateProjectDto> createForm() {
        final EngineForm<CreateProjectDto> internalForm = new EngineForm<>(CreateProjectDto.class);

        CreateProjectDto dto = new CreateProjectDto();
        dto.setProjectTechnicalName("dramtastic");
        dto.setProjectShortName("Dram");
        dto.setProjectLongName("Dramtastic");
        dto.setDatasourcePort("15432");
        dto.setDatasourceUsername("postgres");
        dto.setDatasourcePassword("password");

        internalForm.bind(dto);
        return internalForm;
    }

    private Panel createCenterComponent() {
        Panel panel = new Panel();
        panel.add(this.form);
        return panel;
    }

    private Box createHeaderComponent() {
        final Label label = new Label("Create new project");
        label.setFont(new Font("Dialog", Font.PLAIN, 20));

        final Box header = Box.createVerticalBox();
        header.add(label);
        header.add(new Label(""));
        return header;
    }

    private void init() {

        setLayout(new BorderLayout());

        add(this.headerComponent, BorderLayout.NORTH);


        add(centerComponent, BorderLayout.CENTER);

        JButton button = new JButton("Create Project");
        button.addActionListener(e -> {
            final Map<String, String> validationResults = validate(form.getData());
            if(validationResults.isEmpty()) {
                form.clearErrorMessages();
                button.setEnabled(false);
                createProject(form.getData());
            } else {
                validationResults.forEach(form::setFieldErrorMessage);
            }
        });
        add(button, BorderLayout.SOUTH);
    }

    @SuppressWarnings("all")
    private void createProject(CreateProjectDto dto) {
        dto.setApiImageName("api-"+StringUtils.firstLetterToLowerCase(dto.getProjectTechnicalName()));
        dto.setUiImageName("ui-"+StringUtils.firstLetterToLowerCase(dto.getProjectTechnicalName()));
        dto.setNginxImageName("nginx-"+StringUtils.firstLetterToLowerCase(dto.getProjectTechnicalName()));
        setStatusMessage("Creating project ...");
        try {
            new EngineProjectCreator(dto, rootConfiguration).createProject();
            setStatusMessage("Project created successfully");
        } catch (Exception e) {
          setStatusMessage("Failed to create project");
          e.printStackTrace();
        }
    }

    private void setStatusMessage(String statusMessage) {
        centerComponent.removeAll();
        centerComponent.add(new Label(statusMessage));
        centerComponent.revalidate();
        centerComponent.repaint();
    }

    @SuppressWarnings("all")
    private Map<String, String> validate(CreateProjectDto dto) {
        final Map<String, String> result = new HashMap<>();
        if(StringUtils.isNullOrEmpty(dto.getProjectTechnicalName())) {
            result.put("projectTechnicalName", "Cannot be empty");
        }
        if(StringUtils.isNullOrEmpty(dto.getProjectShortName())) {
            result.put("projectShortName", "Cannot be empty");
        } else {
            if(dto.getProjectShortName().length() > 5) {
                result.put("projectShortName", "Cannot be longer than 5 characters");
            }
        }
        if(StringUtils.isNullOrEmpty(dto.getProjectLongName())) {
            result.put("projectLongName", "Cannot be empty");
        }
        if (StringUtils.isNullOrEmpty(dto.getDatasourcePort())) {
            result.put("datasourcePort", "Cannot be empty");
        }
        if(StringUtils.isNullOrEmpty(dto.getProjectShortName())) {
            result.put("datasourceUsername", "Cannot be empty");
        }
        if(StringUtils.isNullOrEmpty(dto.getProjectLongName())) {
            result.put("datasourcePassword", "Cannot be empty");
        }
        return result;
    }
}
