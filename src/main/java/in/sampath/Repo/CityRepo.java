package in.sampath.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import in.sampath.entity.CityEntity;

public interface CityRepo extends JpaRepository<CityEntity, Integer> {

	@Query(
			value = "select *from city where StateId=sid", 
			nativeQuery = true
			)
	public List<CityEntity> getcities(Integer sid);

}
