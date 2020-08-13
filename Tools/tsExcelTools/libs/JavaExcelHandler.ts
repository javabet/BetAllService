import * as xlsx from 'xlsx';
import * as fs from "fs";
import { dirname } from 'path';

export type ConfigType = 
{
    //{colName:colName,valCnt:0,desc:"",tp:undefined,name:fieldName,isArr:false,propertyType:undefined};
    colName:string;         //列名
    valCnt:number;          //不知道
    desc:string;            //说明
    name:string;            //属性名称
    isArr:boolean;          //是否是数组
    sourceType:string;      //在配置表中的类型
    paramType:string;       //
    defineType:string;      //大类型
    propertyType:string;   //原始类型
}


//读取某个目录下所有的excel文件
export class JavaExcelHandler
{

    private packageNameReg:RegExp = new RegExp("\\{PackageName\\}","g");
    private classNameReg:RegExp = new RegExp("\\{ClassName\\}","g");
    private KeyPropertyTypeReg:RegExp = new RegExp("\\{KeyPropertyType\\}","g");            //关键帧的类型，有String与Int,Long 三种类型
    private KeyPropertyTypeShortReg:RegExp = new RegExp("\\{KeyPropertyTypeShort\\}","g");
    private SourcePathReg:RegExp = new RegExp("\\{SourcePath\\}","g");
    private PropertyKeyNameReg:RegExp = new RegExp("\\{PropertyKeyName\\}","g");
    private ParsePropertyListReg:RegExp = new RegExp("\\{ParsePropertyList\\}","g");
    private PropertyListReg:RegExp = new RegExp("\\{PropertyList\\}","g");

    private excelPath:string;
    private excelFileName = "";
    private primaryKey = "";
    private primaryKeyPropertyType = "";

    private firstColType:string = "string";       //主键索引的数据类型有，string,number两个
    private colList:{[key:string]:ConfigType} = {};            //收集前三行的数据，用于表示字段，名字，类型
    private maxRow = 0;                     //最多的字段函数                
    private tmpRowArrData:Object[] = [];             //临时的中间数据
    private outPutDataList:any[] = [];               //需要输出的最终数据
    private outPutTPScript:string = "";             //需要输出的每个表的字段解析
    private outPutTPScript1:string = "";             //需要输出的每个表的字段解析

    isMapType:boolean = false;                  //是否map类型的数据

    constructor(excelPath:string)
    {
        let self = this;
        self.excelPath = excelPath;
    }

    //此处是使用原来他们服务器使用的配置表来进行处理，只处理第一个sheets
    public doWork()
    {
        let self = this;
        self.colList = {};
        self.maxRow = 0;

        let workbook = xlsx.readFile(this.excelPath);
        let sheetNames = workbook.SheetNames;

        let reg = /[^\\\/\.]+\.((xlsx)|(xls))$/gi;
        let fileMatchList = this.excelPath.match(reg);
        if( fileMatchList == null )
        {
            return;
        }

        if( fileMatchList.length == 0 )
        {
            return;
        }

        let fileName = fileMatchList[0];
        this.excelFileName = fileName.replace(/\.((xlsx)|(xls))/gi,"");


        //console.log( this.excelPath );

        let firstSheetName = sheetNames[0];
        let firstSheet = workbook.Sheets[ firstSheetName ];

        
        this.handlerSheet(firstSheet);          //解析出头
        this.parseSheet(firstSheet);            //解析出具体的数据
        //this.setComboxRowData();                //将解析出来的头与数据进行拼装组合
        //this.setComboxRowData2();                   //使用配置表提供的类型来分配数据
        //this.outPutJsonData();                  //将得到的数据整合进一个数组中，方便导出
        //this.outPutTSConstCode();               //利用生成的字段生成全局的字段变量
    }


