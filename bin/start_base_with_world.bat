start "monitor" java -jar monitor.jar &

::start "gate" java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar gate.jar &
start "gate" java -jar gate.jar


start "world" java -jar world.jar &

pause