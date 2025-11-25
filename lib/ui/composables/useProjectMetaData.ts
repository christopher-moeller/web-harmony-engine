import {ProjectMetaData} from "~/CoreApi";


export default function ():ProjectMetaData {
    return <ProjectMetaData> process.env.APP_PROJECT_META
}

