package pupusas.app.pupusasya;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONObject;
import com.loopj.android.http.*;
import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private TextView m;
    DrawerLayout drawer;
    private EditText usuario;
    private EditText clave;
    private String user, pasw, url, resultado, n, a;
    private boolean status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usuario = findViewById(R.id.etUsuario);
        clave = findViewById(R.id.etPassword);

    }


    public void LogIn(View view) {

        if (usuario.getText().toString().isEmpty()){
            mError("No ha introducido el nombre de usuario.");
        }
        else if (clave.getText().toString().isEmpty()){
            mError("No ha introducido su contraseña.");
        }
        else {
            try {
                if(verifyConexion() == true)initLogIn();
                if(verifyConexion() == false) mError("Ups... parece que no tienes conexión a internet");
            } catch (Exception e) { mError("Ups... Parece que hubo un problema, vuelve a intentarlo.");}
        }
    }

    public void SignUp(View view) {
       // Intent open = new Intent(LoginActivity.this, SignUp.class);
        //LoginActivity.this.startActivity(open);
    }

    private boolean verifyConexion(){
        boolean r = false;

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) r = true;
        return r;
    }

    private void mError(String mensaje){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Aviso").setMessage(mensaje)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void initLogIn(){
        user = usuario.getText().toString();
        pasw = clave.getText().toString();
        AsyncHttpClient client = new AsyncHttpClient();
        url = "https://pupusasapp.000webhostapp.com/LogIn.php";
        RequestParams parametros = new RequestParams();
        parametros.put("usu", user);
        parametros.put("pas", pasw);
        client.post(url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    try {
                        String respuesta = new String(responseBody);

                        JSONObject json = new JSONObject(respuesta);
                        if (json.names().get(0).equals("exito")){
                            a = json.getString("Apellido");
                            n = json.getString("Nombre");
                            status = true;
                        }
                        else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setTitle("Aviso").setMessage("Usuario o contraseña incorrectos.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                            status = false;
                        }

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure (int statusCode, Header[] headers, byte[] responseBody, Throwable error){

            }
        });

        if (status == true){
            Intent openMain = new Intent(LoginActivity.this, MainActivity.class);
            openMain.putExtra("n", n);
            openMain.putExtra("a", a);
            LoginActivity.this.startActivity(openMain);
            finish();
            usuario.setText("");
            clave.setText("");
        }
    }
}
