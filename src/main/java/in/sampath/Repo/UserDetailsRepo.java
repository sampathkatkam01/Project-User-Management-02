package in.sampath.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import in.sampath.entity.UserDetailsEntity;

public interface UserDetailsRepo extends JpaRepository<UserDetailsEntity, Integer> {

	public UserDetailsEntity findByEmail(String email);
	
	public UserDetailsEntity findByEmailAndPassword(String email ,String password);

}
