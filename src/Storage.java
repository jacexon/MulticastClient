import java.util.ArrayList;
import java.util.List;

public class Storage {
    public List<String> store = new ArrayList<>();

    public Storage(){
        System.out.println("Utworzono bazę");
    }

    public void addToStore(String message){
        store.add(message);
    }

    public List<String> getStore(){
        return store;
    }
}
