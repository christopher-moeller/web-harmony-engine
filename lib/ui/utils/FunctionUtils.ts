export default class FunctionUtils {
    public static debounce<T extends Function>(cb: T, wait = 500) {
        let h = 0;
        let callable = (...args: any) => {
            clearTimeout(h);
            // @ts-ignore
            h = setTimeout(() => cb(...args), wait);
        };
        return <T>(<any>callable);
    }
}