package acme.entities.dertims;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import acme.entities.items.Item;
import acme.framework.datatypes.Money;
import acme.framework.entities.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Dertim extends AbstractEntity{
	
	//Serialisation identifier
	
	protected static final long serialVersionUID = 1L;
	
	//Attributes  
	
	@NotBlank
	@Column(unique = true) 
	@Pattern(regexp = "^^\\d{2}(0?[1-9]|1[012])(0?[1-9]|[12][0-9]|3[01])#\\w{3}$", message = "The code must be the current date (yymmdd) followed by a \"#\" and 3 words characters")
	//“^yymmdd#\w{3}$
	protected String code; //Code
	
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	@Past
	protected Date creationMoment; // Creation Moment
	
	@NotBlank
	@Length(min = 1, max=100)
	protected String subject; // Title
	
	@NotBlank
	@Length(min = 1, max=255)
	protected String summary; // Description
	
	@NotNull
	protected Date initialPeriod; //Start of the period
	
	@NotNull
	protected Date endPeriod; // End of the period (at least one month ahead and one week long)
	
	@Valid
	@NotNull
	protected Money provision; // Budget positive
	
	@URL
	protected String additionalInfo; // Optional Link
	
	@OneToOne(optional = true)
    @Valid
    @NotNull
    protected Item item; // Sera component o tool
 
}
