package org.odk.collect.android.tasks;

public class LoginResult {
    private static String message = "";
    private static  boolean successFlag;
    private static String userToken = "";

    public static String getMessage(){ return message; }

    public static void setMessage(String Message){
        message = Message;
    }

    public static boolean getSuccess_Flag() { return successFlag; }

    public static void setSuccess_Flag(boolean Success_Flag){
        successFlag = Success_Flag;
    }

    public static String getUserToken() { return userToken; }

    public static void setUserToken(String UserToken){
        userToken = UserToken;
    }
}