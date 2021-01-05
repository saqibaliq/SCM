package com.springboot.smartcontactmanager.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.springboot.smartcontactmanager.dao.UserRepository;
import com.springboot.smartcontactmanager.entities.User;
import com.springboot.smartcontactmanager.service.EmailService;

@Controller
public class ForgetController {
	@Autowired
	private EmailService emailService;
	@Autowired
	private UserRepository userrepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	Random random = new Random(10000);
	private int otp;

	@RequestMapping("/forget")
	public String openEmailForm(Model model) {
		model.addAttribute("title", "Forget Password");
		return "forget_email_form";
	}

	@RequestMapping(value = "/send-otp", method = RequestMethod.POST)
	public String SendOtp(@RequestParam("email") String email, Model model, HttpSession httpSession) {
		model.addAttribute("title", "Forget Password");
		System.out.println("Email" + email);
		otp = random.nextInt(99999);
		System.out.println("OTP " + otp);
		String subject = "OTP FROM SCM";
		String message = "" + "<div style='border:1px solid #e2e2e2; padding:20px'>" + "<h1>" + "OTP is" + "<b> " + otp
				+ "</h1>" + "</div>";
		boolean flag = this.emailService.sendEmail(subject, message, email);
		if (flag) {
			httpSession.setAttribute("myotp", otp);
			httpSession.setAttribute("email", email);
			return "verify_otp";
		} else {
			httpSession.setAttribute("message", "Check and Correct Your Email..");
			return "forget_email_form";
		}

	}

	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp, HttpSession httpSession) {
		int myOtp = (int) httpSession.getAttribute("myotp");
		String email = (String) httpSession.getAttribute("email");
		// System.out.println("UserEmail " + email);
		if (myOtp == otp) {
			User user = this.userrepository.getUserByUserName(email);
			if (user == null) {
				httpSession.setAttribute("message", "User doesn't exists with this email!!");
				return "forget_email_form";
			} else {

			}
			return "password_change_form";
		} else {
			httpSession.setAttribute("message", "You have entered wrong otp..");
			return "verify_otp";
		}
	}

	@PostMapping("/change-password")
	public String ChangePassword(@RequestParam("change_pass") String newpassword, HttpSession httpSession) {
		String email = (String) httpSession.getAttribute("email");
		User user = this.userrepository.getUserByUserName(email);
		user.setUserPassword(this.bCryptPasswordEncoder.encode(newpassword));
		this.userrepository.save(user);
		return "redirect:/signin?change=password changed successfully..";
	}
}
