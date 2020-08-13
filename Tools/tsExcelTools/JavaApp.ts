import minimist = require('minimist');
import { JavaExcelHandler } from "./libs/JavaExcelHandler";
import * as fs from 'fs';
import * as path from 'path';
import { Md5 } from './libs/EncryHelper';

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

    let templateStr =  getTemplateMsg( "javaTemplate" );
    let mapTemplateStr =  getTemplateMsg( "javaMapTemplate" );

    if( templateStr === undefined || mapTemplateStr == undefined )
    {
        console.log("模板文件不存在");
        return;
    }

    let checkMd5Str = getTemplateMsg("fileCheck");
    let checkMd5Obj:{[key:string]:string} = {};
    if( checkMd5Str != null )
    {
        checkMd5Obj = JSON.parse(checkMd5Str);
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


    let hasChg:boolean = false;
    let doNum:number = 0;
    for(let i = 0; i < list.length;i++)
    {
        let xlsFile = list[i];

        if( xlsFile.indexOf("BaccaratBaseConfig") != -1)
        {
            console.log("go this..");
        }

        //去掉前缀路径
        let midPath = xlsFile.replace(sourcePath,"");
        let longMidPath = midPath;
        longMidPath = longMidPath.replace(/.((xls)|(xlsx))$/gi,"");
        //去掉文件名路径
        midPath = midPath.replace(/\/[^\/]*\.((xls)|(xlsx))$/gi,"");
        midPath = midPath.replace("\/","");

        let xlsFileContent = fs.readFileSync(xlsFile,{encoding:"utf8"});
        let md5Hash = Md5(xlsFileContent);
        if( checkMd5Obj[ longMidPath ] != null && checkMd5Obj[ longMidPath ] == md5Hash )
        {
            continue
        }

        doNum ++;
        if( doNum >= 300 )
        {
            continue;
        }

        let excelHandler:JavaExcelHandler = new JavaExcelHandler( xlsFile );

   
        
        excelHandler.doWork();
        
        let realyOutPutConfigPath = path.join(outputConfigPath,midPath);
        let realyOutPutScriptPath = path.join( outputScriptPath,midPath );

        makeDir(realyOutPutConfigPath);
        makeDir(realyOutPutScriptPath);

        if( excelHandler.isMapType )
        {
            // public saveFiles(outputpath:string,baseTsPath:string,templateTxt:string,dirName:string,packageStr:string = "com.wisp.game.bet",sourcePath:string = "../Config/abc/"):void
            excelHandler.saveFiles(realyOutPutConfigPath,realyOutPutScriptPath,mapTemplateStr,midPath,basePackagePath,xmlPath);
        }
        else
        {
            excelHandler.saveFiles(realyOutPutConfigPath,realyOutPutScriptPath,templateStr,midPath,basePackagePath,xmlPath);
        }
        //console.log( xlsFile +   " handing.......");

        hasChg = true;
        checkMd5Obj[ longMidPath ] = md5Hash;
    }  

    //如果有改动，则
    if( hasChg )
    {
        let checkMd5JsonStr = JSON.stringify(checkMd5Obj);
        fs.writeFileSync("./config/fileCheck.txt",checkMd5JsonStr );
        console.log("need repeat done");
    }
    else
    {
        console.log("complete done");
    }

    
}


//用于子游戏模板的生成
let options:minimist.Opts = {};
options.string = ["xmlSourceDir","outputJavaPath","outputDataPath","basePackagePath","readXmlPath"];
options.alias = {
    xmlSourceDir:"sp",                    //sourcePath  模板文件夹位置
    outputJavaPath:"tp",                        // 生成后的子游戏目录文件夹位置
    childName:"nm",                       //子游戏的名称
    outputDataPath:"id",                            //子游戏类型编号
    basePackagePath:"cnm",                    //子子游戏的中文游戏名称说明
    readXmlPath:"cnm",                    //子子游戏的中文游戏名称说明
}
options.default = {
    xmlSourceDir:"D:\\E\\betGitWorkspace\\newServer\\public\\bin",
    outputJavaPath:"D:\\E\\tsGitCode\\xhJSWorkspace\\Tools\\excelTools\\dist\\out",
    outputDataPath:"D:\\E\\tsGitCode\\xhJSWorkspace\\Tools\\excelTools\\dist\\out",
    basePackagePath:"com.wisp.game.bet",
    readXmlPath:"./Config"
}

let argv = minimist( process.argv.splice(2),options );
let targetDirPath:string = Reflect.get(argv,"tPath");


let sourcePath =  Reflect.get(argv,"xmlSourceDir");;
let outputConfigPath= Reflect.get(argv,"outputJavaPath");
let outputScriptPath= Reflect.get(argv,"outputDataPath");
let basePackagePath = Reflect.get(argv,"basePackagePath");;
let xmlPath = Reflect.get(argv,"readXmlPath");


let args = process.argv;
console.log( args );
//let sourcePath =  args[2] ||"D:\\E\\betGitWorkspace\\newServer\\public\\bin";
//let outputConfigPath= args[3] || "D:\\E\\tsGitCode\\xhJSWorkspace\\Tools\\excelTools\\dist\\out";
//let outputScriptPath= args[4] || "D:\\E\\tsGitCode\\xhJSWorkspace\\Tools\\excelTools\\dist\\out";
//let basePackagePath = args[5] || "com.wisp.game.bet";
//let xmlPath = args[6] || "./Config"

main(sourcePath,outputConfigPath,outputScriptPath);

