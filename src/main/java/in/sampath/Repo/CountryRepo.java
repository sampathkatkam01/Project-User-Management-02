package in.sampath.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import in.sampath.entity.CountryEntity;

public interface CountryRepo extends JpaRepository<CountryEntity, Integer> {

}
