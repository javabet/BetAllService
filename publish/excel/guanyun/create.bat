@echo off

set tool_path=../../../Tools/tsExcelTools/dist/


node %tool_path%/JavaApp.js	--xmlSourceDir=./ --outputJavaPath=./java --outputDataPath=./xml --basePackagePath=com.wisp.game.guanyun


pause