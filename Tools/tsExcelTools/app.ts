import { ExcelHandler } from "./libs/ExcelHandler";
import * as fs from 'fs';
import * as path from 'path';

//循环取excel表数据
function getExcelPath(filePath:string,fileList:string[],ignoreList:string[])
{
    let state = fs.statSync(filePath);
    if( state.isDirectory() )
    {
        let pathList:string[] =  fs.readdirSync(filePath,"utf8");
        for(let i = 0; i < pathList.length;i++)
        {
            let childPath = pathList[i];
            getExcelPath(filePath + "/" +childPath,fileList,ignoreList);
        }
    }
    else
    {
        let reg = /\.(xls)|(xlsx)$/gi;
        if( filePath.match(reg) != null )
        {
            let fileName = filePath.replace(/\.((xlsx)|(xls))$/gi,"");
            let match = fileName.match(/[^\\\/]+$/gi);
            if( match != null && match.length == 1 )
            {
                fileName = match[0];
            }
            if( ignoreList.indexOf( fileName ) >= 0 )
            {
                return;
            }
            fileList.push(filePath );
        }
    }
}



function makeDir( dirpath:string ) 
{
	let dirs = dirpath.split( path.sep );
	let tmpPath = dirpath;
	let makes = [];
    while( !fs.existsSync( tmpPath ) )
    {
		makes.push( path.basename( tmpPath ) );
		tmpPath = path.dirname( tmpPath );
	}

    for( let i=makes.length-1; i>=0; --i )
    {
		tmpPath = path.join( tmpPath, makes[i] );
		fs.mkdirSync(tmpPath);
	}
}

function getTemplateMsg(name:string):string | undefined
{
    //获得模块文件
    let templatePath = `./config/${name}.txt`;
    let templateFileExist = fs.existsSync( templatePath );
    if(!templateFileExist)
    {
        console.log("模板文件不存在");
        return undefined;
    }

    let templateStr = fs.readFileSync(templatePath,{encoding:"utf8"});

    return templateStr;
}

/**
 * 
 * @param sourcePath excels配置表路径
 * @param outputConfigPath 输出配置表路径
 * @param outputScriptPath 输出代码路径
 */
function main(sourcePath:string,outputConfigPath:string,outputScriptPath:string)
{
    let fileExist = fs.existsSync(sourcePath);
    if(!fileExist)
    {
        console.log("工作目录不存在");
        return;
    }
   
    //获得模块文件
    // let templatePath = "./config/template.txt";
    // let templateFileExist = fs.existsSync( templatePath );
    // if(!templateFileExist)
    // {
    //     console.log("模板文件不存在");
    //     return;
    // }

    let templateStr =  getTemplateMsg( "template" ); // fs.readFileSync(templatePath,{encoding:"utf8"});
    let mapTemplateStr =  getTemplateMsg( "mapTemplate" ); // fs.readFileSync(templatePath,{encoding:"utf8"});

    if( templateStr === undefined || mapTemplateStr === undefined  )
    {
        console.log("模板文件不存在");
        return;
    }

    let list:string[] = [];
    let ignoreList:string[] = [];
    let ignoreFilePath = "./config/ignore.json";
    let hasIgnoreFile:boolean = true;
    if(!fs.existsSync( ignoreFilePath ))
    {
        hasIgnoreFile = false;
    }

    if( hasIgnoreFile )
    {
        let ignoreFileData = fs.readFileSync(ignoreFilePath,{encoding:"utf8"});
        let jsonData
        try
        {
            jsonData = JSON.parse(ignoreFileData);
        }
        catch(err)
        {
            console.log("解析忽略列表失败");
        }
        
        ignoreList = jsonData;
    }

    getExcelPath(sourcePath,list,ignoreList);

    for(let i = 0; i < list.length;i++)
    {
        let xlsFile = list[i];

        if( xlsFile.indexOf("BenzBmwProbCFGConfig") != -1)
        {
            console.log("go this..");
        }


        let excelHandler:ExcelHandler = new ExcelHandler( xlsFile );

        //去掉前缀路径
        let midPath = xlsFile.replace(sourcePath,"");
        //去掉文件名路径
        midPath = midPath.replace(/\/[^\/]*\.((xls)|(xlsx))$/gi,"");
        midPath = midPath.replace("^\/","");
        
        excelHandler.doWork();
        
        let realyOutPutConfigPath = path.join(outputConfigPath,midPath);
        let realyOutPutScriptPath = path.join( outputScriptPath,midPath );

        makeDir(realyOutPutConfigPath);
        makeDir(realyOutPutScriptPath);

        if( excelHandler.isMapType )
        {
            excelHandler.saveFiles(realyOutPutConfigPath,realyOutPutScriptPath,mapTemplateStr,midPath);
        }
        else
        {
            excelHandler.saveFiles(realyOutPutConfigPath,realyOutPutScriptPath,templateStr,midPath);
        }


        console.log( xlsFile +   " handing.......");
    }  

    console.log("complete");
}

let args = process.argv;
console.log( args );
let sourcePath=  args[2] ||"C:\\D\\E\\XHGame\\Program\\bin";
let outputConfigPath= args[3] || "C:\\D\\E\\jstsWorkspace\\xhJSWorkspace\\servers\\dist\\excelConfig";
let outputScriptPath= args[4] || "C:/D/E/jstsWorkspace/xhJSWorkspace/servers/app/configScript";

console.log("outputScriptPath:" + outputScriptPath);

main(sourcePath,outputConfigPath,outputScriptPath);



