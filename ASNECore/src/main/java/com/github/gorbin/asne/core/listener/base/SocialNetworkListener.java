package com.github.gorbin.asne.core.listener.base;

/**
 * Base interface definition for a callback to be invoked when any social network request complete.
 */
public interface SocialNetworkListener {
    /**
     * Called when social network request complete with error.
     * @param socialNetworkID id of social network where request was complete with error
     * @param requestID id of request where request was complete with error
     * @param errorMessage error message where request was complete with error
     * @param data data of social network where request was complete with error
     */
    public void onError(int socialNetworkID, String requestID, String errorMessage, Object data);
}
