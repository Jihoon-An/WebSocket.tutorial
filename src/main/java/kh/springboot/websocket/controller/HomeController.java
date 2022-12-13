package kh.springboot.websocket.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class HomeController {
    @Autowired
    private HttpSession session;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String toHome(HttpServletRequest request, String id) {
        session.setAttribute("IP",request.getRemoteAddr());
        session.setAttribute("loginID", id);
        return "home";
    }
}
