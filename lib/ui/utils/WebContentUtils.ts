import {WebContentComponentType} from "~/utils/HarmonyTypes";
import {BoxAlignment, BoxWebContent, ImageWebContent, TextWebContent, TitleWebContent} from "~/CoreApi";

const i18n = I18N.of("WebContentUtils")

export default class WebContentUtils {
    public static getComponentTypes = (): WebContentComponentType[] => {
        return [
            {
                id: "box",
                getCaption: () => i18n.translate('Box').build(),
                iconId: "bx-package",
                createNewInstance: () => {
                    const newBox:BoxWebContent = {
                        children: [],
                        align: BoxAlignment.VERTICAL,
                        type: "box"
                    }
                    return newBox
                }
            },
            {
                id: "title",
                getCaption: () => i18n.translate('Title').build(),
                iconId: "bx-heading",
                createNewInstance: () => {
                    const newTitle:TitleWebContent = {
                        title: i18n.translate("Title").build(),
                        type: "title"
                    }
                    return newTitle
                }
            },
            {
                id: "text",
                getCaption: () => i18n.translate('Text').build(),
                iconId: "bx-text",
                createNewInstance: () => {
                    const newText:TextWebContent = {
                        text: '<p>' + i18n.translate('Text').build() + '</p>',
                        type: "text",
                    }
                    return newText
                }
            },
            {
                id: "image",
                getCaption: () => i18n.translate('Image').build(),
                iconId: "bx-image",
                createNewInstance: () => {
                    const newText:ImageWebContent = {
                        imageTitle: "Image Title",
                        type: "image",
                    }
                    return newText
                }
            }
        ]
    }

    public static getComponentTypeById(id: string): WebContentComponentType {
        return WebContentUtils.getComponentTypes().find(t => t.id === id)!
    }

}