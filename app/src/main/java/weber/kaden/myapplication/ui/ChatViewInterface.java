package weber.kaden.myapplication.ui;

import java.util.List;

import weber.kaden.common.model.ChatMessage;

public interface ChatViewInterface {
    void sendChat(String message);
    void printChatError(String message);
    void updateChat(List<ChatMessage> messages);
}