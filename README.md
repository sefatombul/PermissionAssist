# PermissionAssist

[![](https://jitpack.io/v/sefatombul/PermissionAssist.svg)](https://jitpack.io/#sefatombul/PermissionAssist)

PermissionAssist is a helper for Android that will make your permission processes easier..

## Installation

### Add the JitPack repository to your build file
In settings.gradle, add maven { url 'https://jitpack.io' }

```groovy
dependencyResolutionManagement {
    ...
    repositories {
        google()
        mavenCentral()

        maven { url 'https://jitpack.io' }
    }
}
```
### Add the dependency
```groovy
dependencies {
       implementation 'com.github.sefatombul:PermissionAssist:latest'
	}
```
## Usage
Using PermissionAssist to request Android runtime permissions is simple. The permissions we request; RECORD_AUDIO, CAMERA, CALL_PHONE, WRITE_EXTERNAL_STORAGE and LOCATION permissions, we added them to AndroidManifest.xml.

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    package="com.sefatombul.permissionassist">

    <uses-permission android:name="android.permission.CALL_PHONE"/>
    <uses-permission android:name="android.permission.CAMERA"/>
    <uses-permission android:name="android.permission.RECORD_AUDIO"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />


```
PermissionAssist object must be created in OnCreate method. Otherwise, an error may be received regarding LifecycleOwners.
```kotlin
lateinit var permissionAssist:PermissionAssist

override fun onCreate(savedInstanceState: Bundle?) {
    ...
    permissionAssist = PermissionAssist(this@MainActivity)
}

```
LifecycleOwners Error 
```bash
java.lang.RuntimeException: Unable to resume activity {....MainActivity}: java.lang.IllegalStateException: LifecycleOwner ....MainActivity@bf670cc is attempting to register while current state is STARTED. LifecycleOwners must call register before they are STARTED.
```
Then you can use below codes to request.
```kotlin
var permissions = arrayOf(
        Manifest.permission.CALL_PHONE,
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )

permissionAssist.permissionDialogTitle("Application Permissions")
                    .permissionDialogDescription("Please grant permissions for the app to work stable.")
                    .permissionDialogAllowText("Allow")
                    .permissionDialogNegativeText("Cancel")
                    .permissionDialogOpenSettingsButtonText("Settings")
                    .setNegativeButtonVisible(true)
                    .cancelable(false)
                    .requestPermissions(permissions) { allGranted, grantedList, deniedList ->
                        println("allGranted : $allGranted")
                        grantedList.forEach {
                            println("grantedList : $it")
                        }
                        deniedList.forEach {
                            println("deniedList : $it")
                        }
                    }


```
#### permissionDialogTitle
Specifies the title of the popup that will be shown when the user disallows.
#### permissionDialogDescription
Specifies the description of the popup that will be shown when the user disallows.
#### permissionDialogAllowText
Specifies the positive button text of the popup that will be shown when the user disallows 1 time. When you click the positive  button, permission is requested.
#### permissionDialogOpenSettingsButtonText
If the user does not give permission more than once, the permission process is blocked. In this case, it is necessary to redirect to the settings page. It is the text of the positive button that will appear to redirect to the settings page.
#### permissionDialogNegativeText
It is the text of the negative button used to deny the state of going to the settings page or the state of obtaining permission.
#### setNegativeButtonVisible
You can set whether the negative button, which is used to deny the state of going to the settings page or getting permission, is visible in the popup. If you set "false" value, the user will not see a button to cancel the operation.
#### cancelable
You can set the status of going to the settings page or whether the popup to be shown for permission status can be cancelled. If setNegativeButtonVisible "false" and cancelable "false" are set, the user cannot proceed to the next step without giving permission.
#### requestPermissions
It is a permission request command. With this command, permission is requested from the user and whether the user grants the permission or not is returned.

## License

```
Copyright (C) Sefa Tombul, PermissionAssist Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
