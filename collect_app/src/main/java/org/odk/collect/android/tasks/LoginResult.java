package org.odk.collect.android.tasks;

public class LoginResult {
    private String Message;
    private boolean Success_Flag;
    private static String UserToken = "";

    public String getMessage(){ return Message; }

    public void setMessage(String Message){
        this.Message = Message;
    }

    public boolean getSuccess_Flag() { return Success_Flag; }

    public void setSuccess_Flag(boolean Success_Flag){
        this.Success_Flag = Success_Flag;
    }

    public static String getUserToken() { return UserToken; }

    public static void setUserToken(String UserToken){
        LoginResult.UserToken = UserToken;
    }
}