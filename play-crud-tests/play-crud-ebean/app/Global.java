import com.avaje.ebean.Ebean;
import fr.njin.play.crud.controllers.ClasspathScannerControllerRegistry;
import fr.njin.play.crud.controllers.ControllersManager;
import fr.njin.play.crud.ebean.EbeanDataProviderFactory;
import fr.njin.play.crud.ebean.EbeanModelRegistry;
import models.User;
import play.Application;
import play.GlobalSettings;
import play.libs.Yaml;

import java.util.List;
import java.util.Map;

public class Global extends GlobalSettings {

    @Override
    @SuppressWarnings("unchecked")
    public void onStart(Application application) {
        super.onStart(application);

        ControllersManager.init(new EbeanModelRegistry(application),
                new ClasspathScannerControllerRegistry(application),
                new EbeanDataProviderFactory());

        if(Ebean.find(User.class).findRowCount() == 0) {

            Map<String,List<Object>> all = (Map<String,List<Object>>) Yaml.load("fixture.yml");

            // Insert users first
            Ebean.save(all.get("users"));

            // Insert projects
            Ebean.save(all.get("projects"));
            for(Object project: all.get("projects")) {
                // Insert the project/user relation
                Ebean.saveManyToManyAssociations(project, "members");
            }

            // Insert tasks
            Ebean.save(all.get("tasks"));
        }
    }


    @Override
	@SuppressWarnings("unchecked")
	public <A> A getControllerInstance(Class<A> clazz) throws Exception {
		if(clazz.equals(ControllersManager.class)) {
            ControllersManager manager = ControllersManager.getInstance();
            return (A)manager;
		}
		return super.getControllerInstance(clazz);
	}
}