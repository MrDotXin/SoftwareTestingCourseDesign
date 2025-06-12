SET "BASE_PATH=D:\workFromSit\SoftwareTestingCourseDesign\PropSmartV3\SoftwareTestingCourseDesign"
SET "OPENAPI_CMD_PATH=E:\nodejs\node_modules\@openapitools\openapi-generator-cli\versions\openapi-generator-cli-7.13.0.jar"

java -jar "%OPENAPI_CMD_PATH%" generate ^
    -i http://47.98.177.153:8601/api/v3/api-docs ^
    -g java ^
    -o "%BASE_PATH%\generated-code" ^
    --skip-validate-spec

cd "%BASE_PATH%\generated-code" || exit

call mvn clean package -DSkipTests=true

cd target

IF EXIST "openapi-java-client-*.jar" (
    move "openapi-java-client-*.jar" "%BASE_PATH%\frontend_thirdpart\"
)


