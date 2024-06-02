package in.sampath.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import in.sampath.entity.StateEntity;

public interface StateRepo extends JpaRepository<StateEntity, Integer> {

	@Query(value = "select *from state where CountryId=cid", nativeQuery = true)
	public List<StateEntity> getstates(Integer cid);

}
