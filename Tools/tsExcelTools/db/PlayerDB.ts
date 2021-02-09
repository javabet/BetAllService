import * as mongoose from 'mongoose';

let opt:any = {};
if(global.config.USE_REPLICASET){
    opt = { useNewUrlParser: true, useCreateIndex: true, authSource:"admin", replicaSet:"repset", readPreference: "primaryPreferred"};
}
else{
    opt = { useNewUrlParser: true, useCreateIndex: true, authSource:"admin"};
}
if( global.config.readPreference != undefined )
{
    opt.readPreference = global.config.readPreference;
}

if( global.config.readPreference != undefined )
{
    opt.readPreference = global.config.readPreference;
}

let db = mongoose.createConnection(global.config.DB_PLAYER_URL, opt);

/**
  * 连接成功
  */
 db.on('connected', function () {    
    console.log('Mongoose connection open to ' + global.config.DB_PLAYER_URL);  
});    

/**
 * 连接异常
 */
db.on('error',function (err) {    
    console.log('Mongoose connection error: ' + err);  
});    
 
/**
 * 连接断开
 */
db.on('disconnected', function () {    
    console.log('Mongoose connection disconnected');  
});    

export { db as playerDb };