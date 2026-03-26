package my.utar.p5_irene_chow_ooi_ling_2204382;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PasswordAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<PasswordModel> passwordList;

    public PasswordAdapter(Context context, ArrayList<PasswordModel> passwordList) {
        this.context = context;
        this.passwordList = passwordList;
    }

    @Override
    public int getCount() {
        return passwordList.size();
    }

    @Override
    public Object getItem(int position) {
        return passwordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return passwordList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_password, parent, false);
        }

        TextView tvSiteName = convertView.findViewById(R.id.tvSiteName);
        TextView tvUsername = convertView.findViewById(R.id.tvUsername);
        TextView tvPassword = convertView.findViewById(R.id.tvPassword);

        PasswordModel pm = passwordList.get(position);

        tvSiteName.setText(pm.getSiteName());
        tvUsername.setText("Username: " + pm.getUsername());
        String extraInfo = "";
        if (!pm.getPinNumber().isEmpty() || !pm.getSecurityQuestion().isEmpty() || !pm.getNotes().isEmpty()) {
            extraInfo = "  •  Extra Info Saved";
        }
        tvPassword.setText("Password: ********" + extraInfo);

        return convertView;
    }
}