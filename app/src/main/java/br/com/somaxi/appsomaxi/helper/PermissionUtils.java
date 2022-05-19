package br.com.somaxi.appsomaxi.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class PermissionUtils {

    /**
     * Solicita as permissões
     */
    public static boolean validate(Activity activity, int requestCode, String... permissions) {
        List<String> list = new ArrayList<String>();
        for (String permission : permissions) {
            // Valida permissões
            boolean ok = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
            if (! ok ) {
                list.add(permission);
            }
        }
        if (list.isEmpty()) {
            // Tudo ok, retorna true
            return true;
        }

        // Lista de permissões que falta acesso.
        String[] newPermissions = new String[list.size()];
        list.toArray(newPermissions);

        // Solicita permissão
        ActivityCompat.requestPermissions(activity, newPermissions, 1);

        return false;
    }
}
