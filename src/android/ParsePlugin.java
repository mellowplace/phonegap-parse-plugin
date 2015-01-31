package org.apache.cordova.core;

import java.util.Set;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.PushService;
import com.parse.ParseQuery;
import com.parse.ParseException;
import com.parse.FindCallback;
import com.parse.SaveCallback;

import java.util.TimeZone;
import java.util.Date;
import java.util.Locale;
import java.util.List;
import java.text.SimpleDateFormat;

import android.content.Context;


public class ParsePlugin extends CordovaPlugin {
    public static final String ACTION_INITIALIZE = "initialize";
    public static final String ACTION_GET_INSTALLATION_ID = "getInstallationId";
    public static final String ACTION_GET_INSTALLATION_OBJECT_ID = "getInstallationObjectId";
    public static final String ACTION_GET_SUBSCRIPTIONS = "getSubscriptions";
    public static final String ACTION_SUBSCRIBE = "subscribe";
    public static final String ACTION_UNSUBSCRIBE = "unsubscribe";
    public static final String ACTION_EVENTDETAILS = "sendOfflineEventDetails";
    public static final String ACTION_COUNTEVENTDETAILS = "countOfflineEventDetails";
    public static final String ACTION_FLUSHEVENTDETAILS = "flushOfflineEventDetails";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals(ACTION_INITIALIZE)) {
            this.initialize(callbackContext, args);
            return true;
        }
        if (action.equals(ACTION_GET_INSTALLATION_ID)) {
            this.getInstallationId(callbackContext);
            return true;
        }

        if (action.equals(ACTION_GET_INSTALLATION_OBJECT_ID)) {
            this.getInstallationObjectId(callbackContext);
            return true;
        }
        if (action.equals(ACTION_GET_SUBSCRIPTIONS)) {
            this.getSubscriptions(callbackContext);
            return true;
        }
        if (action.equals(ACTION_SUBSCRIBE)) {
            this.subscribe(args.getString(0), callbackContext);
            return true;
        }
        if (action.equals(ACTION_UNSUBSCRIBE)) {
            this.unsubscribe(args.getString(0), callbackContext);
            return true;
        }
        if (action.equals(ACTION_EVENTDETAILS)) {
            this.sendOfflineEventDetails(
        	    args.getString(0),
        	    args.getString(1),
        	    args.getString(2),
        	    args.getString(3),
        	    args.getString(4),
        	    args.getString(5),
        	    args.getString(6),
        	    callbackContext);
            return true;
        }
        if (action.equals(ACTION_COUNTEVENTDETAILS)) {
            this.countOfflineEventDetails(callbackContext);
            return true;
        }
        if(action.equals(ACTION_FLUSHEVENTDETAILS)) {
            this.flushOfflineEventDetails(callbackContext);
            return true;
        }
        return false;
    }

    private void initialize(final CallbackContext callbackContext, final JSONArray args) {
	final Context context = cordova.getActivity().getApplicationContext();
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    String appId = args.getString(0);
                    String clientKey = args.getString(1);
                    Parse.enableLocalDatastore(context);
                    Parse.initialize(cordova.getActivity(), appId, clientKey);
                    PushService.setDefaultPushCallback(cordova.getActivity(), cordova.getActivity().getClass());
                    ParseInstallation.getCurrentInstallation().saveInBackground();
                    callbackContext.success();
                } catch (JSONException e) {
                    callbackContext.error("JSONException");
                }
            }
        });
    }

    private void getInstallationId(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                String installationId = ParseInstallation.getCurrentInstallation().getInstallationId();
                callbackContext.success(installationId);
            }
        });
    }

    private void getInstallationObjectId(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                String objectId = ParseInstallation.getCurrentInstallation().getObjectId();
                callbackContext.success(objectId);
            }
        });
    }

    private void getSubscriptions(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                 Set<String> subscriptions = PushService.getSubscriptions(cordova.getActivity());
                 callbackContext.success(subscriptions.toString());
            }
        });
    }

    private void subscribe(final String channel, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                PushService.subscribe(cordova.getActivity(), channel, cordova.getActivity().getClass());
                callbackContext.success();
            }
        });
    }

    private void unsubscribe(final String channel, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                PushService.unsubscribe(cordova.getActivity(), channel);
                callbackContext.success();
            }
        });
    }
    
    private void sendOfflineEventDetails(
	    	final String eventName,
	    	final String userId,
	    	final String gaClientId,
    		final String firstName,
    		final String lastName,
    		final String email,
    		final String phoneNumber,
    		final CallbackContext callbackContext) {
	cordova.getThreadPool().execute(new Runnable() {
            public void run() {
        	final ParseObject oEvent = new ParseObject("OfflineEventRegistration");
        	oEvent.put("eventName", eventName);
        	oEvent.put("userId", userId);
        	oEvent.put("gaClientId", gaClientId);
        	oEvent.put("firstName", firstName);
        	oEvent.put("lastName", lastName);
        	oEvent.put("email", email);
        	oEvent.put("phoneNumber", phoneNumber);
        	oEvent.put("registeredAt", getCurrentDateTime());
        	
        	// put in local storage to track how many of these
        	// things are outstanding
        	oEvent.pinInBackground();
        	
        	SaveCallback saveCallback = new SaveCallback() {
		        public void done(ParseException e) {
		            oEvent.unpinInBackground();
		        }
		    };
        	oEvent.saveEventually(saveCallback);
        	
        	callbackContext.success();
            }
	});
    }
    
    private void countOfflineEventDetails(final CallbackContext callbackContext) {
	cordova.getThreadPool().execute(new Runnable() {
            public void run() {
        	FindCallback<ParseObject> findCallback = new FindCallback<ParseObject>() {
		        public void done(List<ParseObject> objects, ParseException e) {
		            callbackContext.success(objects.size());
		        }
		    };
        	ParseQuery<ParseObject> query = ParseQuery.getQuery("OfflineEventRegistration")
        		    .fromLocalDatastore();
        	query.findInBackground(findCallback);
            }
	});
    }
    
    private void flushOfflineEventDetails(final CallbackContext callbackContext) {
	cordova.getThreadPool().execute(new Runnable() {
            public void run() {
        	ParseQuery<ParseObject> query = ParseQuery.getQuery("OfflineEventRegistration")
        		    .fromLocalDatastore();
        	 try {
        	     List<ParseObject> objects = query.find();
        	     ParseObject.saveAll(objects);
        	     ParseObject.unpinAll(objects);
        	     callbackContext.success();
	         } catch (ParseException e) {
	             callbackContext.error("Save failed: " + e.getMessage());
	         }
            }
	});
    }
	    
    private String getCurrentDateTime() {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
	sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	return sdf.format(new Date());
    }

}

