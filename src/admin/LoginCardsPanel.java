/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package admin;

/**
 *
 * @author ashley
 */

import javax.swing.*;
import java.awt.*;

public class LoginCardsPanel extends JPanel {
    
    public static final String CARD_ADMIN_LOGIN = "ADMIN_LOGIN";
    public static final String CARD_USER_LOGIN = "USER_LOGIN";
    public static final String CARD_CREATE_USER = "CREATE_USER";

    private CardLayout cardLayout;

    public LoginCardsPanel() {
        
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        AdminLoginCard adminLoginCard = new AdminLoginCard(this);
        UserLoginCard userLoginCard = new UserLoginCard(this);
        CreateUserCard createUserCard = new CreateUserCard(this);

        add(adminLoginCard, CARD_ADMIN_LOGIN);
        add(userLoginCard, CARD_USER_LOGIN);
        add(createUserCard, CARD_CREATE_USER);

        showAdminLogin();
    }

    public void showAdminLogin() {
        cardLayout.show(this, CARD_ADMIN_LOGIN);
    }

    public void showUserLogin() {
        cardLayout.show(this, CARD_USER_LOGIN);
    }

    public void showCreateUser() {
        cardLayout.show(this, CARD_CREATE_USER);
    }
    
}
