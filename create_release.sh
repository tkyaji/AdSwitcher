#!/bin/bash

VERSION=${1}

if [ -z "$VERSION" ]; then
    echo "Usage: ${0} <version>"
    exit 1
fi

cd `dirname $0`

DIR="release-${VERSION}"
rm -fr ${DIR}
mkdir ${DIR}
cd ${DIR}


# iOS

CORE_DIR="adswitcher-ios-${VERSION}"
ADAPTERS_DIR="adswitcher-ios-adapters-${VERSION}"

mkdir ios
cd ios

mkdir ${CORE_DIR}
mkdir ${ADAPTERS_DIR}

cp -R ../../AdSwitcher-iOS/AdSwitcher/AdSwitcher ./${CORE_DIR}
cp -R ../../AdSwitcher-iOS/AdSwitcher/Adapters ./${ADAPTERS_DIR}

find . -name ".*" -type f | xargs -I{} rm -f {}
find ${ADAPTERS_DIR} -name "Libraries" -type d | xargs -I{} rm -fr {}

zip -r ${CORE_DIR}.zip ${CORE_DIR}
zip -r ${ADAPTERS_DIR}.zip ${ADAPTERS_DIR}

rm -fr ${CORE_DIR}
rm -fr ${ADAPTERS_DIR}

cd ..


# Android

mkdir android
cd android

CORE_DIR="adswitcher-android-${VERSION}"
ADAPTERS_DIR="adswitcher-android-adapters-${VERSION}"

mkdir ${CORE_DIR}
mkdir ${ADAPTERS_DIR}

cp ../../AdSwitcher-Android/libs/*.jar ${ADAPTERS_DIR}
mv ${ADAPTERS_DIR}/adswitcher.jar ${CORE_DIR}

zip -r ${CORE_DIR}.zip ${CORE_DIR}
zip -r ${ADAPTERS_DIR}.zip ${ADAPTERS_DIR}

rm -fr ${CORE_DIR}
rm -fr ${ADAPTERS_DIR}

cd ..


# Unity

mkdir unity
cd unity

CORE_DIR="adswitcher-unity-${VERSION}"
ADAPTERS_DIR="adswitcher-unity-adapters-${VERSION}"

mkdir ${CORE_DIR}
mkdir ${ADAPTERS_DIR}

cp ../../AdSwitcher-Unity/AdSwitcher.unitypackage ${CORE_DIR}
cp ../../AdSwitcher-Unity/Adapters/*.unitypackage ${ADAPTERS_DIR}/.

zip -r ${CORE_DIR}.zip ${CORE_DIR}
zip -r ${ADAPTERS_DIR}.zip ${ADAPTERS_DIR}

rm -fr ${CORE_DIR}
rm -fr ${ADAPTERS_DIR}

cd ..

