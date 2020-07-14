for %%i in (./share/protocol/*.proto) do (
	protoc.exe --proto_path=./share/protocol --java_out=../protocolLibs/src/main/java  ./share/protocol/%%i
)

for %%i in (./server/protocol/*.proto) do (
	protoc.exe --proto_path=./share/protocol --proto_path=./server/protocol --java_out=../protocolLibs/src/main/java  ./server/protocol/%%i
)

pause