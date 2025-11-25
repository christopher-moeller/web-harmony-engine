import {ComponentInternalInstance} from "@vue/runtime-core";
import {WritableComputedRef} from "@vue/reactivity";
import useViewModelContext, {ViewModelContext} from "~/composables/form/useViewModelContext";
import {ResourceContext} from "~/composables/form/useResourceContext";
import {ComplexTypeSchema, SimpleFieldTypeSchema} from "~/CoreApi";
import JsonUtils from "~/utils/JsonUtils";
import {Ref} from "vue";
import {RegisteredViewModelField} from "~/utils/HarmonyTypes";
import {ComputedUtils} from "~/utils/ComputedUtils";

export interface FormFieldContext {
    fieldComputed: WritableComputedRef<any>,
    fullFieldPath: string,
    formComponent:ComponentInternalInstance,
    isResource: boolean,
    fieldSchema: SimpleFieldTypeSchema,
    currentValue: Ref<any>,
    originalValue: any,
    getOrCreateRegisteredField(field: RegisteredViewModelField): Ref<RegisteredViewModelField>
}

function splitPathIntoFragments(fullPath: string):string[] {
    const resultList:string[] = []
    for(const step of fullPath.split(".")) {
        if(step.includes("[") && step.includes("]")) {
            const subFragments = step.split("[")
            resultList.push(subFragments[0])
            resultList.push(subFragments[1].replace("[", "").replace("]", ""))
        } else {
            resultList.push(step)
        }
    }
    return resultList
}

function findFieldSchemaFromDataSchema(dataSchema:ComplexTypeSchema, fullPath: string): SimpleFieldTypeSchema {
    const pathFragments:string[] = splitPathIntoFragments(fullPath)
    let currentFieldSchema = dataSchema
    for(const step of pathFragments) {
        if(!isNaN(Number(step))) // if path fragment is array index
            continue

        const fields = currentFieldSchema.fields
        if(fields) {
            // @ts-ignore
            currentFieldSchema = fields[step]!
        }
    }
    return currentFieldSchema
}

function getNameOfComponent(componentInstance: ComponentInternalInstance): string {
    return componentInstance.type.name ? componentInstance.type.name : componentInstance.type.__name!
}

function getCplFormComponent(componentInstance: ComponentInternalInstance):ComponentInternalInstance[] {
    const resultList:ComponentInternalInstance[] = [];
    let currentComponent:ComponentInternalInstance | null = componentInstance;
    while (currentComponent) {
        resultList.push(currentComponent)
        currentComponent = currentComponent.parent
    }
    return resultList
}

function findFormInstance(componentInstance: ComponentInternalInstance):ComponentInternalInstance {
    const cpl:ComponentInternalInstance[] = getCplFormComponent(componentInstance)
    for (const component of cpl) {
        const componentName = getNameOfComponent(component)
        if(componentName === "HarmonyViewModelForm" || componentName === "HarmonyResourceForm") {
            return component
        }
    }

    throw new Error("Component " + getNameOfComponent(componentInstance) + " is not inside a form")
}

function createFieldPath(formInstance:ComponentInternalInstance, componentInstance: ComponentInternalInstance): string {
    const uidOfFormInstance:number = formInstance.uid
    const cpl:ComponentInternalInstance[] = getCplFormComponent(componentInstance)

    const formFieldSubPath = componentInstance?.props.fieldPath + ""
    const pathFragments:string[] = [formFieldSubPath]
    for (const component of cpl) {
        if(component.uid === uidOfFormInstance) {
            break
        }
        const componentName = getNameOfComponent(component)
        if(componentName === "FormContext") {
            let subPath = component.props.path + ""
            if(component.props.arrayIndex !== undefined)
                subPath = subPath + "[" + component.props.arrayIndex + "]"
            pathFragments.push(subPath)
        }
    }
    return pathFragments.reverse().join(".")
}

function isResourceForm(formInstance: ComponentInternalInstance):boolean {
    return getNameOfComponent(formInstance) === "HarmonyResourceForm"
}

function getOrCreateRegisteredField(existingFields: RegisteredViewModelField[], newField: RegisteredViewModelField): Ref<RegisteredViewModelField> {
    const existingField = existingFields.find(f => f.fieldPath === newField.fieldPath)
    if(!existingField) {
        existingFields.push(newField)
    }
    return ref(existingFields.find(f => f.fieldPath === newField.fieldPath)!)
}

function createFormFieldContextForResource(resourceForm:ComponentInternalInstance, componentInstance: ComponentInternalInstance, getMapper?: Function, setMapper?: Function):FormFieldContext {
    const resourceContext:ResourceContext = <ResourceContext> resourceForm.props.resourceContext
    const localState = resourceContext.getLocalState()
    const fullFieldPath = createFieldPath(resourceForm, componentInstance)
    const fieldComputed = ComputedUtils.createComputedValueForObjectAndFieldPath(localState.data, fullFieldPath+"", getMapper, setMapper)

    const dataSchema:ComplexTypeSchema = localState.schema.schema!
    const fieldSchema:SimpleFieldTypeSchema = findFieldSchemaFromDataSchema(dataSchema, fullFieldPath)
    const originalValue = JsonUtils.getFieldValueByPath(localState.originalData, fullFieldPath)

    return  {
        fieldComputed,
        fullFieldPath,
        formComponent: resourceForm,
        isResource: true,
        fieldSchema,
        originalValue,
        currentValue: ComputedUtils.createComputedValueForObjectAndFieldPath(localState.data, fullFieldPath+""),
        getOrCreateRegisteredField(field: RegisteredViewModelField): Ref<RegisteredViewModelField> {
            return getOrCreateRegisteredField(localState.registeredFields, field)
        }
    }
}

function createFormFieldContextForViewModel(viewModelForm:ComponentInternalInstance, componentInstance: ComponentInternalInstance, getMapper?: Function, setMapper?: Function):FormFieldContext {
    const viewModelContext:ViewModelContext = <ViewModelContext> viewModelForm.props.viewModelContext
    const uniqueId:string = viewModelContext.getViewModelId()
    const localState = useViewModelContext(uniqueId).getLocalState()
    const fullFieldPath = createFieldPath(viewModelForm, componentInstance)
    const fieldComputed = ComputedUtils.createComputedValueForObjectAndFieldPath(localState.data, fullFieldPath+"", getMapper, setMapper)

    const dataSchema:ComplexTypeSchema = localState.schema
    const fieldSchema:SimpleFieldTypeSchema = findFieldSchemaFromDataSchema(dataSchema, fullFieldPath)
    const originalValue = JsonUtils.getFieldValueByPath(localState.originalData, fullFieldPath)
    return  {
        fieldComputed,
        fullFieldPath,
        formComponent: viewModelForm,
        isResource: false,
        fieldSchema,
        originalValue,
        currentValue: ComputedUtils.createComputedValueForObjectAndFieldPath(localState.data, fullFieldPath+""),
        getOrCreateRegisteredField(field: RegisteredViewModelField): Ref<RegisteredViewModelField> {
            return getOrCreateRegisteredField(localState.registeredFields, field)
        }
    }
}

export default function (componentInstance: ComponentInternalInstance | null, getMapper?: Function, setMapper?: Function):FormFieldContext {
    const targetForm:ComponentInternalInstance = findFormInstance(componentInstance!)
    return isResourceForm(targetForm) ? createFormFieldContextForResource(targetForm, componentInstance!, getMapper, setMapper) : createFormFieldContextForViewModel(targetForm, componentInstance!, getMapper, setMapper)
}