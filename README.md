![enter image description here][1]

Android Social Networks Extended
=====================
ASNE это расширение библиотеки [Android Social Networks][2] добавляющее множество новых возможностей. Она сохранила простоту использования и интеграции в ваши проекты! Интеграция социальных сетей никогда не была настолько проста.

Социальные сети:

 - Twitter  
 - LinkedIn  
 - Facebook 
 - Google Plus 
 - Vkontakte 
 - Odnoklassniki

Возможности
-----------
Библеотека охватывает практически все необходимые запросы к социальным сетям

 - Подключение и вход в социальные сети
 - Настройка необходимых вам разрешений для социальных сетей
 - Получение Access Token
 - Получение профиля текущего пользователя
 - Получение профиля любого пользователя
 - Получение профилей массива пользователей
 - Детальная информация о пользователе
 - Отправка сообщений в ленту пользователя
 - Прикрепление изображений в ленту пользователя
 - Отправка ссылки в ленту пользователя
 - Вызов диалогового окна с предварительно составленным сообщением
 - Проверка является ли пользователь другом текущего пользователя
 - Запрос списка друзей
 - Добавление друга
 - Удаление друга
 
![enter image description here][3]

Демо
====
[Ссылка на страницу загрузки версий демо приложения][4]

[Прямая ссылка на загрузку последней версии демо][5]

