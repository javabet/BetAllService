@echo off

set tool_path=../../../Tools/tsExcelTools/dist/
set xmlDirpath=..\..\..\bin\Config
set javaDirPath=..\..\..\games\guanyun\src\main\java\com\wisp\game\guanyun


node %tool_path%/JavaApp.js	--xmlSourceDir=./ --outputJavaPath=./java --outputDataPath=./xml --basePackagePath=com.wisp.game.guanyun


::将xml文件复制过去
copy xml\*.xml  %xmlDirpath%	

::将xml文件复制过去
copy java\*.java %javaDirPath%

pause