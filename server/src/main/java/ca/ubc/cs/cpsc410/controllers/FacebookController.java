package ca.ubc.cs.cpsc410.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by vincent on 20/10/15.
 * <p>
 * Represents controller to handle all Facebook-related API operations
 */
@Controller
@RequestMapping("/facebook")
public class FacebookController {

    private Facebook facebook;

    @Autowired
    public FacebookController(Facebook facebook) {
        this.facebook = facebook;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getFeed(Model model) {
        try {
            if (!facebook.isAuthorized()) {
                return "redirect:/connect/facebook";
            }
        } catch (NullPointerException npe) {
            return "redirect:/connect/facebook";
        }

        /*model.addAttribute("facebookProfile",
                facebook.userOperations().getUserProfile());
        PagedList<Post> homeFeed = facebook.feedOperations().getHomeFeed();
        model.addAttribute("feed", homeFeed);*/

        model.addAttribute("facebookProfile", facebook.userOperations().getUserProfile());
        model.addAttribute("friends", facebook.friendOperations().getFriendProfiles());
        model.addAttribute("events", facebook.eventOperations().getAttending());

        return "feed";
    }

}
