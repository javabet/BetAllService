import { Logger } from 'log4js'


declare global {    
    namespace NodeJS {        
        interface Global {
            config: any,
            logger: Logger
        }
    }
}

declare module "mongoose" {
    interface Document {
        ApiName: any;
        Data: Object;
        Time: Date;
        Process: Boolean;
        FailCount: Number;
    }
}
