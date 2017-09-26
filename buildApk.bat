@echo off
echo Start Build Apk

call gradlew clean
call gradlew assembleProductionRelease -Dchannel=kinglloy
call gradlew assembleProductionRelease -Dchannel=yingyongbao
call gradlew assembleProductionRelease -Dchannel=360
call gradlew assembleProductionRelease -Dchannel=vivo
call gradlew assembleProductionRelease -Dchannel=flyme
call gradlew assembleProductionRelease -Dchannel=wandoujia
call gradlew assembleProductionRelease -Dchannel=baidu
call gradlew assembleProductionRelease -Dchannel=google
call gradlew assembleProductionRelease -Dchannel=huawei

echo Build Apk Complete