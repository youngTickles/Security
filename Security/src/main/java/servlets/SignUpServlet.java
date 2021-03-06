package servlets;

import com.google.appengine.repackaged.com.google.api.client.http.HttpStatusCodes;
import com.google.gson.Gson;
import sec.helper.DbConnect;
import sec.model.Password;
import sec.model.Token;
import sec.model.User;
import sec.model.UserAuthentication;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Base64;


@WebServlet(name = "signup", urlPatterns = "/signup")
public class SignUpServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String email = req.getParameter("email");
        String name = req.getParameter("name");
        String lastName = req.getParameter("last_name");
        String password = req.getParameter("password");

        Gson gson = new Gson();
        String json = "";
        if (UserAuthentication.newAccountAuthorization(email)==true)
        {


             User user = new User(name, lastName, email, password.toCharArray(), Password.getNextSalt());

             User.addNewUserToDataBase(user);

             Token token = new Token(email, Token.nextToken());

             token.addTokenToDataBase();
             json = gson.toJson(token);
             out.print( json);
        }
        else
        out.print(gson.toJson("Error: "+HttpStatusCodes.STATUS_CODE_UNAUTHORIZED));
    }

}