Подключение библиотеки
----------------------
Для начала необходимо создать и настроить приложения в необходимых социальных сетях:
 
 - [Twitter](https://github.com/gorbin/ASNE/wiki/%D0%A1%D0%BE%D0%B7%D0%B4%D0%B0%D0%BD%D0%B8%D0%B5-%D0%BF%D1%80%D0%B8%D0%BB%D0%BE%D0%B6%D0%B5%D0%BD%D0%B8%D1%8F-Twitter)  
 - [LinkedIn](https://github.com/gorbin/ASNE/wiki/%D0%A1%D0%BE%D0%B7%D0%B4%D0%B0%D0%BD%D0%B8%D0%B5-%D0%BF%D1%80%D0%B8%D0%BB%D0%BE%D0%B6%D0%B5%D0%BD%D0%B8%D1%8F-LinkedIn)  
 - [Facebook](https://github.com/gorbin/ASNE/wiki/%D0%A1%D0%BE%D0%B7%D0%B4%D0%B0%D0%BD%D0%B8%D0%B5-%D0%BF%D1%80%D0%B8%D0%BB%D0%BE%D0%B6%D0%B5%D0%BD%D0%B8%D1%8F-Facebook) 
 - [Google Plus](https://github.com/gorbin/ASNE/wiki/%D0%A1%D0%BE%D0%B7%D0%B4%D0%B0%D0%BD%D0%B8%D0%B5-%D0%BF%D1%80%D0%B8%D0%BB%D0%BE%D0%B6%D0%B5%D0%BD%D0%B8%D1%8F-Google-Plus) 
 - [Vkontakte](https://github.com/gorbin/ASNE/wiki/%D0%A1%D0%BE%D0%B7%D0%B4%D0%B0%D0%BD%D0%B8%D0%B5-%D0%BF%D1%80%D0%B8%D0%BB%D0%BE%D0%B6%D0%B5%D0%BD%D0%B8%D1%8F-Vkontakte) 
 - [Odnoklassniki](https://github.com/gorbin/ASNE/wiki/%D0%A1%D0%BE%D0%B7%D0%B4%D0%B0%D0%BD%D0%B8%D0%B5-%D0%BF%D1%80%D0%B8%D0%BB%D0%BE%D0%B6%D0%B5%D0%BD%D0%B8%D1%8F-Odnoklassniki)


Далее необходимо создать `mSocialNetworkManager`, с помощью которого можно подключить необходимые социальные сети. Для этого можно воспользоваться `SocialNetworkManager.Builder` следующим образом:

```java
mSocialNetworkManager = (SocialNetworkManager) getFragmentManager().findFragmentByTag(SOCIAL_NETWORK_TAG);
	if (mSocialNetworkManager == null) {
        mSocialNetworkManager = SocialNetworkManager.Builder.from(getActivity())
            .twitter(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET)
            .facebook(fbScope)
            .googlePlus()
            .linkedIn(LINKEDIN_CONSUMER_KEY, LINKEDIN_CONSUMER_SECRET, linkedInScope)
            .vk(VK_KEY, vkScope)
            .ok(OK_APP_ID, OK_PUBLIC_KEY, OK_SECRET_KEY, okScope)
            .build();
        getFragmentManager().beginTransaction().add(mSocialNetworkManager, SOCIAL_NETWORK_TAG).commit();
	}
```            
 Далее вы можете отправлять запросы к социально сети следующим образом(на примере логина):
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
Или обращаться напрямую к объекту социальной сети и составлять свои запросы.

Добавление библиотеки в проект
=====================
Библиотека находится в разработке, поэтому пока вы можете подключить ее как модуль в ваш проект, в дальнейшем проект библиотека может быть собрана в aar/jar

Например в AndroidStudio вы можете добавить библиотеку в Ваш проект с использованием Gradle: 

 1. Скопируйте директорию library в директорию Вашего проекта.
 2. Найдите settings.gradle. Скорее всего, оно содержит что-то вроде `include ':app'` - jтредактируйте строку следующим образом `include ':library',':app' `
 3. Ваш проект теперь содержит модуль library. Необходимо добавить его как зависимость к Вашему приложению. Найдите build.gradle в поддиректории модуля Вашего приложения (например YOUR_PROJECT/app/build.gradle) Добавьте новую строку в dependencies: `compile project(':library') `

Если Ваш проект не поддерживает Gradle, добавить SDK можно следующим образом: 
 1. Откройте Project Settings и выберите Modules. 
 2. Нажмите кнопку «Добавить» (+), и выберите Import module 
 3. Найдите директорию с VK SDK и выберите library, нажмите «Добавить». 
 4. Выберите Create module from existing sources, затем два раза нажмите "Next" переименуйте модуль из "main" в "asne", снова нажмите "next". 
 5. Добавьте новый модуль asne зависимостью к модулю Вашего приложения. 

**Не забудьте нодключинь необходимые библиотеки в проект - проведите те же действия с папкой ``third_party``**

Важные замечания
=====================
**Библиотека находится в стадии разработки, поэтому возможны ошибки - обращайтесь исправлю**

**Библиотека не заботиться о состоянии вашего приложения и жизненого цикла, вы должны делать это сами!**

**Если Вы используете Google Plus, добавьте этот код в вашу активити:**
```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    /**
     * This is required only if you are using Google Plus, the issue is that there SDK
     * require Activity to launch Auth, so library can't receive onActivityResult in fragment
     */
    Fragment fragment = getSupportFragmentManager().findFragmentByTag(BaseDemoFragment.SOCIAL_NETWORK_TAG);
    if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
```

**Ограничения Facebook**

Политика Facebook позволяет отправлять сообщения в ленту пользователя в обход диалога и получить день рождение или расположение только после одобрения приложения командой Facebook, поэтому чать функционала вам может быть не доступна, так как они все никак не одобрять мое приложение.
Но есть выход - вы можете отправить мне ваш facebook id и я добавлю вас в качестве тестера, тогда вы сможете воспользоваться полным функционалом. email для связи: gorbin.e.o@gmail.com

После последнего обновления API, Facebook отдает лишь список Ваших друзей использующих это же приложение - что приведет к пустому списку друзей, но если вы добавите меня в друзья, то увидите как минимум меня([профиль][6])

**Проблемы с LinkedIn**

Иногда отваливается связь с LinkedIn сервером, но при этом при следующем запросе востанавливается - решаю данную проблему

Лицензия
=====================
ASNE доступна по лицензии [MIT license](http://opensource.org/licenses/MIT):

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

  [1]: http://i.imgur.com/njXNyEC.png
  [2]: https://github.com/gorbin/AndroidSocialNetworks
  [3]: http://i.imgur.com/J3WhMQ0.png
  [4]: https://github.com/gorbin/ASNE/releases
  [5]: https://github.com/gorbin/ASNE/releases/download/0.2/ASNE-debug-unaligned.apk
  [6]: https://www.facebook.com/evgeny.gorbin
