package com.tapadoo.slacknotifier;

import java.util.HashMap;
import java.util.List;
import jetbrains.buildServer.serverSide.MainConfigProcessor;
import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Element;

/**
 * Created by jasonconnery on 02/03/2014.
 */
public class SlackConfigProcessor implements MainConfigProcessor {

    public static final String PREF_KEY_SLACK_WEB_TOKEN = "slackWebToken";
    public static final String PREF_KEY_SLACK_DEF_CHANNEL = "slackDefaultChannel";
    public static final String PREF_KEY_SLACK_POSTURL = "slackPostUrl";
    public static final String PREF_KEY_SLACK_LOGOURL = "slackLogoUrl";

    private static final String PREF_CHILD_ELEMENT = "slackNotifier";

    private static final String PREF_KEY_NOTIFICATIONS = "notifications";
    private static final String PREF_KEY_GLOBAL_NOTIFICATIONS = "global";
    private static final String PREF_KEY_PERSONAL_NOTIFICATIONS = "personal";
    private static final String PREF_KEY_NOTIFICATION = "notification";
    private static final String ATTR_NAME_ONLY_FIRST = "onlyFirst";

    private String token = "invalidToken";
    private String defaultChannel = "#general";
    private String postUrl;
    private String logoUrl;
    
    private HashMap<String, HashMap<String, String>> notifications = new HashMap();

    public SlackConfigProcessor() {

    }

    public void init() {

    }

    public boolean shouldPost(String scope, String notification) {
        return this.notifications.containsKey(scope) && this.notifications.get(scope).containsKey(notification);
    }

    public boolean postSuccessful() {
        return this.shouldPost("global", "succeeded");
    }

    public boolean postFailed() {
        return this.shouldPost("global", "failed");
    }

    public boolean postStarted() {
        return this.shouldPost("global", "started");
    }

    public boolean postFirstFailure() {
        return this.shouldPost("global", "firstFailure");
    }

    public boolean postFirstSuccess() {
        return this.shouldPost("global", "firstSuccess");
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

        if( mainConfigElement == null ) {
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
            String[] notificationScopes = { PREF_KEY_GLOBAL_NOTIFICATIONS, PREF_KEY_PERSONAL_NOTIFICATIONS };
            for ( String scope : notificationScopes ) {
                Element scopeRoot = notificationsRoot.getChild( scope );
                if ( scopeRoot != null ) {
                    HashMap<String, String> types = new HashMap<String, String> ();

                    for ( Element notification : (List<Element>) scopeRoot.getChildren(PREF_KEY_NOTIFICATION) ) {
                        String type = notification.getTextTrim();
                        if ( type != "started" ) {
                            Attribute onlyFirstAttr = notification.getAttribute(ATTR_NAME_ONLY_FIRST);
                            if ( onlyFirstAttr != null ) {
                                boolean onlyFirst = false;
                                try {
                                    onlyFirst = onlyFirstAttr.getBooleanValue();
                                }
                                catch ( DataConversionException ex ) { /* swallow */ }

                                if ( onlyFirst ) {
                                    if ( type == "succeeded" ) {
                                        type = "firstSuccess";
                                    }
                                    else if ( type == "failed" ) {
                                        type = "firstFailure";
                                    }
                                }                           
                            } 
                        }

                        types.put(type, "");
                    }
                    
                    if ( types.size() > 0 ) {
                        this.notifications.put(scope, types);
                    }
                }
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
