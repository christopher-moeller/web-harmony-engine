package com.webharmony.core.api.rest.model.utils.validinput;

import com.webharmony.core.utils.objects.LabelValueObject;
import com.webharmony.core.utils.objects.ObjectsWithLabel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class EnumInputSpecification extends ValidInputSpecification {


    private final List<LabelValueObject> values;

    public EnumInputSpecification(Class<? extends Enum<?>> enumClass) {
        this.values = initValues(enumClass);
    }

    private List<LabelValueObject> initValues(Class<? extends Enum<?>> enumClass) {
        List<LabelValueObject> resultList = new ArrayList<>();
        for(var enumConstant : enumClass.getEnumConstants()) {
            String value = enumConstant.name();
            String label = enumConstant instanceof ObjectsWithLabel<?> objectsWithLabel ? objectsWithLabel.getStringLabel() : value;
            resultList.add(new LabelValueObject(label, value));
        }
        return resultList;
    }
}
