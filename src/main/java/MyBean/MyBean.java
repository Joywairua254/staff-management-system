import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named("myBean")
@RequestScoped
public class MyBean {
    private String message;

    public void showMessage() {
        message = "Welcome to Java";
    }

    public String getMessage() {
        return message;
    }
}
