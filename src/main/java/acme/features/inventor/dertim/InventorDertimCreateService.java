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
import acme.entities.items.Item;
import acme.framework.components.models.Model;
import acme.framework.controllers.Errors;
import acme.framework.controllers.Request;
import acme.framework.services.AbstractCreateService;
import acme.roles.Inventor;
import main.spamDetector;

@Service
public class InventorDertimCreateService implements AbstractCreateService<Inventor, Dertim>{
	
	@Autowired
	protected InventorDertimRepository repository;
	 
	@Override
	public boolean authorise(final Request<Dertim> request) {
		assert request != null;
		
		boolean result;
		
		result = request.getPrincipal().hasRole(Inventor.class);
		
		return result;
	}

	@Override
	public void bind(final Request<Dertim> request, final Dertim entity, final Errors errors) {
		assert request != null;
        assert entity != null;
        assert errors != null;
        
        request.bind(entity, errors, "code", "subject", "summary", "initialPeriod", "endPeriod", "provision", "additionalInfo");
        
        final String itemCode = request.getModel().getString("items");
        
        final Item item = this.repository.findOneItemByCode(itemCode);
        entity.setItem(item);

	}
	
	@Override
	public void validate(final Request<Dertim> request, final Dertim entity, final Errors errors) {
		assert request != null;
        assert entity != null;
        assert errors != null;
        
        if(!errors.hasErrors("code")) {
            final Date d = entity.getCreationMoment();
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            
            final String fecha = entity.getCode();
            final Integer dia = Integer.parseInt(fecha.substring(4,6));
            final Integer mes = Integer.parseInt(fecha.substring(2,4));
            final Integer anyo = Integer.parseInt(fecha.substring(0,2));
            
            final String year = String.valueOf(calendar.get(Calendar.YEAR));
            final char[] digitsYear = year.toCharArray();
            final String ten = digitsYear[2] + "0";
            final String one = digitsYear[0] + "";
            final Integer yearTwoDigits = Integer.parseInt(ten) + Integer.parseInt(one);
            
            final Integer month = calendar.get(Calendar.MONTH) + 1;
            final Integer day = calendar.get(Calendar.DAY_OF_MONTH);

            final Boolean result = (dia.equals(day)) && (mes.equals(month)) && (anyo.equals(yearTwoDigits));
            
            errors.state(request, result, "code", "inventor.dertim.form.error.code-date");
        }
        
        if(!errors.hasErrors("code")) {
        	errors.state(request, this.repository.findDertimByCode(entity.getCode()) == null, "code", "inventor.dertim.form.error.not-unique");
        }
		
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
        
        if(!errors.hasErrors("items")) {
            final String itemCode = request.getModel().getString("items");
            final Item item = this.repository.findOneItemByCode(itemCode);
        	if (itemCode.equals("No components available")) {
            	errors.state(request, item != null, "items", "inventor.dertim.form.error.item-not-available");
            }
        }
        
        if(!errors.hasErrors("items")) {
            final String itemCode = request.getModel().getString("items");
            final Item item = this.repository.findOneItemByCode(itemCode);
            errors.state(request, !itemCode.equals(""), "items", "inventor.dertim.form.error.item-null");
            if (!itemCode.equals("")) {
                errors.state(request, item != null, "items", "inventor.dertim.form.error.item-does-not-exist");
            }
        }
        
	}

	@Override
	public void unbind(final Request<Dertim> request, final Dertim entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;
		
		final String username = request.getPrincipal().getUsername();
		
		final List<String> possible = this.repository.findAllPossibleItemCodes(username);
		final List<String> taken = this.repository.findAllTakenItemCodes(username);
		final List<String> itemList = new ArrayList<>();

		for(final String code : possible) {
			if(!taken.contains(code))
				itemList.add(code);
		}

		if(itemList.isEmpty()) {
			itemList.add("No Dertims available");
		}

		model.setAttribute("items", itemList);
		
		request.unbind(entity, model, "code", "subject", "summary", "initialPeriod", "endPeriod", "provision", "additionalInfo");
	}
	
	@Override
	public Dertim instantiate(final Request<Dertim> request) {
		assert request != null;
		
		Dertim result;
		result = new Dertim();
		
		Date moment;
		moment = new Date(System.currentTimeMillis() - 1);
		result.setCreationMoment(moment);	
        
		return result;
	}
	
	@Override
	public void create(final Request<Dertim> request, final Dertim entity) {
		assert request != null;
        assert entity != null;
        
        this.repository.save(entity);
	}
	
}
