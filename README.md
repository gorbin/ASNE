Unfreeze project - merge all puls for master branch - current version was not tested - **NOT STABLE**

**Sorry, but I freeze work on project for some time(I hope not long, nearly month) - too busy. I saw all Issues and will resolve them as soon as I can.** 

**I checked project - current maven libs mostly works fine. For now I need help with Issues and open for pull requests - will check them and merge on weekends.**

![enter image description here][1]

ASNE        [![ASNE Maven Central](http://img.shields.io/badge/ASNE%20Maven%20Central-0.3.1-brightgreen.svg?style=flat)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.asne%22) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ASNE-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/868)
=====================
ASNE library created for simple integration of social setworks to android applications. If you want to integrate your application with multiple social networks just choose ASNE modules and add them to your project. You just need to: add module, build SocialNetworkManager and configure your AndroidManiferst. 
ASNE contains common interface for most popular social networks, but you can easily make module for another.

ASNE contains modules for social networks:
 - Twitter  
 - LinkedIn  
 - Facebook 
 - Google Plus 
 - Instagram
 - Vkontakte 
 - Odnoklassniki

Table of contents
-----------
[Features](https://github.com/gorbin/ASNE/blob/master/README.md#features)  
[Documentation](https://github.com/gorbin/ASNE/blob/master/README.md#documentation)  
[Demo app](https://github.com/gorbin/ASNE/blob/master/README.md#demo-app)  
[Getting started](https://github.com/gorbin/ASNE/blob/master/README.md#getting-started)  
[Important](https://github.com/gorbin/ASNE/blob/master/README.md#important)  
[Developed By](https://github.com/gorbin/ASNE/blob/master/README.md#developed-by)  
[License](https://github.com/gorbin/ASNE/blob/master/README.md#license)  

Features
-----------
ASNE got [almost all necessary requests](https://github.com/gorbin/ASNE/wiki/%D0%A1%D0%BF%D0%B8%D1%81%D0%BE%D0%BA-%D0%BC%D0%B5%D1%82%D0%BE%D0%B4%D0%BE%D0%B2-SocialNetwork) to social networks

 - Login
 - Configure necessary permissions
 - Get Access Token
 - Get current person social profile
 - Get social profile of user by id
 - Get social profile for array of users 
 - Get detailed user profile
 - Share message
 - Share photo
 - Share link
 - Request Share dialog with message/photo/link
 - Check is user(by id) is friend of current
 - Get list of Friends
 - Adding friends by id
 - Remove friend from friend list
 - Any request to chosen social network - you got full SDK/API
 
![enter image description here][3]

Documentation
-----------
[Javadoc](http://gorbin.github.io/ASNE/)

[**Tutorial project on GitHub**](https://github.com/gorbin/ASNETutorial)

[**Tutorial article on CodeProject**](http://www.codeproject.com/Articles/815900/Android-social-network-integration)

Demo app
====
[Link for releases][4]

<a href="https://play.google.com/store/apps/details?id=com.gorbin.androidsocialnetworksextended.asne">
  <img alt="Get it on Google Play"
       src="https://developer.android.com//images/brand/ru_generic_rgb_wo_60.png" />
</a>

Getting started
=====================

**Adding library**

_1) Using Maven Central_

Add dependency for chosen module, here example for all modules, you can choose one or two

```
dependencies {
...
    compile 'com.github.asne:asne-facebook:0.3.3'
    compile 'com.github.asne:asne-twitter:0.3.3'
    compile 'com.github.asne:asne-googleplus:0.3.3'
    compile 'com.github.asne:asne-linkedin:0.3.3'
    compile 'com.github.asne:asne-instagram:0.3.3'
    compile 'com.github.asne:asne-vk:0.3.3'
    compile 'com.github.asne:asne-odnoklassniki:0.3.3'
...
}
```

_2) Import module to your project_

For example, in AndroidStudio you can add modules via Gradle: 

 1. Copy social module to your project.
 2. In settings.gradle include `':ASNECore', ':socialNetworkModuleName'`
 3. In build.gradle of your app (YOUR_PROJECT/app/build.gradle) add new dependencies: `compile project(':socialNetworkModuleName') `

Without Gradle, add ASNE like: 
 1. Open Project Settings and choose Modules. 
 2. Find button "Add" (+), and choose Import module 
 3. Find ASNECore and socialNetworkModuleName directories - «Add». 
 4. Choose Create module from existing sources, then click "Next" rename module from "main" to "ASNECore". 
 5. Add new asne-module in dependencies to your app. 

**Using library**

Firstly, you need to create app in social network. You can read about main steps:

 - [Twitter](https://github.com/gorbin/ASNE/wiki/Create-Twitter-App)
 - [LinkedIn](https://github.com/gorbin/ASNE/wiki/Create-LinkedIn-App)
 - [Facebook](https://github.com/gorbin/ASNE/wiki/Create-Facebook-App)
 - [Google Plus](https://github.com/gorbin/ASNE/wiki/Create-Google-Plus-app) 
 - [Instagram](https://github.com/gorbin/ASNE/wiki/Create-Instagram-App)
 - [Vkontakte](https://github.com/gorbin/ASNE/wiki/Create-Vkontakte-App) 
 - [Odnoklassniki](https://github.com/gorbin/ASNE/wiki/Create-Odnoklassniki-App)

Second, we need to catch response after login via social network login dialog:

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    Fragment fragment = getSupportFragmentManager().findFragmentByTag(BaseDemoFragment.SOCIAL_NETWORK_TAG);
    if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
```

Then, you need to initialize `mSocialNetworkManager`, it contain common interface for all ASNE social network modules. Initialize chosen social network and add social network to SocialNetworkManager(example: FacebookSocialNetwork):

```java
mSocialNetworkManager = (SocialNetworkManager) getFragmentManager().findFragmentByTag(SOCIAL_NETWORK_TAG);
	if (mSocialNetworkManager == null) {
        mSocialNetworkManager = new SocialNetworkManager();
		FacebookSocialNetwork fbNetwork = new FacebookSocialNetwork(this, fbScope);
		//or from an activity
		//FacebookSocialNetwork fbNetwork = new FacebookSocialNetwork(mSocialNetworkManager, this, fbScope);
        mSocialNetworkManager.addSocialNetwork(fbNetwork);
        getFragmentManager().beginTransaction().add(mSocialNetworkManager, SOCIAL_NETWORK_TAG).commit();
	}
```    
     
where `fbScope` is **permissions** for your app, for example I used:

```java
ArrayList<String> fbScope = new ArrayList<String>();
fbScope.addAll(Arrays.asList("public_profile, email, user_friends, user_location, user_birthday"));
```

 Then you can send requests to social network like:

 ```java
	mSocialNetworkManager.getVKSocialNetwork().requestLogin(new OnLoginCompleteListener() {
        @Override
        public void onLoginSuccess(int socialNetworkID) {

        }

        @Override
        public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {

        }
    });
```

Or get Social network directly like:

```java
	Session session = Session.getActiveSession();
```

Important
=====================

**Facebook Upgrades**

Facebook some permissions you can get only after Facebook submission, so my demo app wasn't submitted due low functionality. So if you want to use it with all functionality send me your facebook id and I add you as tester - this is easy way to to fully use demo app
email: gorbin.e.o@gmail.com

Apps are no longer able to retrieve the full list of a user's friends (only those friends who have specifically authorized your app using the user_friends permission) but if you add me as friend you will see me in friendlist([profile][6])

Developed By
=====================
ASNE developed on the basis of ([Android Social Networks][2]) mostly redone and add new features(some features are pulled to Android Social Networks)

Evgeny Gorbin - <gorbin.e.o@gmail.com>

<a href="https://plus.google.com/108381948947848082245">
  <img alt="Follow me on Google+"
       src="https://raw.githubusercontent.com/gorbin/ASNE/master/resources/gp.png" />
</a>
<a href="https://twitter.com/egorbin">
  <img alt="Follow me on Twitter"
       src="https://raw.githubusercontent.com/gorbin/ASNE/master/resources/twitter.png" />
</a>
License
=====================
ASNE is made available under the MIT license: [MIT license](http://opensource.org/licenses/MIT):

<pre>
The MIT License (MIT)

Copyright (c) 2014 Evgrny Gorbin

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
</pre>

  [1]: https://raw.githubusercontent.com/gorbin/ASNE/master/resources/recomended.png
  [2]: https://github.com/gorbin/AndroidSocialNetworks
  [3]: https://raw.githubusercontent.com/gorbin/ASNE/master/resources/main.png
  [4]: https://github.com/gorbin/ASNE/releases
  [5]: https://github.com/gorbin/ASNE/releases/download/0.2/ASNE-debug-unaligned.apk
  [6]: https://www.facebook.com/evgeny.gorbin
