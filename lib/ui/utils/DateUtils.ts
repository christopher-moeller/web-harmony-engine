export class DateUtils {

    public static readonly getCurrentDateString = (): string => {
        return DateUtils.parseDateToString(DateUtils.getCurrentDate())
    }

    public static readonly getCurrentDate = (): Date => {
        return new Date()
    }

    public static readonly parseDateStringToDate = (dateString: string): Date => {
        const [year, month, day] = dateString.split('-');
        return new Date(Number(year), Number(month), Number(day))
    }

    public static readonly parseDateTimeToString = (date: Date): string => {
        return DateUtils.parseDateToString(date) + " " + DateUtils.parseTimeToString(date);
    }
    public static readonly parseDateToString = (date: Date): string => {
        const year = date.getFullYear();
        const month = (date.getMonth() + 1).toString().padStart(2, '0'); // Months are zero-based
        const day = date.getDate().toString().padStart(2, '0');
        return year + '-' + month + '-' + day;
    }

    public static readonly parseTimeToString = (date: Date): string => {
        const hours = date.getHours().toString().padStart(2, '0');
        const minutes = date.getMinutes().toString().padStart(2, '0');
        const seconds = date.getSeconds().toString().padStart(2, '0');
        return hours + ':' + minutes + ':' + seconds;
    }

    public static readonly getDatePlusDays = (date: Date, numberOfDays: number): Date => {
        const newDate = new Date(date.getTime());
        newDate.setDate(date.getDate() + numberOfDays);
        return newDate
    }

    public static readonly getDateMinusDays = (date: Date, numberOfDays: number): Date => {
        const newDate = new Date(date.getTime());
        newDate.setDate(date.getDate() - numberOfDays);
        return newDate
    }

    public static readonly formatDateString = (dateString:string, formatTemplate: string) => {
        const [year, month, day] = dateString.split('-');
        return formatTemplate
            .replace('yyyy', year)
            .replace('MM', month)
            .replace('dd', day);
    }


}