    private handlerSheet(sheet:xlsx.WorkSheet)
    {
        let self = this;

        for(let key in sheet)
        {
            if( key.indexOf("!") == 0 )
            {
                continue;
            }
            let keyName:string = key;
            let keyRowMatch = keyName.match(/\d+$/);
            if( keyRowMatch == null || keyRowMatch.length == 0)
            {
                continue;
            }
            let colNameMath = keyName.match(/^[A-Za-z]+/);
            if( colNameMath == null || colNameMath.length == 0 )
            {
                continue;
            }

            let row = parseInt(keyRowMatch[0]);
            let colName = colNameMath[0];

            let val =  sheet[ key ].v;
            if( val == null || val == undefined )
            {
                continue;
            }

            if( row > self.maxRow )
            {
                self.maxRow = row;
            }

            if( row == 1 )          //字段文字
            {
                let fieldName:string = val;
                //如果第一行不为a-zA-Z0-9，则认为不是字段类
                let reg = /^[a-zA-Z_\$][a-zA-Z0-9_\$]*$/gi;
                if( fieldName.match( reg )  == null)
                {
                    continue;
                }

                let coldData:ConfigType = {colName:colName,valCnt:0,desc:"",name:fieldName,isArr:false,defineType:"",sourceType:"",paramType:"",propertyType:""};
                self.colList[ colName ] = coldData;
            }
            else
            {
                let colRowData:ConfigType = Reflect.get(self.colList,colName); 
                if( colRowData == null || colRowData == undefined )
                {
                    continue;
                }

                if(row == 2)           //字段描述
                {
                    colRowData.desc = val;
                }
                else if(row == 3)           //类型
                {
                    let type = val;
                    colRowData.sourceType = type;
                    self.parsetDataType( colRowData );
                }
            }
        }

        for(let colKey in self.colList)
        {
            if( Reflect.get(self.colList,colKey).name  == undefined )
            {
               //delete self.colList[colKey]
               Reflect.deleteProperty(self.colList,colKey);
            }
        }


        if( !self.colList.hasOwnProperty("A") )
        {
            console.log("无主键:" + self.excelFileName);
        }
        else
        {
            self.primaryKey = self.colList["A"].name;  //Reflect.get(self.colList,"A").name;
            this.firstColType =  self.colList["A"].defineType;// Reflect.get(self.colList,"A").tp;
            this.primaryKeyPropertyType =  self.colList["A"].propertyType; //Reflect.get(self.colList,"A").propertyType;
        }

        //查看是否是map类型数据，判断依据，1：第二列 2：没有类型 3：名字为KEY_VALUE
        if( Reflect.get(self.colList,"B"))
        {
            let colData = Reflect.get(self.colList,"B");
            if(colData != undefined && colData.tp === undefined && colData.name === "KEY_VALUE")
            {
                this.isMapType = true;
            }
        }

    }
    

    private parseSheet(sheet:xlsx.WorkSheet)
    {
        let colLen = 0;

        let self = this;
        for(let i = 4; i <= self.maxRow;i++)
        {
            let rowData = {};
            for(let colKey in self.colList)
            {
                let colData:ConfigType = Reflect.get(self.colList,colKey);
            
                let cell = sheet[ colData.colName + i ]
                if(cell == undefined)
                {
                    continue;
                }
                Reflect.set(rowData,colKey,cell.v)
            }

            self.tmpRowArrData.push( rowData );;
        }
    }


    /**
     * 将生成的数据保存起来,需要保存两个文件，分别是配置表json,解析ts
     * @param baseJsonPath 保存Json 的数据的基础路径
     * @param baseTsPath  保存两个ts文件的基础路径
     */
    public saveFiles(outPutConfigPath:string,baseTsPath:string,templateTxt:string,dirName:string,packageStr:string = "com.wisp.game.bet",sourcePath:string = "../Config/abc/"):void
    {
        let self = this;
        let outputStr = self.formatTempStr(templateTxt,dirName,packageStr,sourcePath);
        fs.writeFileSync(baseTsPath + "/" + self.excelFileName + ".java",outputStr );
    }

    private formatTempStr(templateTxt:string,dirName:string,packageStr:string = "com.wisp.game.bet",sourcePath:string = "../Config/abc/"):string
    {
        let self = this;
        templateTxt = templateTxt.replace(self.packageNameReg,packageStr + "." + dirName);
        templateTxt = templateTxt.replace(self.classNameReg,self.excelFileName);
        templateTxt = templateTxt.replace(self.KeyPropertyTypeReg,self.primaryKeyPropertyType);
        templateTxt = templateTxt.replace(self.KeyPropertyTypeShortReg,self.firstColType);
        templateTxt = templateTxt.replace(self.SourcePathReg,sourcePath);
        templateTxt = templateTxt.replace(self.PropertyKeyNameReg,"m" + self.primaryKey);
        templateTxt = templateTxt.replace(self.ParsePropertyListReg,self.getParsePropertyList());
        templateTxt = templateTxt.replace(self.PropertyListReg,self.getClassDataPropertySetterGetter());

        return templateTxt;
    }

