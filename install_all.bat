
cd share
start mvn clean install
cd ..

cd configLib
start "configLib" mvn clean install
cd ..

cd dbLib
start "dbLib" mvn clean install
cd ..

cd monitor
start "monitor" mvn clean package
cd ..


cd gate
start "gate" mvn clean package
cd ..

pause
