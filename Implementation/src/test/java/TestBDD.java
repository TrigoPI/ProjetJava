import static org.junit.jupiter.api.Assertions.assertEquals;
import ClavarChat.Controllers.API.DataBaseAPI;
import ClavarChat.Controllers.Managers.User.UserManager;
import Resources.Resources;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class TestBDD {
    private UserManager uManager = new UserManager();
    private DataBaseAPI api = new DataBaseAPI(uManager);

    @Test
    @DisplayName("Test pour verifier l'insertion d'un user et la modification de son pseudo")
    void testUserPseudonym() throws IOException {
        this.api.clear(); //clear la BDD pour qu'il n'y ait pas de résidu
        //test insert user
        String user1="USER1";
        String user2="USER2";
        String user3="USER3";
        int userid1 = 1111;
        int userid2 = 2222;
        int userid3 = 3333;
        byte[] buffer = Resources.IMG.USER_1.readAllBytes();
        this.api.addUser(userid1,user1,buffer);
        this.api.addUser(userid2,user1,buffer);
        this.api.addUser(userid3,user1,buffer);

        assertEquals(user1,this.api.getUserPseudo(userid1));
        assertEquals(user2,this.api.getUserPseudo(userid2));
        assertEquals(user3,this.api.getUserPseudo(userid3));

        //test update user
        user1="UpdatedUSER1";
        user2="UpdatedUSER2";
        user3="UpdatedUSER3";
        this.api.updatePseudo(userid1,user1);
        this.api.updatePseudo(userid2,user2);
        this.api.updatePseudo(userid3,user3);

        assertEquals(user1,this.api.getUserPseudo(userid1));
        assertEquals(user2,this.api.getUserPseudo(userid2));
        assertEquals(user3,this.api.getUserPseudo(userid3));

        this.api.clear();
    }
    @Test
    @DisplayName("Test pour verifier l'insertion d'utilisateur dans la BDD et l'update d'avatar")
    void testUserAvatar() throws IOException {
        this.api.clear();
        String user1="USER1";
        int userid1 = 1111;
        byte[] buffer = Resources.IMG.USER_1.readAllBytes();
        this.api.addUser(userid1,user1,buffer);
        //verifie si la longueur est identique pour ne pas avoir de segmentation fault
        if (buffer.length == this.api.getUserAvatar(userid1).length) {
            for (int i=0; i<buffer.length;i++){
                //test chaque byte de l'image et la compare avec celle d'origine
                assertEquals(buffer[i],this.api.getUserAvatar(userid1)[i]);
            }
        }
        buffer=Resources.IMG.LOGO.readAllBytes();
        this.api.updateAvatar(userid1,buffer);
        //verifie si la longueur est identique pour ne pas avoir de segmentation fault
        if (buffer.length == this.api.getUserAvatar(userid1).length) {
            for (int i = 0; i < buffer.length; i++) {
                //test chaque byte de l'image et la compare avec celle d'origine
                assertEquals(buffer[i], this.api.getUserAvatar(userid1)[i]);
            }
        }
        this.api.clear();
    }

    @Test
    @DisplayName("test sur l'ajout de message à la BDD")
    void testMessage() {
        String message = "testMESSAGE";
        int convID=1;
        int senderID = 1111;
        int receiverID=2222;
        this.api.addMessage(convID,senderID,receiverID,message);
        assertEquals(message,this.api.getMessageText(this.api.getMessagesId(convID).get(0)));
        //pas besoin de tester l'id vu qu'il est créé par autoincrémantation
        //pas d'update à tester
    }
}
