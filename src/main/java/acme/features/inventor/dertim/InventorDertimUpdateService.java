package acme.features.inventor.dertim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.Configuration;
import acme.entities.dertims.Dertim;
import acme.framework.components.models.Model;
import acme.framework.controllers.Errors;
import acme.framework.controllers.Request;
import acme.framework.services.AbstractUpdateService;
import acme.roles.Inventor;
import main.spamDetector;

@Service
public class InventorDertimUpdateService implements AbstractUpdateService<Inventor, Dertim> {
	
	// Internal State 
	
	@Autowired
	protected InventorDertimRepository repository;
		
	//AbstractUpdateService<Inventor, Dertim> interface
	
	@Override
	public boolean authorise(final Request<Dertim> request) {
		assert request != null;
		final java.util.Collection<Dertim> mines = this.repository.findMineDertim(request.getPrincipal().getUsername());
		final Dertim dertim = this.repository.findOneDertim(request.getModel().getInteger("id"));
		
		final boolean result;
		result = request.getPrincipal().hasRole(Inventor.class)&&mines.contains(dertim);
		return result;
	}
	
	@Override
	public void bind(final Request<Dertim> request, final Dertim entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;

		request.bind(entity, errors, "creationMoment", "subject", "summary", "initialPeriod", "endPeriod", "endPeriod", "provision", "additionalInfo");

	}
	
	@Override
	public void unbind(final Request<Dertim> request, final Dertim entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;

		request.unbind(entity, model,"code",  "creationMoment", "subject", "summary", "initialPeriod", "endPeriod", "endPeriod", "provision", "additionalInfo");
		final String item = entity.getItem().getCode();
		model.setAttribute("item", item);
		
	}
	
	@Override
	public Dertim findOne(final Request<Dertim> request) {
		assert request != null;

		Dertim result;
		int id;

		id = request.getModel().getInteger("id");
		result = this.repository.findOneDertim(id);

		return result;
	}
	
	@Override
	public void validate(final Request<Dertim> request, final Dertim entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;
		
		if(!errors.hasErrors("subject")) {
        	final Configuration configuration = this.repository.findConfiguration();
        	final String[] sp = configuration.getWeakSpamTerms().split(",");
        	final List<String> softSpam = new ArrayList<String>(Arrays.asList(sp));
        	final Double softThreshold = configuration.getWeakSpamThreshold();
        	final String[] hp = configuration.getStrongSpamTerms().split(",");
        	final List<String> hardSpam = new ArrayList<String>(Arrays.asList(hp));
        	final Double hardThreshold = configuration.getStrongSpamThreshold();
        	errors.state(request, !spamDetector.isSpam(entity.getSubject(), softSpam, softThreshold, hardSpam, hardThreshold), "subject", "inventor.dertim.form.error.spam");
        }
		
		if(!errors.hasErrors("summary")) {
        	final Configuration configuration = this.repository.findConfiguration();
        	final String[] sp = configuration.getWeakSpamTerms().split(",");
        	final List<String> softSpam = new ArrayList<String>(Arrays.asList(sp));
        	final Double softThreshold = configuration.getWeakSpamThreshold();
        	final String[] hp = configuration.getStrongSpamTerms().split(",");
        	final List<String> hardSpam = new ArrayList<String>(Arrays.asList(hp));
        	final Double hardThreshold = configuration.getStrongSpamThreshold();
        	errors.state(request, !spamDetector.isSpam(entity.getSummary(), softSpam, softThreshold, hardSpam, hardThreshold), "summary", "inventor.dertim.form.error.spam");
        }
		
		
		if(!errors.hasErrors("initialPeriod")) {
        	final Date startPeriod = entity.getInitialPeriod();
        	final Calendar calendar = Calendar.getInstance();
        	calendar.setTime(entity.getCreationMoment()); // Aquí no tendremos en cuenta la fecha de actualización, sino de creación
        	calendar.add(Calendar.MONTH, 1);
        	calendar.add(Calendar.SECOND, -1); // Un mes menos un segundo
        	errors.state(request, startPeriod.after(calendar.getTime()), "initialPeriod", "inventor.dertim.form.error.start-period-not-enough");
        }
		
		if(!errors.hasErrors("endPeriod") && entity.getInitialPeriod()!=null) {
        	final Date startPeriod = entity.getInitialPeriod();
        	final Date endPeriod = entity.getEndPeriod();
        	final Date moment = new Date(startPeriod.getTime() + 604799999); 
        	errors.state(request, endPeriod.after(moment), "endPeriod", "inventor.dertim.form.error.end-period-one-week-before-start-period");
        }
		
		if(!errors.hasErrors("provision")) {
            final String acceptedCurrencies = this.repository.findConfiguration().getAcceptedCurrencies();
            final String[] currencies = acceptedCurrencies.split(",");
            boolean isCorrect = false;
            final String c = entity.getProvision().getCurrency();
            for (final String currency : currencies) {
                if (c.equals(currency)) {
                    isCorrect = true;
                }
            }
            errors.state(request, isCorrect, "provision", "inventor.dertim.form.error.incorrect-currency");
        }
        
        if(!errors.hasErrors("provision")) {
            errors.state(request, entity.getProvision().getAmount() >= 0.0, "provision", "inventor.dertim.form.error.negative-budget");
        }
		
	}
		
	@Override
	public void update(final Request<Dertim> request, final Dertim entity) {
		assert request != null;
		assert entity != null;

		this.repository.save(entity);
	}

}
