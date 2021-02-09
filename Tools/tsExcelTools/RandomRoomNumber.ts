#!/usr/bin/env node

//生成random
const mongoose = require("mongoose");

let configPath = './config/Config.json';
if ( !process.env.NODE_ENV) {
    global.config = require(configPath).local;
}
else
{
    let env = process.env.NODE_ENV;
  global.config = require(configPath)[env];
}


import {RandomRoomNumberModel} from './model/RandomRoomNumberModel'

RandomRoomNumberModel.remove({});

/** 
export async function main()
{
    await RandomRoomNumberModel.remove({});

    let roomNumberLen:number = 1000000;

    let tmpList:number[] = [];
    for(let i = 1; i <= roomNumberLen;i++ )
    {
        tmpList.push(i);
    }

    for(let i = 0; i < tmpList.length;i++)
    {
        let r1:number = Math.floor( Math.random() * roomNumberLen);
        let d1:number = tmpList[i];
        let d2:number = tmpList[ r1 ];
        tmpList[ i ] = d2;
        tmpList[ r1 ] = d1;
    }

    let insertObj:any[] = [];
    for(let i = 0; i < roomNumberLen;i++)
    {
        insertObj.push({Index:i,Value:tmpList[i]});
    }

    await RandomRoomNumberModel.insertMany(insertObj);

    console.log("complete insert the room number");
}


main();

**/