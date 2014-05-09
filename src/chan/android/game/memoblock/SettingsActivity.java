package chan.android.game.memoblock;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.Arrays;
import java.util.List;

public class SettingsActivity extends Activity {

    private CheckBox checkBoxSound;

    private Spinner spinnerBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        spinnerBackground = (Spinner) findViewById(R.id.setting_$_spinner_difficulty);
        final CustomArrayAdapter adapter = new CustomArrayAdapter(this, Difficulty.values());
        spinnerBackground.setAdapter(adapter);
        spinnerBackground.setSelection(adapter.indexOf(GameSettings.getDifficulty()));
        spinnerBackground.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                GameSettings.persistDifficulty(adapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Ignore
            }
        });

        checkBoxSound = (CheckBox) findViewById(R.id.setting_$_checkbox_sound);
        checkBoxSound.setChecked(GameSettings.isSoundEnabled());
    }

    @Override
    public void onBackPressed() {
        GameSettings.persistEnableSound(checkBoxSound.isChecked());
        super.onBackPressed();
    }

    private static class CustomArrayAdapter extends BaseAdapter {

        final List<Difficulty> levels;
        final Context context;

        public CustomArrayAdapter(Context context, Difficulty[] levels) {
            this.context = context;
            this.levels = Arrays.asList(levels);
        }

        @Override
        public int getCount() {
            return levels.size();
        }

        @Override
        public Difficulty getItem(int position) {
            return levels.get(position);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public int indexOf(Difficulty difficulty) {
            for (int i = 0; i < levels.size(); ++i) {
                if (levels.get(i) == difficulty) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.diffcult_row, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.name.setText(levels.get(position).toString());
            return convertView;
        }

        static class ViewHolder {
            final TextView name;

            public ViewHolder(View v) {
                name = (TextView) v.findViewById(R.id.difficult_row_$_textview_level);
            }
        }
    }
}
