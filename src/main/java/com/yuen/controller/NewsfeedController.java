package com.yuen.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.yuen.domain.CustomUserDetails;
import com.yuen.domain.Notification;
import com.yuen.domain.Post;
import com.yuen.domain.User;
import com.yuen.service.NotificationService;
import com.yuen.service.PostService;
import com.yuen.util.Const;

@Controller
public class NewsfeedController {

	@Autowired
	private PostService postService;
	
	@Autowired
	private NotificationService notificationService;
	
	@GetMapping("/")
	public String index(Model model) {
		// Get latest posts of current user and him/her followings
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = ((CustomUserDetails) authentication.getPrincipal()).getUser();
		Page<Post> posts = postService.findByUsers(currentUser.getConnection(), 0, Const.POSTS_PER_PAGE);
		
		// Get not read notifications
		List<Notification> notifications = notificationService.findNotRead(currentUser);
		
		if (posts.hasContent()) {
			model.addAttribute("posts", posts);
		}
		model.addAttribute("notifications", notifications);
		return "newsfeed";
	}
	
	@GetMapping("/newsfeed/more")
	public ModelAndView loadMore(@RequestParam int page, Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = ((CustomUserDetails) authentication.getPrincipal()).getUser();
		Page<Post> posts = postService.findByUsers(currentUser.getConnection(), page, Const.POSTS_PER_PAGE);
		
		if (posts.hasContent()) {
			model.addAttribute("posts", posts);
		}
        return new ModelAndView("newsfeed_fragment");
    }
	
}
