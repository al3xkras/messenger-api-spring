package com.al3xkras.messenger.model;

import java.util.ResourceBundle;

public final class MessengerUtils {
    private static final ResourceBundle messages = ResourceBundle.getBundle("response-messages");
    private static final ResourceBundle messenger = ResourceBundle.getBundle("messenger");
    public enum Messages {
        WARNING_SECURITY_DISABLED(messages.getString("warn.security.disabled")),
        EXCEPTION_MESSENGER_USER_NOT_FOUND(messages.getString("exception.messenger-user.not-found")),
        EXCEPTION_MESSENGER_USER_USERNAME_EXISTS(messages.getString("exception.messenger-user.username.exists")),
        EXCEPTION_MESSENGER_USER_PERSIST_INVALID_USER_TYPE(messages.getString("exception.messenger-user.persist.invalid-user-type")),
        EXCEPTION_MESSENGER_USER_DELETE_INVALID_USER_TYPE(messages.getString("exception.messenger-user.delete.invalid-user-type")),
        EXCEPTION_AUTHORIZE(messages.getString("exception.authorize")),
        EXCEPTION_PARAMETER_NOT_SPECIFIED_PATTERN(messages.getString("exception.parameter.not-specified")),
        EXCEPTION_FORBIDDEN_PATTERN(messages.getString("exception.forbidden")),
        EXCEPTION_MESSENGER_USER_ID_IS_INVALID(messages.getString("exception.messenger-user.id.invalid")),
        EXCEPTION_ARGUMENT_ISNULL(messages.getString("exception.argument.is-null")),
        EXCEPTION_USER_SERVICE_UNREACHABLE(messages.getString("exception.user-service.unreachable")),
        EXCEPTION_USER_SERVICE_INTERNAL_ERROR(messages.getString("exception.user-service.internal-error")),
        EXCEPTION_CHAT_NAME_IS_INVALID(messages.getString("exception.chat-name.is-invalid")),
        EXCEPTION_CHAT_OWNER_ID_IS_INVALID(messages.getString("exception.chat.owner-id.is-invalid")),
        EXCEPTION_CHAT_MODIFICATION_FORBIDDEN(messages.getString("exception.chat.modification.is-forbidden")),
        EXCEPTION_CHAT_ADD_USER_IS_FORBIDDEN(messages.getString("exception.chat.add-chat-user.is-forbidden")),
        EXCEPTION_CHAT_DELETE_USER_IS_FORBIDDEN(messages.getString("exception.chat.delete-chat-user.is-forbidden")),
        EXCEPTION_CHAT_USER_NOT_FOUND(messages.getString("exception.chat-user.not-found")),
        EXCEPTION_CHAT_USER_ROLE_MODIFIED(messages.getString("exception.chat-user.role.modified")),
        WARNING_FILTER_IGNORED_FOR_REQUEST(messages.getString("warn.filter.ignored-for-request")),
        EXCEPTION_CHAT_NOT_FOUND(messages.getString("exception.chat.not-found")),
        EXCEPTION_AUTH_TOKEN_IS_NULL(messages.getString("exception.auth-token.is-null")),
        EXCEPTION_AUTH_TOKEN_IS_INVALID(messages.getString("exception.auth-token.is-invalid")),
        EXCEPTION_ID_IS_INVALID(messages.getString("exception.id.is-invalid")),
        EXCEPTION_REQUIRED_PARAMETERS_ARE_NULL(messages.getString("request.required-parameters.are-null")),
        EXCEPTION_MESSAGE_SERVICE_READ_FORBIDDEN(messages.getString("exception.message-service.messages.read-forbidden")),
        EXCEPTION_MESSAGE_SERVICE_SEND_FORBIDDEN(messages.getString("exception.message-service.messages.send-forbidden")),
        EXCEPTION_MESSAGE_SERVICE_MESSAGE_MODIFICATION_FORBIDDEN(messages.getString("exception.message-service.message.modification-forbidden")),
        EXCEPTION_MESSAGE_SERVICE_MESSAGE_DELETE_FORBIDDEN(messages.getString("exception.message-service.message.delete-forbidden")),
        WARN_TOKEN_ACCESS(messages.getString("warn.token.access"));
        private final String value;
        public String value() {
            return value;
        }
        Messages(String value) {
            this.value = value;
        }
    }

    public enum Property {
        USER_SERVICE_NAME(messenger.getString("user-service.name")),
        CHAT_SERVICE_NAME(messenger.getString("chat-service.name")),
        MESSAGE_SERVICE_NAME(messenger.getString("message-service.name")),
        USER_SERVICE_URI("http://"+messenger.getString("messenger.host")+":"+messenger.getString("user-service.port")+messenger.getString("user-service.uri.prefix")),
        CHAT_SERVICE_URI("http://"+messenger.getString("messenger.host")+":"+messenger.getString("chat-service.port")+messenger.getString("chat-service.uri.prefix")),
        MESSAGE_SERVICE_URI("http://"+messenger.getString("messenger.host")+":"+messenger.getString("message-service.port")+messenger.getString("message-service.uri.prefix")),
        ENTITY_CHAT_USER(messenger.getString("entity.chat-user")),
        CHAT_USER_DEFAULT_TITLE(messenger.getString("entity.chat-user.user.default-title")),
        CHAT_ADMIN_DEFAULT_TITLE(messenger.getString("entity.chat-user.admin.default-title"));
        private final String value;
        public String value() {
            return value;
        }
        Property(String value) {
            this.value = value;
        }
    }

}
