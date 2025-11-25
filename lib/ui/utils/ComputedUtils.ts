import {ComputedGetter, ComputedRef, ComputedSetter, WritableComputedRef} from "@vue/reactivity";
import {computed, SetupContext} from "vue";
import JsonUtils from "~/utils/JsonUtils";
import {ComponentInternalInstance} from "@vue/runtime-core";

export class ComputedUtils {

    public static readonly createComputedValueForObjectAndFieldPath = (target: any, fieldPath: string, getMapper?: Function, setMapper?: Function):WritableComputedRef<any> => {
        return ComputedUtils.createWritableComputed(() => {
            let v = JsonUtils.getFieldValueByPath(target, fieldPath)
            return getMapper ? getMapper(v) : v
        }, v => {
            let value = v
            if(setMapper) {
                value = setMapper(v)
            }
            JsonUtils.setFieldValueByPath(target, fieldPath, value)
        })
    }

    public static readonly createReadOnlyComputedValueForObjectAndFieldPath = (target: any, fieldPath: string, getMapper?: Function): ComputedRef => {
        return ComputedUtils.createReadableComputed(() => {
            let v = JsonUtils.getFieldValueByPath(target, fieldPath)
            return getMapper ? getMapper(v) : v
        })
    }

    public static readonly createDelegatedComputedValue = (props: any, context: SetupContext<any> | ComponentInternalInstance):WritableComputedRef<any> => {
        return ComputedUtils.createDelegatedComputedValueByParams(props, context, "modelValue", "update:modelValue")
    }

    public static readonly createDelegatedComputedValueByParams = (props: any, context: SetupContext<any> | ComponentInternalInstance, propField: string, emitId: string):WritableComputedRef<any> => {
        return ComputedUtils.createWritableComputed(() => {
            return props[propField]
        }, v => {
            context.emit(emitId, v)
        })
    }

    public static readonly createWritableComputed = <T>(get: ComputedGetter<T>, set: ComputedSetter<T>):WritableComputedRef<T> => {
        return computed({get, set})
    }

    public static readonly createReadableComputed = <T>(get: ComputedGetter<T>):ComputedRef<T> => {
        return computed(get)
    }

}