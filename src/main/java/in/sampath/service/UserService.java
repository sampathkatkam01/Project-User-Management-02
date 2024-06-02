package in.sampath.service;

import java.util.Map;

import in.sampath.DTO.LoginDto;
import in.sampath.DTO.RegisterDto;
import in.sampath.DTO.ResetPwdDto;
import in.sampath.DTO.UserDto;

public interface UserService {

	public Map<Integer, String> getCountries();

	public Map<Integer, String> getStates(Integer cid);

	public Map<Integer, String> getCities(Integer sid);

	public UserDto getUser(String email);

	public boolean registerUser(RegisterDto registerdto);

	public UserDto getUserLogin(LoginDto logindto);

	public boolean resetPwd(ResetPwdDto resetpwdDto);

	public String getQuote();

}
