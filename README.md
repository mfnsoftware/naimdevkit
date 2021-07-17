# naimdevkit
Naim Developer Kit Android Kotlin

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
			maven { url 'https://developer.huawei.com/repo/' }
		}
	}
  
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.naimyag:naimdevkit:1.0.1'
		
		//firebase
    	api platform('com.google.firebase:firebase-bom:26.5.0')
    	api 'com.google.firebase:firebase-messaging-ktx'

    	//huawei
    	api 'com.huawei.hms:push:5.3.0.304'

    	//notification badge
    	api "me.leolin:ShortcutBadger:1.1.22@aar"

    	//Dialog
    	implementation 'com.github.andreilisun:swipedismissdialog:0.1'

    	//Retrofit
    	api "com.squareup.retrofit2:retrofit:2.9.0"
    	api "com.squareup.retrofit2:converter-gson:2.9.0"
    	api "com.squareup.retrofit2:adapter-rxjava3:2.9.0"
    	api 'com.squareup.okhttp3:okhttp:4.9.0'
    	api 'com.squareup.retrofit2:converter-scalars:2.9.0'

    	//Rx
    	api "io.reactivex.rxjava3:rxkotlin:3.0.1"
    	api "io.reactivex.rxjava3:rxandroid:3.0.0"
		
	}

Step 3. Fix Duplicate class error
	gradle.properties
 
	android.enableJetifier=true
