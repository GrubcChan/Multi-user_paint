package ru.suai.network.message;

/**
 * Transfer Protocol Client-Server Interaction
 * <p>
 * 1. REQUEST_USERNAME:     request username;
 * 2. USERNAME_ACCEPTED:    username accepted;
 * 3. USERNAME_USED:        the username is already in use;
 * 4. CREATE_BOARD:         create a new board;
 * 5. CONNECT_BOARD:        connect to the board;
 * 6. BOARD_NAME_LIST:      list of boards;
 * 7. PAINTING_PEN:         drawing (with a pencil);
 * 8. PAINTING_BRUSH:       drawing (with a brush);
 * 9. PAINTING_ERASER:      drawing (with an eraser);
 * 10. PAINTING_TEXT:       drawing (text);
 * 11. PAINTING_LINE:       drawing (line);
 * 12. PAINTING_OVAL:       drawing (circle);
 * 13. PAINTING_RECT:       drawing (rectangle);
 * 14. PAINTING_REPAINT:    Rework (update);
 * 15. SAVE_IMAGE:          user save message.
 */

public enum MessageType {
    REQUEST_USERNAME,
    USERNAME_ACCEPTED,
    USERNAME_USED,
    CREATE_BOARD,
    CONNECT_BOARD,
    BOARD_NAME_LIST,
    PAINTING_PEN,
    PAINTING_BRUSH,
    PAINTING_ERASER,
    PAINTING_REPAINT,
    PAINTING_TEXT,
    PAINTING_LINE,
    PAINTING_OVAL,
    PAINTING_RECT,
    SAVE_IMAGE
}