    private getClassDataPropertySetterGetter():string
    {
        let beforeStr = "";
        let afterStr = "";

        let self = this;
        for (let configKey in self.colList)
        {
            let itemData:ConfigType = Reflect.get(self.colList,configKey);
            
            if(  itemData.defineType == "" && itemData.name == "KEY_VALUE" )
            {
                continue;
            }

            if(  itemData.defineType == "" )
            {
                console.log( " excelFileName: "+ self.excelFileName + " the column has not the defineKey: " + itemData.colName);
                continue;
            }
            
            beforeStr += self.getConfigDataItemPre(itemData);
            afterStr += self.getConfigDataItem( itemData );
        }

        return beforeStr + "\n\n" + afterStr;
    }
    

    private getParsePropertyList():string
    {
        let parseStr = "\n";


        let self = this;
        for (let configKey in self.colList)
        {
            let itemData:ConfigType = Reflect.get(self.colList,configKey);

            if(  itemData.defineType == "" && itemData.name == "KEY_VALUE" )
            {
                continue;
            }

            if( itemData.defineType == "" )
            {
                console.log( " excelFileName: "+ self.excelFileName + " the column has not the defineKey: " + itemData.colName);
                continue;
            }
            
            parseStr += self.getParseConfigDataItem(itemData);
        }

        return parseStr;
    }

    private getConfigDataItemPre(config:ConfigType):string
    {
        let templateTxt = "";
        
        templateTxt = "\n        private {PropertyType} m{PropertyKey}; //{desc}\n";

        let KeyPropertyTypeShortReg:RegExp = new RegExp("\\{PropertyType\\}","g");
        let KeyPropertyReg:RegExp = new RegExp("\\{PropertyKey\\}","g");
        let descReg:RegExp = new RegExp("\\{desc\\}","g");

        templateTxt = templateTxt.replace(KeyPropertyTypeShortReg,config.defineType);
        templateTxt = templateTxt.replace(KeyPropertyReg,config.name);
        templateTxt = templateTxt.replace(descReg,config.desc);
        
        return templateTxt;
    }

    private getConfigDataItem(config:ConfigType):string
    {
        let templateTxt = 
        "        public {PropertyType} getm{PropertyKey}() {\n" +
        "            return m{PropertyKey};\n" + 
        "        }\n" + 
        "        \n" +
        "        public void setm{PropertyKey}({PropertyType} m{PropertyKey}) {\n" +
        "            this.m{PropertyKey} = m{PropertyKey};\n" + 
        "        }\n" +
        "        \n";
        
        let KeyPropertyTypeShortReg:RegExp = new RegExp("\\{PropertyType\\}","g");
        let KeyPropertyReg:RegExp = new RegExp("\\{PropertyKey\\}","g");

        templateTxt = templateTxt.replace(KeyPropertyTypeShortReg,config.defineType);
        templateTxt = templateTxt.replace(KeyPropertyReg,config.name);

        return templateTxt;
    }

