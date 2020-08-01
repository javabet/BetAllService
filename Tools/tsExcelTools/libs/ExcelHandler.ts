import * as xlsx from 'xlsx';
import * as fs from "fs";


//读取某个目录下所有的excel文件
export class ExcelHandler
{
    private excelPath:string;
    private excelFileName = "";
    private primaryKey = "";

    private firstColType:string = "string";       //主键索引的数据类型有，string,number两个
    private colList:Object = {};            //收集前三行的数据，用于表示字段，名字，类型
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
        this.setComboxRowData2();                   //使用配置表提供的类型来分配数据
        this.outPutJsonData();                  //将得到的数据整合进一个数组中，方便导出
        this.outPutTSConstCode();               //利用生成的字段生成全局的字段变量
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
                let fieldName:String = val;
                //如果第一行不为a-zA-Z0-9，则认为不是字段类
                let reg = /^[a-zA-Z_\$][a-zA-Z0-9_\$]*$/gi;
                if( fieldName.match( reg )  == null)
                {
                    continue;
                }

                let coldData = {colName:colName,valCnt:0,desc:"",tp:undefined,name:fieldName,isArr:false};
                Reflect.set(self.colList,colName,coldData);
            }
            else
            {
                let colRowData = Reflect.get(self.colList,colName); 
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
                    colRowData.tp = self.getDataType( type );
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
            self.primaryKey = Reflect.get(self.colList,"A").name;//self.colList["A"].name;
            this.firstColType = Reflect.get(self.colList,"A").tp;
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
                let colData = Reflect.get(self.colList,colKey);
            
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
    private setComboxRowData2():void
    {
        let self = this;
        for(let i = 0; i < self.tmpRowArrData.length;i++)
        {
            let tmpRowData = self.tmpRowArrData[i];
            let len = 0;
            for(let rowKey in tmpRowData)
            {
                len ++;
            } 
        }

        for(let field in self.colList)
        {
            let colData =  Reflect.get(self.colList,field);// self.colList[ field ];
            if( colData.tp == "string[]" || colData.tp == "number[]" )
            {
                for(let i = 0; i < self.tmpRowArrData.length;i++)
                {
                    let tmpRowData = self.tmpRowArrData[i];
                    let len = 0;
                    for(let rowKey in tmpRowData)
                    {
                        len ++;
                        if( len >= 1)
                        {
                            break;
                        }
                    }
                    
                    if( len == 0 )
                    {
                        continue;
                    }

                    let oldValues = Reflect.get(tmpRowData,field);// tmpRowData[ field ];
                    if( (oldValues+"").indexOf(",") > 0 )
                    {
                        //tmpRowData[ field ] = oldValues.split(",");
                        Reflect.set(tmpRowData,field,oldValues.split(","))
                    }
                    else
                    {
                        //tmpRowData[ field ] = [ oldValues ];
                        Reflect.set(tmpRowData,field,[oldValues])
                    }
                    
                    //如果是数字数组类型，则需要将其进行转换
                    if( colData.tp == "number[]" )
                    {
                        let list:string[] = Reflect.get(tmpRowData,field);  //tmpRowData[ field ];
                        let newList:number[] = [];
                        for(let i = 0; i < list.length;i++)
                        {
                            newList.push( parseInt( list[i] ) );
                        }

                        Reflect.set(tmpRowData,field,newList); 
                        //tmpRowData[ field ] = newList;
                    }
                }
            }
        }
    }

    //        //分析字段内容后，重新组织了相关字段类型，将字段与内容组合起来生成数据json
    private outPutJsonData()
    {
        let self = this;
        for(let i = 0; i < self.tmpRowArrData.length;i++)
        {
            let tmpRowData = self.tmpRowArrData[i];
            let len = 0;
            for(let rowKey in tmpRowData)
            {
                len ++;
            } 

            //没有数据
            if( len == 0 )
            {
                continue;
            }

            let rowData:any= {};

            for(let rowKey in tmpRowData)
            {
                let firstRowData = Reflect.get(self.colList,rowKey);// self.colList[ rowKey ];
                if( firstRowData == null )
                {
                    continue;
                }

                let rowValue:string = Reflect.get(tmpRowData,rowKey);// tmpRowData[  rowKey ];
                if( firstRowData.isArr )
                {
                    rowData[ firstRowData.colName ] = rowValue.split(",");
                }
                else
                {
                    if( firstRowData.tp === "number" )
                    {
                        rowData[ firstRowData.name ] = parseInt(rowValue);
                    }
                    else
                    {
                        rowData[ firstRowData.name ] = rowValue
                    }

                }
            }

            self.outPutDataList.push( rowData );
        }
    }

    //利用得到的字段自动生成解析数据
    private outPutTSParseCode():void
    {
        
    }

    //利用生成的字段生成全局的字段变量
    private outPutTSConstCode():void
    {
        let self = this;
        for(let key in self.colList)
        {
            let colData = Reflect.get(self.colList,key);//  self.colList[ key ];
            self.outPutTPScript += "   " + colData.name + " : " + colData.tp + ";    //" + colData.desc + "\n";
			//self.outPutTPScript1 += "   m" + colData.name + " : " + colData.tp + ";    //" + colData.desc + "\n";
        }
    }

    /**
     * 将生成的数据保存起来,需要保存两个文件，分别是配置表json,解析ts
     * @param baseJsonPath 保存Json 的数据的基础路径
     * @param baseTsPath  保存两个ts文件的基础路径
     */
    public saveFiles(baseJsonPath:string,baseTsPath:string,templateTxt:string,shortPath:string):void
    {
        let self = this;
        let jsonFilePath = baseJsonPath + "/" + self.excelFileName + ".json";

        fs.writeFileSync(jsonFilePath,JSON.stringify(self.outPutDataList))

        //{0} 配置表的具体名字
        //{1} 配置表主键值字段
        //{2} 单条数据所有的字段
        //{3} 配置表文件路径与读取文件夹之间的短名路径

        let newTSCodeScript = self.format(templateTxt,self.excelFileName,self.primaryKey,self.outPutTPScript ,shortPath,this.firstColType);
        fs.writeFileSync(baseTsPath + "/" + self.excelFileName + ".ts",newTSCodeScript );
    }


    /**
     * 将原先c++使用的配置表里的字段转化为js可以使用的字段
     */
    private getDataType(oldType:String):String
    {
        let self = this;
        if( oldType == undefined || oldType == null )
        {
            return "";
        }

        let funType = typeof oldType.toUpperCase;
        if( funType != "function" )
        {
            console.log("go this...." + self.excelFileName);
            return "";
        }

        let type:String = "";
        switch(oldType.toUpperCase())
        {
            case "INT":
            case "FLOAT":
            case "INT64":
                    type = "number";
                break;
            case "STRING":
                    type = "string";
                break;
            case "BOOL":
                    type = "boolean";
                break;
            case "VECTOR<STRING>":
                    type = "string[]";
                break;
            case "VECTOR<INT>":
            case "LIST<INT>":
            case "VECTOR<FLOAT>":
                    type = "number[]";
                break;
            default:
                    //type = oldType;
                    console.log("unknow type:" + oldType + " excelName:" + self.excelFileName);
                    type="string";
                break;
        }

        return type;
    }

    private format( str:string, ...rest:string[] ):string
    {
        let num:number = arguments.length;
        for (let i = 1; i < num; i++)
        {
            let pattern = "\\{" + (i-1) + "\\}";
            let re = new RegExp(pattern, "g");
            str = str.replace(re, arguments[i]);
        }
        return str;
    }

}