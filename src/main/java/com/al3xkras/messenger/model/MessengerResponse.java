package com.al3xkras.messenger.model;

import java.util.ResourceBundle;

public final class MessengerResponse {
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("response-messages");
    public enum Messages {
        WARNING_SECURITY_DISABLED(resourceBundle.getString("warn.security.disabled")),
        EXCEPTION_MESSENGER_USER_NOT_FOUND(resourceBundle.getString("exception.messenger-user.not-found")),
        EXCEPTION_MESSENGER_USER_USERNAME_EXISTS(resourceBundle.getString("exception.messenger-user.username.exists")),
        EXCEPTION_MESSENGER_USER_PERSIST_INVALID_USER_TYPE(resourceBundle.getString("exception.messenger-user.persist.invalid-user-type")),
        EXCEPTION_REQUEST_USER_ID_AND_USERNAME_IS_EMPTY(resourceBundle.getString("exception.request.username-and-userid.is-empty")),
        EXCEPTION_MESSENGER_USER_DELETE_INVALID_USER_TYPE(resourceBundle.getString("exception.messenger-user.delete.invalid-user-type")),
        EXCEPTION_AUTHORIZE(resourceBundle.getString("exception.authorize")),
        EXCEPTION_PARAMETER_NOT_SPECIFIED_PATTERN(resourceBundle.getString("exception.parameter.not-specified")),
        EXCEPTION_FORBIDDEN_PATTERN(resourceBundle.getString("exception.forbidden")),
        EXCEPTION_MESSENGER_USER_ID_IS_INVALID(resourceBundle.getString("exception.messenger-user.id.invalid")),
        EXCEPTION_ARGUMENT_ISNULL(resourceBundle.getString("exception.argument.is-null"));
        private final String value;
        public String value() {
            return value;
        }
        Messages(String value) {
            this.value = value;
        }
    }

}