    private getParseConfigDataItem(config:ConfigType):string
    {
        let strTemplate = "           data.m{PropertyKey} = XMLUtils.getStringByElement(childElement,\"{PropertyKey}\");\n";
        let intTemplate = "           data.m{PropertyKey} = XMLUtils.getIntByElement(childElement,\"{PropertyKey}\");\n";
        let boolTemplate = "           data.m{PropertyKey} = XMLUtils.getBooleanByElement(childElement,\"{PropertyKey}\");\n";
        let floatTemplate = "           data.m{PropertyKey} = XMLUtils.getFloatleByElement(childElement,\"{PropertyKey}\");\n";
        let longTemplate = "           data.m{PropertyKey} = XMLUtils.getLongByElement(childElement,\"{PropertyKey}\");\n";

        let listTemplate=
            "            {\n" +
            "               data.m{PropertyKey} = new ArrayList<>();\n" +
            "               String eleStr =  childElement.attribute(\"{PropertyKey}\").getValue();\n" +
            "               if( eleStr != null && !eleStr.equals(\"\") )\n"+
            "               {\n"+
            "                   String[] {PropertyKey}Str = eleStr.split(\",\");\n" +
            "                   for(int i = 0; i < {PropertyKey}Str.length;i++)\n" +
            "                   {\n" +
            "                       data.m{PropertyKey}.add( {PropertyValue} );\n" +
            "                   }\n" +
            "               }\n"+
            "            }\n";

        let KeyPropertyReg:RegExp = new RegExp("\\{PropertyKey\\}","g");
        let PropertyValueReg:RegExp = new RegExp("\\{PropertyValue\\}","g");

        let templateTxt ="";
        if( config.isArr )
        {
            templateTxt = listTemplate.replace(KeyPropertyReg,config.name);

            if( config.defineType == "List<Integer>" )
            {
                templateTxt = templateTxt.replace(PropertyValueReg, "Integer.valueOf(" + config.name + "Str[i]" +")" );
            }
            else if( config.defineType == "List<String>"  )
            {
                templateTxt = templateTxt.replace(PropertyValueReg,config.name + "Str[i]");
            }
            else if( config.defineType == "List<Float>" )
            {
                templateTxt = templateTxt.replace(PropertyValueReg, "Float.valueOf(" + config.name + "Str[i]" +")" );
            }
            else if( config.defineType == "List<Double>" )
            {
                templateTxt = templateTxt.replace(PropertyValueReg, "Double.valueOf(" + config.name + "Str[i]" +")" );
            }
            else if(config.defineType == "List<Long>")
            {
                templateTxt = templateTxt.replace(PropertyValueReg, "Long.valueOf(" + config.name + "Str[i]" +")" );
            }
            else
            {
                console.log("unknown array:" + config.sourceType + " config.defineType:" + config.defineType);
            }
        }
        else
        {
            if(config.defineType == "String")
            {
                templateTxt = strTemplate.replace(KeyPropertyReg,config.name);
            }
            else if( config.defineType == "int" )
            {
                templateTxt = intTemplate.replace(KeyPropertyReg,config.name);
            }
            else if(config.defineType == "boolean")
            {
                templateTxt = boolTemplate.replace(KeyPropertyReg,config.name);
            }
            else if(config.defineType == "float")
            {
                templateTxt = floatTemplate.replace(KeyPropertyReg,config.name);
            }
            else if(config.defineType == "long")
            {
                templateTxt = longTemplate.replace(KeyPropertyReg,config.name);
            }
            else
            {
                console.log("unknown the type:" + config.sourceType);
            }
        }

        return templateTxt;
    }

    private parsetDataType(config:ConfigType):void
    {
        let self = this;
        if( config.sourceType == undefined || config.sourceType == "" )
        {
            return;
        }

        let type:string = "";
        switch(config.sourceType.toUpperCase())
        {
            case "INT":
                config.paramType = "int";
                config.defineType = "int";
                config.propertyType = "Integer";
                config.isArr = false;
                break;
            case "FLOAT":
                config.paramType = "float";
                config.defineType = "float";
                config.propertyType = "Float";
                config.isArr = false;
                break
            case "INT64":
                config.paramType = "long";
                config.defineType = "long";
                config.propertyType = "Long";
                config.isArr = false;
                break;
            case "STRING":
                config.paramType = "String";
                config.defineType = "String";
                config.propertyType = "String";
                config.isArr = false;
                break;
            case "BOOL":
                config.paramType = "boolean";
                config.defineType = "boolean";
                config.propertyType = "Boolean";
                config.isArr = false;
                break;
            case "VECTOR<STRING>":
                config.defineType = "List<String>";
                config.propertyType = "List<String>";
                config.isArr = true;
                break;
            case "VECTOR<INT>":
                config.paramType = "List<Integer>";
                config.defineType = "List<Integer>";
                config.propertyType = "List<Integer>";
                config.isArr = true;
                break;
            case "LIST<INT>":
                config.paramType = "List<Integer>";
                config.defineType = "List<Integer>";
                config.propertyType = "List<Integer>";
                config.isArr = true;
                break;
            case "VECTOR<FLOAT>":
                config.paramType = "List<float>";
                config.defineType = "List<Float>";
                config.propertyType = "List<Float>";
                config.isArr = true;;
                break;
            case "VECTOR<INT64>":
                config.paramType = "List<long>";
                config.defineType = "List<Long>";
                config.propertyType = "List<Long>";
                config.isArr = true;;
                break;
            default:
                    //type = oldType;
                    console.log( "excelName:" + self.excelFileName +  " unknow type:" + config.sourceType + " excelName:" + self.excelFileName);
                break;
        }
    }
}