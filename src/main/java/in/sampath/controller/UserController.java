package in.sampath.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import in.sampath.DTO.LoginDto;
import in.sampath.DTO.RegisterDto;
import in.sampath.DTO.ResetPwdDto;
import in.sampath.DTO.UserDto;
import in.sampath.Utils.AppProperties;
import in.sampath.Utils.Appconstants;
import in.sampath.service.UserService;

@Controller
public class UserController {
	@Autowired
	private UserService userservice;
	
	@Autowired
	private AppProperties props;

	@GetMapping("/register")
	public String registerPage(Model model) {

		model.addAttribute("registerDto", new RegisterDto());
		Map<Integer, String> countriesmap = userservice.getCountries();
		model.addAttribute("countries", countriesmap);
		return Appconstants.REGISTER_VIEW;
	}

	@GetMapping("/states/{cid}")
	@ResponseBody
	public Map<Integer, String> getStates(@PathVariable("cid") Integer cid) {

		if (cid == null || cid <= 0) {
			throw new IllegalArgumentException("Invalid country ID");
		}
		return userservice.getStates(cid);
	}

	// getmaping to cities
	@GetMapping("/cities/{sid}")
	@ResponseBody
	public Map<Integer, String> getCities(@PathVariable("sid") Integer sid) {

		if (sid == null || sid <= 0) {
			throw new IllegalArgumentException("Invalid state ID");
		}
		return userservice.getCities(sid);
	}

	@PostMapping("/register")
	public String register(RegisterDto regDto, Model model) {
		Map<String, String> messages = props.getMessages();

		UserDto user = userservice.getUser(regDto.getEmail());

		if (user != null) {
			model.addAttribute(Appconstants.ERROR_MSG, messages.get("dupEmail"));
			return Appconstants.REGISTER_VIEW;
		}

		boolean registerUser = userservice.registerUser(regDto);
		if (registerUser) {
			model.addAttribute(Appconstants.SUCCESS_MSG, regDto.getName() + ",Registred Succeddfully");
		} else {
			model.addAttribute(Appconstants.ERROR_MSG, "User Registration Faild...");
		}

		return Appconstants.REGISTER_VIEW;
	}

	@GetMapping("/")
	public String loginPage(Model model) {
	
		model.addAttribute("loginDto", new LoginDto());
		return Appconstants.INDEX_VIEW;
	}

	@PostMapping("/login")
	public String login(LoginDto loginDto, Model model) {
		Map<String, String> messages = props.getMessages();
		UserDto userLogin = userservice.getUserLogin(loginDto);
		if (userLogin == null) {
			model.addAttribute(Appconstants.ERROR_MSG, messages.get("invalidcredential"));
			return Appconstants.INDEX_VIEW;
		}

		if ("Yes".equalsIgnoreCase(userLogin.getUpdatedpwd())) {
			// If password already updated go to dashboard page
			return "redirect:dashboard";
		} else {
			// If password not updated go to resetpassword page
			ResetPwdDto resetPwdDto = new ResetPwdDto();
			resetPwdDto.setEmail(userLogin.getEmail());
			model.addAttribute("resetPwdDto", resetPwdDto);
			return Appconstants.RESET_PWD_VIEW;
		}
	}

	@PostMapping("/resetpwd")
	public String resetPwd(ResetPwdDto pwdDto, Model model) {

		if (!(pwdDto.getNewpwd().equals(pwdDto.getConfirmpwd()))) {
			model.addAttribute(Appconstants.ERROR_MSG, "New Password and Confirm Password should match");
			return Appconstants.RESET_PWD_VIEW;
		}
		UserDto user = userservice.getUser(pwdDto.getEmail());

		if (user.getPassword().equals(pwdDto.getOldPwd())) {
			boolean resetPwd = userservice.resetPwd(pwdDto);
			if (resetPwd) {
				return "redirect:dashboard";
			} else {
				model.addAttribute(Appconstants.ERROR_MSG, "Password Updataiton Failed");
				return Appconstants.RESET_PWD_VIEW;
			}
		} else {
			model.addAttribute("emsg", "Provided Wrong Old Password");
			return Appconstants.RESET_PWD_VIEW;
		}
	}

	@GetMapping("/dashboard")
	public String dashboard(Model model) {
		String quotes = userservice.getQuote();
		model.addAttribute("quote", quotes);
		return Appconstants.DASHBOARD_VIEW;
	}

	@GetMapping("/logout")
	public String logout() {
		return "redirect:/";
	}

}
