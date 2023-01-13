@ECHO OFF
mkdir dist
mkdir .\dist\Resources
mkdir .\dist\Resources\BDD
copy .\src\main\resources\Resources\BDD\ClavarDataBase.db .\dist\Resources\BDD
call .\build\maven\bin\mvn assembly:assembly
copy .\target\ClavarChat-1.0-SNAPSHOT-jar-with-dependencies.jar .\dist
C:\Users\payet\.jdks\openjdk-19.0.1\bin\java.exe -jar .\dist\ClavarChat-1.0-SNAPSHOT-jar-with-dependencies.jar