package com.bunny.entertainment.yousee.player;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.bunny.entertainment.yousee.R;

public class BrightnessDialog extends AppCompatDialogFragment {

    TextView bright_no;
    ImageView cross;
    SeekBar seekBar;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.brt_dialog_item, null);
        builder.setView(view);

        cross = view.findViewById(R.id.brt_close);
        bright_no = view.findViewById(R.id.brt_number);
        seekBar = view.findViewById(R.id.brt_seekbar);
        int brightness = Settings.System.getInt(requireContext().getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, 0);
        String brightnessText = brightness + "";
        bright_no.setText(brightnessText);
        seekBar.setProgress(brightness);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Context context = requireContext().getApplicationContext();
                boolean canWrite = Settings.System.canWrite(context);
                if (canWrite) {
                    int sBrightness = progress * 255 / 255;
                    String sBrightnessText = sBrightness + "";
                    bright_no.setText(sBrightnessText);
                    Settings.System.putInt(context.getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS_MODE,
                            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                    Settings.System.putInt(context.getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS, sBrightness);
                } else {
                    Toast.makeText(context, "Enable write settings for brightness control", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                    startActivityForResult(intent, 0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return builder.create();
    }
}
