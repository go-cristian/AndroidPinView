package co.iyubinest.pinview;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import co.iyubinest.pin.PinEditView;
import co.iyubinest.pin.ReadyListener;

public class MainActivity extends Activity {

  private EditText usernameView;
  private PinEditView pinView;
  private Button button;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    usernameView = findViewById(R.id.username);
    pinView = findViewById(R.id.pin);
    button = findViewById(R.id.button);
    pinView.setReadyListener(new ReadyListener() {
      @Override public void onReady(boolean ready) {
        button.setEnabled(ready);
      }
    });
  }

  public void validate(View view) {
    if (pinView.value().equals("123456") && usernameView.getText().toString().equals("Android")) {
      Toast.makeText(this, "valid", Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(this, "try again", Toast.LENGTH_SHORT).show();
    }
  }
}
