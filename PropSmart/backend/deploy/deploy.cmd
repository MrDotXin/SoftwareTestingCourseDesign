SET BASE_PATH=D:\workFromSit\SoftwareTestingCourseDesign\PropSmartV3\SoftwareTestingCourseDesign
SET ECS_DEPLOY_PATH=\var\java\project\JCD\bin\deploy

cd %BASE_PATH%\PropSmart\backend

call mvn clean package -DSkipTests=true

cd %BASE_PATH%\PropSmart\backend\target

FOR %%F IN ("%BASE_PATH%\bin\*.jar") DO (
    ECHO %%F
    del /F %%F
)

move "*.jar" %BASE_PATH%\bin

FOR %%F IN ("%BASE_PATH%\bin\*.yml") DO (
    del "%%F"
)

cd %BASE_PATH%\PropSmart\backend\src\main\resources

copy "*-remote.yml" %BASE_PATH%\bin\application.yml

@echo off
echo on connecting STP ...
(
    echo ls
    echo put -r "%BASE_PATH%\bin\" "%ECS_DEPLOY_PATH%\"
    echo exit
) | sftp "ecshost"

