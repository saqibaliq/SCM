package com.springboot.smartcontactmanager.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.springboot.smartcontactmanager.dao.UserRepository;
import com.springboot.smartcontactmanager.entities.User;
import com.springboot.smartcontactmanager.helper.Message;

@Controller
public class HomeController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}

	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}

	@GetMapping("/signup")
	public String Signup(Model model) {
		model.addAttribute("title", "Registration - Smart Contact Manager");
		model.addAttribute("user", new User());

		return "signup";
	}

	// this handler is to register user to database
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String Register(@Valid @ModelAttribute("user") User user, BindingResult result1,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {
		try {
			if (!agreement) {
				System.out.println("You have not agreed terms and conditions");
				throw new Exception("You have not agreed terms and conditions");
			}
			if (result1.hasErrors()) {
				System.out.println("Error " + result1.toString());
				model.addAttribute("user", user);
				return "signup";
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
			user.setUserImage("profileuser.png");
			System.out.println("Agreement	" + agreement);
			System.out.println("User   " + user);
			User result = this.userRepository.save(user);
			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Successfully Registered.. ", "alert-success"));
			return "signup";
		} catch (Exception ex) {
			ex.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("emailmessage",
					new Message("Email already registered . Try with Another", "alert-warning"));
			session.setAttribute("message",
					new Message("Something went wrong!! Please Agree terms and Conditions", "alert-danger"));
			return "signup";

		}
	}

	@GetMapping("/signin")
	public String Login(Model model) {
		model.addAttribute("title", "Login Page");
		return "login";
	}
}
