package acme.features.inventor.dertim;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import acme.entities.dertims.Dertim;
import acme.framework.controllers.AbstractController;
import acme.roles.Inventor;

@Controller
public class InventorDertimController extends AbstractController<Inventor, Dertim>{
	
	  @Autowired
	  protected InventorDertimListMineService    listMineService;
	  
	  @Autowired
	  protected InventorDertimShowService    showService;
	  
	  @Autowired
	  protected InventorDertimCreateService createService;
	  
	  @Autowired
	  protected InventorDertimUpdateService updateService;
	  
	  @Autowired
	  protected InventorDertimDeleteService deleteService;
	  
	  @PostConstruct
	  protected void initialise() {
	        super.addCommand("list-mine", "list", this.listMineService);
	        super.addCommand("show", this.showService);
	        super.addCommand("create", this.createService);
	        super.addCommand("update", this.updateService);
	        super.addCommand("delete", this.deleteService);
	    }

}
