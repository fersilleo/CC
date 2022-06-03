package acme.features.inventor.dertim;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.dertims.Dertim;
import acme.framework.components.models.Model;
import acme.framework.controllers.Request;
import acme.framework.services.AbstractShowService;
import acme.roles.Inventor;

@Service
public class InventorDertimShowService implements AbstractShowService<Inventor, Dertim>{
	
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
	public Dertim findOne(final Request<Dertim> request) {
		assert request != null;
		
		Dertim result;
		int id;
		
		id = request.getModel().getInteger("id");
		result = this.repository.findOneDertim(id);
		
		return result;
	}

	@Override
	public void unbind(final Request<Dertim> request, final Dertim entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;
		
		request.unbind(entity, model, "code", "creationMoment", "subject", "summary", "initialPeriod", "endPeriod", "provision", "additionalInfo");
		
		final String item = entity.getItem().getCode();
		model.setAttribute("item", item);
		
	}
	
}
