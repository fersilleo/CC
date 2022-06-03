package acme.features.inventor.dertim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.dertims.Dertim;
import acme.framework.components.models.Model;
import acme.framework.controllers.Request;
import acme.framework.entities.Principal;
import acme.framework.services.AbstractListService;
import acme.roles.Inventor;

@Service
public class InventorDertimListMineService implements AbstractListService<Inventor, Dertim>{
	
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
	public Collection<Dertim> findMany(final Request<Dertim> request) {
		assert request != null;
		
		Collection<Dertim> result;
		Principal principal;

		principal = request.getPrincipal(); 
        result = this.repository.findMineDertim(principal.getUsername());
		return result;
	}

	@Override
	public void unbind(final Request<Dertim> request, final Dertim entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;
		
		request.unbind(entity, model, "code", "subject");
		
		final String item = entity.getItem().getCode();
		model.setAttribute("item", item);
		
	}
	
}
