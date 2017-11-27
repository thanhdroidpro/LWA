#!/usr/bin/env bash
echo "Start Build Apk"

./gradlew clean
./gradlew assembleProductionRelease -Dchannel=kinglloy
./gradlew assembleProductionRelease -Dchannel=yingyongbao
./gradlew assembleProductionRelease -Dchannel=360
./gradlew assembleProductionRelease -Dchannel=vivo
./gradlew assembleProductionRelease -Dchannel=flyme
./gradlew assembleProductionRelease -Dchannel=wandoujia
./gradlew assembleProductionRelease -Dchannel=baidu
./gradlew assembleProductionRelease -Dchannel=google
./gradlew assembleProductionRelease -Dchannel=huawei
./gradlew assembleProductionRelease -Dchannel=coolapk

./gradlew assembleUltimateRelease -Dchannel=google

echo "Build Apk Complete"
