@ECHO OFF
mkdir dist
mkdir .\dist\Resources
call .\build\maven\bin\mvn assembly:assembly
copy .\target\ClavarChat-1.0-SNAPSHOT-jar-with-dependencies.jar .\dist
C:\Users\payet\.jdks\openjdk-19.0.1\bin\java.exe -jar .\dist\ClavarChat-1.0-SNAPSHOT-jar-with-dependencies.jar