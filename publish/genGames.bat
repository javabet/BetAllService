for %%i in (./games/baccarat/*.proto) do (
	protoc.exe  --proto_path=./games/baccarat --proto_path=./share/protocol  --java_out=../games/baccarat/src/main/java  ./games/baccarat/%%i
)

pause