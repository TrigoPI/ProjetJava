import static org.junit.jupiter.api.Assertions.assertEquals;
import ClavarChat.Controllers.API.DataBaseAPI;
import ClavarChat.Controllers.Managers.User.UserManager;
import Resources.Resources;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class TestBDD {

    private UserManager uManager = new UserManager();
    private DataBaseAPI api = new DataBaseAPI(uManager);

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
}
