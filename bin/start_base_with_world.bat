start "monitor" java -jar monitor.jar &

start "gate" java -jar gate.jar &

start "world" java -jar world.jar &

pause