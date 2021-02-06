@echo off

set tool_path=../../../Tools/tsExcelTools/dist/
set xmlDirpath=..\..\..\bin\Config
set javaDirPath=..\..\..\world\src\main\java\com\wisp\game\bet\world\config


node %tool_path%/JavaApp.js --basePackagePath=com\wisp\game\bet\world\config --xmlSourceDir=./ --outputJavaPath=./java --outputDataPath=./xml 


::将xml文件复制过去
copy xml\*.xml  %xmlDirpath%	

::将xml文件复制过去
copy java\*.java %javaDirPath%

pause