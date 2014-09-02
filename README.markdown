# TCSlackNotifierPlugin - TeamCity -> Slack Notifications

A plugin for [TeamCity](http://www.jetbrains.com/teamcity/) to post notifications to [slack](https://slack.com/)

It works by registering as a server listener, and posts to slack on build events like successful builds (optionally also builds starting and failing)

#Build Plugin

Gradle is used to build. Wrapper is included in the project so you dont need to install it, just have java.

    ./gradlew buildZip

this will generate a zip file with the right meta data in the right folder structure at : `build/distributions/TCSlackNotifierPlugin-<version>.zip`

#Install Plugin

Copy the zip file into TeamCity plugin directory inside the data directory, usually `.BuildServer`

```
scp build/distributions/TCSlackNotifierPlugin-<version>.zip buildserver:.BuildServer/plugins/slackNotifier.zip
```

Then restart TeamCity.

#Configuration

###In slack
Add a new webhook integration. Make a note of the Token.

###In TeamCity

Edit the main config file, usually `.BuildServer/config/main-config.xml` and add an element like so:

```
<server rootURL="http://localhost:8111">
  ...
  <slackNotifier>
    <slackWebToken>[TOKEN]</slackWebToken>
    <slackDefaultChannel>#general</slackDefaultChannel>
    <slackPostUrl>https://[TEAM].slack.com/services/hooks/incoming-webhook?token=</slackPostUrl>
    <slackLogoUrl>http://build.tapadoo.com/img/icons/TeamCity32.png</slackLogoUrl>
    <notifications>
        <global>
            <notification>started</notification>
            <notification onlyFirst="true">succeeded</notification>
            <notification>failed</notification>
        </global>
        <personal>
            <notification>succeeded</notification>
            <notification onlyFirst="true">failed</notification>
        </personal>
    </notifications>
  </slackNotifier>
  ...
</server>
```

On Slack, locate the URL on the webhook integration page. Replace [TOKEN] with the token from the end of the slack URL. Change [TEAM] to point to the right slack team as noted at the beginning of the URL. Change the logo url whatever you want.

####Notifications
Configure the notifications you would like to receive within the `<notifications>` block.

Notifications can be sent in either of two scopes, global and personal.

Public notifications can be configured within the `<global>` block. Private, personal build notifications are configured within the `<personal>` block. To disable all public or private notifications, simple exclude the scope block from the config.

As to the notifications themselves, define the notifications you would like to receive in each scope by including a `<notification>` element with one of three values: started, succeeded, and failed. The usage of each is self-evident.

Notifications with a value of _succeeded_ or _failed_ may be optionally decorated with the `onlyFirst="true"` attribute. Adding this attribute to a succeeded or failed notification will cause TeamCity to only send notifications on the first successful build after a build failure, or on first failure after a successful build respectively.

In the above example, global notifications will be sent whenever a build starts or fails but, for success, only when the build had been failing and is fixed. Private notifications (sent to the @username channel) will be sent every time a personal build succeeds, never when a build starts, and only once when a succeeding build fails for the first time.

This by default will post all builds to slack. you can tweak these on a project level though

####Project Config (Optional)

To change channel or disable per project:

Edit the plugin specific xml config, `plugin-settings.xml` probably somewhere inside `.BuildServer/config/projects/PROJECTNAME`
```
<settings>
  <slackSettings enabled="true">
    <channel>#blah</channel>
  </slackSettings>
</settings>
```

#Note on TeamCity version support

I'm still using **TeamCity 7.1** , but a few tests on the free version of TeamCity 8 went fine, and it seems to work there also.

###Issues

* all xml config - needs web ui extensions for updating settings from GUI. Considering it.
* channel can be changed per-project either by environmental variable (SLACK_CHANNEL) or by changing the project specific xml in the data directory. This could also use web ui extension UI for editing.
* All or nothing notifications. By default, all builds are posted. It can be disabled per project, but not currently by build config.


# License

MIT License.
