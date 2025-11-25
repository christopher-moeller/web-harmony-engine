package com.webharmony.engine.form;

import com.webharmony.core.utils.reflection.ReflectionUtils;
import com.webharmony.engine.utils.EngineFormField;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EngineForm<T> extends JPanel {

    private final transient List<FieldBinder> fieldBinders;

    @Getter
    private transient T data;

    public EngineForm(Class<T> formDataType) {
        this.fieldBinders = createFieldBinders(formDataType);
        init();
    }

    private void init() {
        setLayout(new FlowLayout(FlowLayout.CENTER));
        final Box box = Box.createVerticalBox();
        for (FieldBinder fieldBinder : this.fieldBinders) {
            box.add(fieldBinder.getComponent());
        }
        add(box, BorderLayout.CENTER);
    }

    @SneakyThrows
    public void bind(T data) {
        this.data = data;
        for (FieldBinder fieldBinder : fieldBinders) {
            Field field = fieldBinder.getField();
            final Method getter = ReflectionUtils.findGetterByField(field, data.getClass())
                    .orElseThrow(() -> new IllegalStateException("Field " + field.getName() + " has not getter"));

            Object value = getter.invoke(data);
            fieldBinder.getSetValueFunction().accept(value);

            final Method setter = ReflectionUtils.findSetterByField(field, data.getClass())
                    .orElseThrow(() -> new IllegalStateException("Field " + field.getName() + " has not setter"));

            final Consumer<Object> onUpdateValue = v -> {
                try {
                    setter.invoke(data, v);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new IllegalStateException(e);
                }
            };

            fieldBinder.setOnValueUpdateFunction(onUpdateValue);
        }
    }

    public void clearErrorMessages() {
        for (FieldBinder fieldBinder : fieldBinders) {
            if(fieldBinder.getSetErrorMessageFunction() != null) {
                fieldBinder.getSetErrorMessageFunction().accept(null);
            }
        }
    }

    public void setFieldErrorMessage(String fieldName, String message) {

        final FieldBinder fieldBinder = fieldBinders.stream().filter(binder -> binder.getField().getName().equals(fieldName))
                .findAny()
                .orElseThrow(() -> new IllegalStateException(String.format("Field with name '%s' not found", fieldName)));

        if(fieldBinder.getSetErrorMessageFunction() != null) {
            fieldBinder.getSetErrorMessageFunction().accept(message);
        }
    }

    private List<FieldBinder> createFieldBinders(Class<T> formDataType) {
        final List<FieldBinder> resultList = new ArrayList<>();
        for (Field declaredField : formDataType.getDeclaredFields()) {
            final EngineFormField formFieldAnnotation = declaredField.getAnnotation(EngineFormField.class);
            if(formFieldAnnotation != null) {
                resultList.add(createTextFieldBinder(declaredField));
            }
        }
        return resultList;
    }

    private FieldBinder createTextFieldBinder(Field declaredField) {
        final EngineFormField formFieldAnnotation = declaredField.getAnnotation(EngineFormField.class);

        final Box box = Box.createVerticalBox();

        final JLabel label = new JLabel(formFieldAnnotation.value());
        box.add(label);

        JTextField textField = new JTextField();
        textField.setBorder(null);

        box.add(textField);

        JLabel errorMessageLabel = new JLabel("");
        errorMessageLabel.setForeground(Color.RED);
        errorMessageLabel.setFont(new Font("Serif", Font.PLAIN, 10));
        box.add(errorMessageLabel);

        FieldBinder fieldBinder = new FieldBinder();
        fieldBinder.setField(declaredField);
        fieldBinder.setComponent(box);

        final Consumer<Object> setValueFunction = value -> {
            final String internalValue = value != null ? value.toString() : "";
            textField.setText(internalValue);
        };
        fieldBinder.setSetValueFunction(setValueFunction);

        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onTextFieldUpdate(textField, fieldBinder);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onTextFieldUpdate(textField, fieldBinder);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onTextFieldUpdate(textField, fieldBinder);
            }
        });

        final Consumer<String> setErrorMessageFunction = message -> {
          if(message == null) {
              errorMessageLabel.setText("");
              textField.setBorder(null);
          } else {
              errorMessageLabel.setText(message);
              textField.setBorder(new LineBorder(Color.red,1));
          }
        };

        fieldBinder.setSetErrorMessageFunction(setErrorMessageFunction);

        return fieldBinder;
    }

    private void onTextFieldUpdate(JTextField textField, FieldBinder fieldBinder) {
        if(fieldBinder.onValueUpdateFunction != null) {
            fieldBinder.onValueUpdateFunction.accept(textField.getText());
        }
    }

    @Getter
    @Setter
    private static class FieldBinder {
        private Field field;
        private Component component;
        private Consumer<Object> setValueFunction;
        private Consumer<Object> onValueUpdateFunction;
        private Consumer<String> setErrorMessageFunction;
    }
}
