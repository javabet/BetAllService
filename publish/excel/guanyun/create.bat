@echo off

set tool_path=../../../Tools/tsExcelTools/dist/
set xmlDirpath=..\..\..\bin\Config
set javaDirPath=..\..\..\games\guanyuan\src\main\java\com\wisp\game\bet\games\guanyuan\config


node %tool_path%/JavaApp.js --basePackagePath=com\wisp\game\bet\games\guanyun\config --xmlSourceDir=./ --outputJavaPath=./java --outputDataPath=./xml 


::将xml文件复制过去
copy xml\*.xml  %xmlDirpath%	

::将xml文件复制过去
copy java\*.java %javaDirPath%

pause