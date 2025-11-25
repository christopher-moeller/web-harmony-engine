import _ from "lodash";

export default class JsonUtils {
    public static getFieldValueByPath = (data: any, path: string): any => {
        if(path === "")
            return data
        return _.get(data, path)
    }

    public static setFieldValueByPath = (data: any, path: string, value: any) => {
        _.set(data, path, value)
    }
}