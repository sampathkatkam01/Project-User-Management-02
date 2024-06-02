package in.sampath.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.sampath.DTO.LoginDto;
import in.sampath.DTO.QuoteDto;
import in.sampath.DTO.RegisterDto;
import in.sampath.DTO.ResetPwdDto;
import in.sampath.DTO.UserDto;
import in.sampath.Repo.CityRepo;
import in.sampath.Repo.CountryRepo;
import in.sampath.Repo.StateRepo;
import in.sampath.Repo.UserDetailsRepo;
import in.sampath.Utils.EmailUtils;
import in.sampath.entity.CityEntity;
import in.sampath.entity.CountryEntity;
import in.sampath.entity.StateEntity;
import in.sampath.entity.UserDetailsEntity;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDetailsRepo userrepo;
	@Autowired
	private CountryRepo countryrepo;
	@Autowired
	private StateRepo staterepo;
	@Autowired
	private CityRepo cityrepo;
	@Autowired
	private EmailUtils emailutils;

	private QuoteDto[] quotations = null;

	Random r = new Random();

	// Mapping countries from database
	@Override
	public Map<Integer, String> getCountries() {

		Map<Integer, String> countryMap = new HashMap<>();

		List<CountryEntity> Countrylist = countryrepo.findAll();

		Countrylist.forEach(c -> {
			countryMap.put(c.getCountryId(), c.getCountryName());
		});

		return countryMap;
	}

	// Mapping states from database
	@Override
	public Map<Integer, String> getStates(Integer cid) {

		Map<Integer, String> statesmap = new HashMap<>();

		
		  CountryEntity country = new CountryEntity(); country.setCountryId(cid);
		  
		  StateEntity state = new StateEntity(); state.setCountry(country);
		  
		  Example<StateEntity> of = Example.of(state); 
		  List<StateEntity> stateList=staterepo.findAll(of);
		 

//		List<StateEntity> stateList = staterepo.getstates(cid);

		stateList.forEach(s -> {
			statesmap.put(s.getStateId(), s.getStateName());
		});

		return statesmap;
	}

	// Mapping city from database
	@Override
	public Map<Integer, String> getCities(Integer sid) {

		Map<Integer, String> cityMap = new HashMap<>();

		StateEntity state = new StateEntity();
		state.setStateId(sid);

		CityEntity city = new CityEntity();
		city.setState(state);

		Example<CityEntity> of = Example.of(city);
		List<CityEntity> cityList = cityrepo.findAll(of);

		cityList.forEach(c -> {
			cityMap.put(c.getCityId(), c.getCityName());
		});
		return cityMap;
	}

	@Override
	public UserDto getUser(String email) {

		UserDetailsEntity userentity = userrepo.findByEmail(email);

		/*
		 * UserDto dto =new UserDto(); BeanUtils.copyProperties(userentity, dto);
		 */

		if (userentity == null) {
			return null;
		}
		ModelMapper mapper = new ModelMapper();
		UserDto dto = mapper.map(userentity, UserDto.class);
		return dto;
	}

	// regestration
	@Override
	public boolean registerUser(RegisterDto registerdto) {

		ModelMapper mapper = new ModelMapper();
		UserDetailsEntity entity = mapper.map(registerdto, UserDetailsEntity.class);

		// Assoicaciton maping for UserEntity
		CountryEntity country = countryrepo.findById(registerdto.getCountryid()).orElseThrow();
		StateEntity state = staterepo.findById(registerdto.getStateid()).orElseThrow();
		CityEntity city = cityrepo.findById(registerdto.getCityid()).orElseThrow();

		entity.setCountry(country);
		entity.setState(state);
		entity.setCity(city);

		entity.setPassword(generateRandom());
		entity.setUpdatedpwd("No");

		// email sending
		String subject = "User Registration Password";
		String body = "Your Random Password is : " + entity.getPassword();
		emailutils.sendEmail(registerdto.getEmail(), subject, body);
		
		// user registration here
				UserDetailsEntity save = userrepo.save(entity);

				System.out.println("Registration successfull : "+entity.getName());
		return save.getUserId() != null;
	}

	// login
	@Override
	public UserDto getUserLogin(LoginDto logindto) {

		UserDetailsEntity userentity = userrepo.findByEmailAndPassword(logindto.getEmail(), logindto.getPassword());

		if (userentity == null) {
			return null;
		}

		ModelMapper mapper = new ModelMapper();
		return mapper.map(userentity, UserDto.class);

	}

	// Reset password method to reset the password
	@Override
	public boolean resetPwd(ResetPwdDto resetpwdDto) {

		UserDetailsEntity userentity = userrepo.findByEmailAndPassword(resetpwdDto.getEmail(), resetpwdDto.getOldPwd());
		if (userentity != null) {
			userentity.setPassword(resetpwdDto.getNewpwd());
			userentity.setUpdatedpwd("Yes");
			userrepo.save(userentity);
			System.out.println("Password updated for user: " + resetpwdDto.getEmail());  // Logging statement
			return true;
		}
		 System.out.println("Password update failed for user: " + resetpwdDto.getEmail());  // Logging statement
		return false;
	}

	@Override
	public String getQuote() {

		if (quotations == null) {
			String url = "https://type.fit/api/quotes";

			// web service call

			RestTemplate rt = new RestTemplate();
			ResponseEntity<String> entity = rt.getForEntity(url, String.class);
			String body = entity.getBody();

			ObjectMapper mapper = new ObjectMapper();
			try {
				quotations = mapper.readValue(body, QuoteDto[].class);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}

		int index = r.nextInt(quotations.length);

		return quotations[index].getText();
	}

	// to generate random text as password
	private String generateRandom() {

		String aToZ = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789abcdefghijklmnopqrstuvwxyz";

		StringBuilder res = new StringBuilder();
		for (int i = 0; i < 8; i++) {
			int randIndex = r.nextInt(aToZ.length());
			res.append(aToZ.charAt(randIndex));
		}
		return res.toString();
	}

}
