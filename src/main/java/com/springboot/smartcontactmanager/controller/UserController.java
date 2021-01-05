package com.springboot.smartcontactmanager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.springboot.smartcontactmanager.dao.ContactRepository;
import com.springboot.smartcontactmanager.dao.UserRepository;
import com.springboot.smartcontactmanager.entities.Contact;
import com.springboot.smartcontactmanager.entities.User;
import com.springboot.smartcontactmanager.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@ModelAttribute
	public void addCommanData(Model model, Principal principal) {
		String Username = principal.getName();
		User user = userRepository.getUserByUserName(Username);
		model.addAttribute("user", user);
		System.out.println(user);
	}

	@RequestMapping(value = { "/index" })
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}

	/* Open Add Contact Form */
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	@PostMapping("/process-contact")
	public String AddContact(@ModelAttribute Contact contact, @RequestParam("c_image") MultipartFile file, Model model,
			Principal principal, HttpSession httpSession) {
		try {
			model.addAttribute("title", "Add Contact");

			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);
			if (file.isEmpty()) {
				contact.setImage("default.png");
				System.out.println("Please Upload Picture");
			} else {
				// concat new Date just bcs two image should not get same name and then
				// conflicts
				String originalFilename = file.getOriginalFilename();/* .concat(new Date().toString()); */
				contact.setImage(originalFilename);
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image Uploaded");
			}
			contact.setUser(user);
			user.getContacts().add(contact);
			this.userRepository.save(user);
			System.out.println("Contact Added..");
			httpSession.setAttribute("message", new Message("Your Contact is Added Successfully..", "success"));
			System.out.println("Contact " + contact);
		} catch (Exception ex) {
			ex.printStackTrace();
			httpSession.setAttribute("message", new Message("Something went wrong ... ", "danger"));
		}
		return "normal/add_contact_form";
	}

	@GetMapping("/show_contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
		model.addAttribute("title", "View Contacts");
		String name = principal.getName();
		User user = this.userRepository.getUserByUserName(name);
		Pageable pageable = PageRequest.of(page, 2);
		Page<Contact> contacts = this.contactRepository.findContactByUser(user.getUserId(), pageable);
		System.out.println("Contacts contacts " + contacts);

		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		return "normal/show_conacts";
	}

	@GetMapping("/{cId}/contact")
	public String showParticularContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {

		Optional<Contact> findById = this.contactRepository.findById(cId);
		Contact contact = findById.get();
		String name = principal.getName();
		User user = this.userRepository.getUserByUserName(name);
		if (user.getUserId() == contact.getUser().getUserId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getcName());
		}
		System.out.println("Particular Contact Detail " + contact);
		return "normal/contact_detail";
	}

	// Delete Handler
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId, HttpSession httpSession, MultipartFile file,
			Principal principal) {
		try {
			Optional<Contact> contactOptional = this.contactRepository.findById(cId);
			Contact contact = contactOptional.get();
			User user = this.userRepository.getUserByUserName(principal.getName());
			user.getContacts().remove(contact);
			this.userRepository.save(user);
			/*
			 * contact.setUser(null); this.contactRepository.delete(contact);
			 */
			File saveFile = new ClassPathResource("static/img/" + contact.getImage()).getFile();
			boolean delete = saveFile.delete();
			if (delete == true) {
				System.out.println("Deleted Success");
			} else {
				System.out.println("Not Deleted");
			}
			// Path path = Paths.get(saveFile.getAbsolutePath() + File.separator +
			// file.getOriginalFilename());
			System.out.println("Save File " + saveFile);
			httpSession.setAttribute("message", new Message("Contact Deleted Successfully..", "success"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "redirect:/user/show_contacts/0";

	}

	@PostMapping("/update-form/{cid}")
	public String openUpdateForm(@PathVariable("cid") Integer cid, Model model) {
		model.addAttribute("title", "Update");
		Optional<Contact> contactOptional = this.contactRepository.findById(cid);
		Contact contact = contactOptional.get();
		model.addAttribute("contact", contact);
		return "normal/update_form";
	}

	@PostMapping("/process-updatecontact")
	public String Update_Controller(@ModelAttribute Contact contact, @RequestParam("c_image") MultipartFile file,
			Model model, HttpSession httpSession, Principal principal) {
		try {
			// Old Contact
			Contact contact2 = this.contactRepository.findById(contact.getcId()).get();
			if (!file.isEmpty()) {
				// rewrite image file
				// upload new photo and delete old photo
				File old_profile = new ClassPathResource("static/img").getFile();
				File f1 = new File(old_profile, contact2.getImage());
				f1.delete();

				File Save = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(Save.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
			} else {
				contact.setImage(contact2.getImage());
			}
			User user = this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			// contact.setImage("default.png");
			this.contactRepository.save(contact);
			httpSession.setAttribute("message", new Message("Contact Updated Successfully..", "success"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "redirect:/user/" + contact.getcId() + "/contact";
	}

	@GetMapping("/show_profile")
	public String Show_Profile(Model model) {
		model.addAttribute("title", "User Profile");
		return "normal/show_profile";
	}

	/* open settings tab */
	@GetMapping("/settings")
	public String openSettings(Model model) {
		model.addAttribute("title", "Settings");
		return "normal/setting";
	}

	@PostMapping("/change-password")
	public String ChangePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, Principal principal, HttpSession httpSession) {
		System.out.println("Old Pass " + oldPassword);
		System.out.println("New Pass " + newPassword);
		User CurrentUser = this.userRepository.getUserByUserName(principal.getName());

		if (this.bCryptPasswordEncoder.matches(oldPassword, CurrentUser.getUserPassword())) {
			CurrentUser.setUserPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(CurrentUser);
			httpSession.setAttribute("message", new Message("Your Password is Successfully Chnaged..", "success"));
		} else {
			httpSession.setAttribute("message", new Message("Your Enter InCorrect Old Password..", "danger"));
			return "redirect:/user/settings";
		}
		return "redirect:/logout";

	}
}
