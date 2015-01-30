var parsePlugin = {
    initialize: function(appId, clientKey, successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'ParsePlugin',
            'initialize',
            [appId, clientKey]
        );
    },

    getInstallationId: function(successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'ParsePlugin',
            'getInstallationId',
            []
        );
    },

    getInstallationObjectId: function(successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'ParsePlugin',
            'getInstallationObjectId',
            []
        );
    },

    getSubscriptions: function(successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'ParsePlugin',
            'getSubscriptions',
            []
        );
    },

    subscribe: function(channel, successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'ParsePlugin',
            'subscribe',
            [ channel ]
        );
    },

    unsubscribe: function(channel, successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'ParsePlugin',
            'unsubscribe',
            [ channel ]
        );
    },
    
    sendOfflineEventDetails: function(eventName, userId, gaClientId, firstName, lastName, email, phoneNumber, successCallback, errorCallback) {
    	cordova.exec(
                successCallback,
                errorCallback,
                'ParsePlugin',
                'sendOfflineEventDetails',
                [eventName, userId, gaClientId, firstName, lastName, email, phoneNumber]
           );
    },
    
    countOfflineEventDetails: function(successCallback, errorCallback) {
    	cordova.exec(
                successCallback,
                errorCallback,
                'ParsePlugin',
                'countOfflineEventDetails',
                []
        );
    },
    
    flushOfflineEventDetails: function(successCallback, errorCallback) {
    	cordova.exec(
                successCallback,
                errorCallback,
                'ParsePlugin',
                'flushOfflineEventDetails',
                []
        );
    }
};
module.exports = parsePlugin;
