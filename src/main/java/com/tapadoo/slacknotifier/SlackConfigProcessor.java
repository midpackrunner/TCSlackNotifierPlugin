package com.tapadoo.slacknotifier;

import jetbrains.buildServer.serverSide.MainConfigProcessor;
import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Element;
import java.util.HashMap;

/**
 * Created by jasonconnery on 02/03/2014.
 */
public class SlackConfigProcessor implements MainConfigProcessor {

    public static final String PREF_KEY_SLACK_WEB_TOKEN = "slackWebToken";
    public static final String PREF_KEY_SLACK_DEF_CHANNEL = "slackDefaultChannel";
    public static final String PREF_KEY_SLACK_POSTURL = "slackPostUrl";
    public static final String PREF_KEY_SLACK_LOGOURL = "slackLogoUrl";

    private static final String PREF_CHILD_ELEMENT = "slackNotifier";

    private static final java.lang.String PREF_KEY_NOTIFICATIONS = "notifications";
    private static final java.lang.String PREF_KEY_NOTIFICATION = "notification";

    private String token = "invalidToken";
    private String defaultChannel = "#general";
    private String postUrl;
    private String logoUrl;
    
    private HashMap<String, String> notifications = new HashMap();

    public SlackConfigProcessor() {

    }

    public void init() {

    }

    public boolean postSuccessful() {
        return notifications.containsKey('succeeded');
    }

    public boolean postFailed() {
        return notifications.containsKey('failed');
    }

    public boolean postStarted() {
        return notifications.containsKey('started');
    }

    public boolean postFirstFailure() {
        return notifications.containsKey('failedAfterSucceeded');
    }

    public boolean postFirstSuccessful() {
        return notifications.containsKey('succeededAfterFailed');
    }

    public boolean postPersonal() {
        return notifications.containsKey('personal');
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDefaultChannel() {
        return defaultChannel;
    }

    public void setDefaultChannel(String defaultChannel) {
        this.defaultChannel = defaultChannel;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public void readFrom(org.jdom.Element element) {
        Element mainConfigElement = element.getChild(PREF_CHILD_ELEMENT);

        if( mainConfigElement == null )
        {
            token = "" ;
            postUrl = "http://localhost/?token=" ;
            return ;
        }

        token = mainConfigElement.getChildText(PREF_KEY_SLACK_WEB_TOKEN);
        defaultChannel = mainConfigElement.getChildText(PREF_KEY_SLACK_DEF_CHANNEL);
        postUrl = mainConfigElement.getChildText(PREF_KEY_SLACK_POSTURL);
        logoUrl = mainConfigElement.getChildText(PREF_KEY_SLACK_LOGOURL);

        Element notificationsRoot = mainConfigElement.getChild(PREF_KEY_NOTIFICATIONS);
        if ( notificationsRoot != null ) {
            for ( Element notification : notificationsRoot.getChildren(PREF_KEY_NOTIFICATION) ) {
                notifications.put(notification.getTextTrim(), null);
            }
        }
    }

    public void writeTo(org.jdom.Element element) {

        Element mainConfigElement = new Element(PREF_CHILD_ELEMENT);
        Element webTokenElement = new Element(PREF_KEY_SLACK_WEB_TOKEN);
        Element defChannelElement = new Element(PREF_KEY_SLACK_DEF_CHANNEL);
        Element postUrlElement = new Element(PREF_KEY_SLACK_POSTURL);
        Element logoUrlElement = new Element(PREF_KEY_SLACK_LOGOURL);

        webTokenElement.setText(token);
        defChannelElement.setText(defaultChannel);

        mainConfigElement.addContent(webTokenElement);
        mainConfigElement.addContent(defChannelElement);
        mainConfigElement.addContent(postUrlElement);
        mainConfigElement.addContent(logoUrlElement);

        element.addContent(mainConfigElement);


    }
}
