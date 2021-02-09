
import { playerDb } from "../db/PlayerDB";
import { Schema } from "mongoose";


let randomRoomNumberSchema = new Schema({
    Index:{type:Number},
    Value:{type:Number}
}, { collection: 'RandomRoomNumber',versionKey:false });


let model = playerDb.model('RandomRoomNumber', randomRoomNumberSchema);
export { model as RandomRoomNumberModel };
