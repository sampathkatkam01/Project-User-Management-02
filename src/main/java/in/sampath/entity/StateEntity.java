package in.sampath.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "state")
public class StateEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer StateId;
	private String stateName;
	
	@ManyToOne
	@JoinColumn(name="CountryId")
	private CountryEntity country;

	public Integer getStateId() {
		return StateId;
	}

	public void setStateId(Integer stateId) {
		StateId = stateId;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public CountryEntity getCountry() {
		return country;
	}

	public void setCountry(CountryEntity country) {
		this.country = country;
	}
	
	
}
